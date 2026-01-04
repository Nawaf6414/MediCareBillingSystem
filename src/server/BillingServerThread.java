package server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * BillingServerThread - Handles individual client connections
 * Extends Thread for multithreading support
 */
public class BillingServerThread extends Thread {
    private Socket clientSocket;
    
    // Database connection details
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/medicareBilling";
    static final String USER = "root";
    static final String PASSWORD = "";
    
    // Service pricing map
    private Map<String, Double> servicePrices;
    
    // Insurance discount map
    private Map<String, Double> insuranceDiscounts;
    
    // Insurance per-visit fee map
    private Map<String, Double> perVisitFees;
    
    // Patient type extra charge map
    private Map<String, Double> patientTypeCharges;
    
    // Constructor
    public BillingServerThread(Socket socket) {
        this.clientSocket = socket;
        initializeMaps();
    }
    
    /**
     * Initialize pricing and discount maps
     */
    private void initializeMaps() {
        // Service codes and prices
        servicePrices = new HashMap<>();
        servicePrices.put("CONS100", 12.00);
        servicePrices.put("LAB210", 8.50);
        servicePrices.put("IMG330", 25.00);
        servicePrices.put("US400", 35.00);
        servicePrices.put("MRI700", 180.00);
        
        // Insurance plan discounts
        insuranceDiscounts = new HashMap<>();
        insuranceDiscounts.put("Premium", 0.15);
        insuranceDiscounts.put("Standard", 0.10);
        insuranceDiscounts.put("Basic", 0.00);
        
        // Per-visit fees
        perVisitFees = new HashMap<>();
        perVisitFees.put("Premium", 5.00);
        perVisitFees.put("Standard", 8.00);
        perVisitFees.put("Basic", 10.00);
        
        // Patient type extra charges
        patientTypeCharges = new HashMap<>();
        patientTypeCharges.put("Outpatient", 0.00);
        patientTypeCharges.put("Inpatient", 0.05);
        patientTypeCharges.put("Emergency", 0.15);
    }
    
    /**
     * run() - Main execution method of the thread
     */
    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        Connection conn = null;
        
        try {
            // Initialize streams
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            System.out.println("\n[SERVER] New client connected: " + clientSocket.getInetAddress());
            
            // ii. Accept data from client
            String request = in.readLine();
            if (request == null) return;
            
            System.out.println("[SERVER] Received request: " + request);
            
            // Parse client data
            String[] parts = request.split(",");
            int patientId = Integer.parseInt(parts[0]);
            String visitDate = parts[1];
            String patientType = parts[2];
            String serviceCode = parts[3];
            
            // iii. Make connection to database
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("[SERVER] Database connection established");
            
            // iv. Retrieve insurance plan from database
            String insurancePlan = getInsurancePlan(conn, patientId);
            
            if (insurancePlan == null) {
                out.println("ERROR: Patient ID not found in database");
                out.println("END");
                return;
            }
            
            // v. Calculate bill amount
            double serviceAmount = servicePrices.get(serviceCode);
            double insuranceDiscount = insuranceDiscounts.get(insurancePlan) * serviceAmount;
            double discountedAmount = serviceAmount - insuranceDiscount;
            double perVisitFee = perVisitFees.get(insurancePlan);
            double subtotal = discountedAmount + perVisitFee;
            double extraCharge = subtotal * patientTypeCharges.get(patientType);
            double finalBillAmount = subtotal + extraCharge;
            
            // vi. Insert bill record into database
            insertBillRecord(conn, patientId, visitDate, finalBillAmount);
            
            // vii. Send results back to client
            out.println("=====================================");
            out.println("         PATIENT BILL DETAILS        ");
            out.println("=====================================");
            out.println("Patient ID: " + patientId);
            out.println("Visit Date: " + visitDate);
            out.println("Service Code: " + serviceCode);
            out.println("Patient Type: " + patientType);
            out.println("Insurance Plan: " + insurancePlan);
            out.println("-------------------------------------");
            out.println("Service Amount: OMR " + String.format("%.2f", serviceAmount));
            out.println("Insurance Discount (" + 
                (insuranceDiscounts.get(insurancePlan) * 100) + "%): -OMR " + 
                String.format("%.2f", insuranceDiscount));
            out.println("Discounted Amount: OMR " + String.format("%.2f", discountedAmount));
            out.println("Per-Visit Fee: OMR " + String.format("%.2f", perVisitFee));
            out.println("Subtotal: OMR " + String.format("%.2f", subtotal));
            out.println("Extra Charge (" + 
                (patientTypeCharges.get(patientType) * 100) + "%): OMR " + 
                String.format("%.2f", extraCharge));
            out.println("=====================================");
            out.println("FINAL BILL AMOUNT: OMR " + String.format("%.2f", finalBillAmount));
            out.println("=====================================");
            out.println("END");
            
            System.out.println("[SERVER] Bill calculated and sent to client. Final Amount: OMR " + 
                String.format("%.2f", finalBillAmount));
            
        } catch (ClassNotFoundException e) {
            System.out.println("[SERVER ERROR] JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[SERVER ERROR] Database error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[SERVER ERROR] IO Error: " + e.getMessage());
        } finally {
            // viii. Close database connection
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            // Close client connection
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
                System.out.println("[SERVER] Client connection closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Retrieve insurance plan from Patient table using patient ID
     */
    private String getInsurancePlan(Connection conn, int patientId) {
        String query = "SELECT insurance_plan FROM Patient WHERE patient_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("insurance_plan");
            }
        } catch (SQLException e) {
            System.out.println("[SERVER] Error retrieving insurance plan: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Insert bill record into PatientBill table
     */
    private void insertBillRecord(Connection conn, int patientId, String visitDate, double billAmount) {
        String query = "INSERT INTO PatientBill (patient_id, visit_date, bill_amount) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, patientId);
            pstmt.setString(2, visitDate);
            pstmt.setDouble(3, billAmount);
            pstmt.executeUpdate();
            System.out.println("[SERVER] Bill record inserted into database");
        } catch (SQLException e) {
            System.out.println("[SERVER] Error inserting bill record: " + e.getMessage());
        }
    }
}