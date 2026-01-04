package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * ClientThread - Handles client-side operations for billing system
 * Extends Thread class to enable multithreading
 */
public class ClientThread extends Thread {
    private String serverHost;
    private int serverPort;
    
    // Constructor
    public ClientThread(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }
    
    /**
     * run() - Main execution method of the thread
     * Handles connection, data transmission, and response
     */
    @Override
    public void run() {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        Scanner scanner = new Scanner(System.in);
        
        try {
            // ii. Request connection to the server
            System.out.println("=== MediCare Billing System - Client ===");
            System.out.println("Connecting to server at " + serverHost + ":" + serverPort);
            socket = new Socket(serverHost, serverPort);
            System.out.println("✓ Connected to server successfully\n");
            
            // Initialize input/output streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // iii. Accept and forward the necessary data to the server
            System.out.println("--- Enter Patient Billing Information ---");
            
            // Input Patient ID
            int patientId = getValidInteger("Enter Patient ID: ", scanner);
            
            // Input Visit Date
            String visitDate = getValidDate("Enter Visit Date (YYYY-MM-DD): ", scanner);
            
            // Input Patient Type
            String patientType = getValidPatientType("Enter Patient Type (Outpatient/Inpatient/Emergency): ", scanner);
            
            // Input Service Code
            String serviceCode = getValidServiceCode("Enter Service Code (CONS100/LAB210/IMG330/US400/MRI700): ", scanner);
            
            // Send data to server
            String request = patientId + "," + visitDate + "," + patientType + "," + serviceCode;
            out.println(request);
            System.out.println("\n✓ Request sent to server: " + request);
            
            // iv. Receive the bill details and display
            System.out.println("\n--- Bill Details ---");
            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) {
                    break;
                }
                System.out.println(response);
            }
            
            System.out.println("\n✓ Bill calculation completed");
            
        } catch (SocketException e) {
            System.out.println("✗ Connection error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("✗ IO Error: " + e.getMessage());
        } finally {
            // v. Close the connection after use
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) {
                    socket.close();
                    System.out.println("\n✓ Connection closed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            scanner.close();
        }
    }
    
    /**
     * Input validation for Patient ID
     */
    private int getValidInteger(String prompt, Scanner scanner) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                } else {
                    System.out.println("✗ Please enter a positive number");
                }
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Please enter a valid number");
            }
        }
    }
    
    /**
     * Input validation for Visit Date
     */
    private String getValidDate(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String date = scanner.nextLine().trim();
            if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return date;
            } else {
                System.out.println("✗ Invalid date format. Use YYYY-MM-DD");
            }
        }
    }
    
    /**
     * Input validation for Patient Type
     */
    private String getValidPatientType(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String type = scanner.nextLine().trim();
            if (type.equalsIgnoreCase("Outpatient") || 
                type.equalsIgnoreCase("Inpatient") || 
                type.equalsIgnoreCase("Emergency")) {
                return type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
            } else {
                System.out.println("✗ Invalid type. Choose: Outpatient, Inpatient, or Emergency");
            }
        }
    }
    
    /**
     * Input validation for Service Code
     */
    private String getValidServiceCode(String prompt, Scanner scanner) {
        String[] validCodes = {"CONS100", "LAB210", "IMG330", "US400", "MRI700"};
        while (true) {
            System.out.print(prompt);
            String code = scanner.nextLine().trim().toUpperCase();
            for (String valid : validCodes) {
                if (code.equals(valid)) {
                    return code;
                }
            }
            System.out.println("✗ Invalid service code. Valid codes: CONS100, LAB210, IMG330, US400, MRI700");
        }
    }
}