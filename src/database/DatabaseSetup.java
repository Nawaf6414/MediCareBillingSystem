package database;

import java.sql.*;

public class DatabaseSetup {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/";
    static final String USER = "root";
    static final String PASSWORD = "";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Load JDBC Driver
            Class.forName(JDBC_DRIVER);
            
            // Create database connection
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            stmt = conn.createStatement();
            
            // Create Database
            String createDB = "CREATE DATABASE IF NOT EXISTS medicareBilling";
            stmt.executeUpdate(createDB);
            System.out.println("✓ Database 'medicareBilling' created successfully");
            
            // Close connection and reconnect to new database
            stmt.close();
            conn.close();
            
            // Reconnect to the new database
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/medicareBilling", 
                USER, PASSWORD
            );
            stmt = conn.createStatement();
            
            // Create Patient Table
            String createPatientTable = "CREATE TABLE IF NOT EXISTS Patient ("
                    + "patient_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "name VARCHAR(50) NOT NULL, "
                    + "age INT NOT NULL, "
                    + "insurance_plan VARCHAR(20) NOT NULL"
                    + ")";
            stmt.executeUpdate(createPatientTable);
            System.out.println("✓ Table 'Patient' created successfully");
            
            // Create PatientBill Table
            String createBillTable = "CREATE TABLE IF NOT EXISTS PatientBill ("
                    + "bill_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "patient_id INT NOT NULL, "
                    + "visit_date DATE NOT NULL, "
                    + "bill_amount DECIMAL(10, 2) NOT NULL, "
                    + "FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)"
                    + ")";
            stmt.executeUpdate(createBillTable);
            System.out.println("✓ Table 'PatientBill' created successfully");
            
            // Insert Sample Records
            String[] insertPatients = {
                "INSERT INTO Patient (name, age, insurance_plan) VALUES ('Ahmed Al-Balushi', 45, 'Premium')",
                "INSERT INTO Patient (name, age, insurance_plan) VALUES ('Fatima Al-Hinai', 38, 'Standard')",
                "INSERT INTO Patient (name, age, insurance_plan) VALUES ('Mohammed Al-Kalbani', 52, 'Basic')",
                "INSERT INTO Patient (name, age, insurance_plan) VALUES ('Layla Al-Ismaili', 29, 'Premium')",
                "INSERT INTO Patient (name, age, insurance_plan) VALUES ('Salem Al-Harthi', 61, 'Standard')"
            };
            
            for (String insert : insertPatients) {
                stmt.executeUpdate(insert);
            }
            System.out.println("✓ 5 sample patient records inserted");
            
            // Display inserted records
            System.out.println("\n=== PATIENT RECORDS ===");
            ResultSet rs = stmt.executeQuery("SELECT * FROM Patient");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("patient_id") 
                    + " | Name: " + rs.getString("name") 
                    + " | Age: " + rs.getInt("age") 
                    + " | Insurance: " + rs.getString("insurance_plan"));
            }
            
            System.out.println("\n✓ Database setup completed successfully!");
            
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}