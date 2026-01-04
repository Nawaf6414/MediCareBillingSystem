package utility;

import java.util.*;

/**
 * PatientBillCollection - Utility class using Collections
 * Uses ArrayList and HashMap for bill management
 */
public class PatientBillCollection {
    
    // Inner class to represent a bill
    public static class PatientBill {
        public int patientId;
        public String patientName;
        public String visitDate;
        public double billAmount;
        
        public PatientBill(int patientId, String patientName, String visitDate, double billAmount) {
            this.patientId = patientId;
            this.patientName = patientName;
            this.visitDate = visitDate;
            this.billAmount = billAmount;
        }
        
        @Override
        public String toString() {
            return "ID: " + patientId + " | Name: " + patientName + 
                   " | Date: " + visitDate + " | Amount: OMR " + 
                   String.format("%.2f", billAmount);
        }
    }
    
    // ArrayList to store bills
    private List<PatientBill> billList;
    
    // HashMap to store bills by patient ID for quick access
    private Map<Integer, List<PatientBill>> billMap;
    
    // Constructor
    public PatientBillCollection() {
        this.billList = new ArrayList<>();
        this.billMap = new HashMap<>();
    }
    
    /**
     * i. Add element via keyboard
     * Adds a new bill to the collection
     */
    public void addBill(Scanner scanner) {
        System.out.println("\n--- Add New Bill ---");
        
        System.out.print("Enter Patient ID: ");
        int patientId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        System.out.print("Enter Patient Name: ");
        String patientName = scanner.nextLine();
        
        System.out.print("Enter Visit Date (YYYY-MM-DD): ");
        String visitDate = scanner.nextLine();
        
        System.out.print("Enter Bill Amount (OMR): ");
        double billAmount = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        
        // Create new bill object
        PatientBill bill = new PatientBill(patientId, patientName, visitDate, billAmount);
        
        // Add to ArrayList
        billList.add(bill);
        
        // Add to HashMap
        if (!billMap.containsKey(patientId)) {
            billMap.put(patientId, new ArrayList<>());
        }
        billMap.get(patientId).add(bill);
        
        System.out.println("✓ Bill added successfully");
    }
    
    /**
     * Display bill(s) based on patient ID
     */
    public void displayBill(Scanner scanner) {
        System.out.println("\n--- Display Bill ---");
        System.out.print("Enter Patient ID: ");
        int patientId = scanner.nextInt();
        scanner.nextLine();
        
        if (billMap.containsKey(patientId)) {
            System.out.println("\nBills for Patient ID " + patientId + ":");
            System.out.println("=====================================");
            for (PatientBill bill : billMap.get(patientId)) {
                System.out.println(bill);
            }
            System.out.println("=====================================");
        } else {
            System.out.println("✗ No bills found for Patient ID: " + patientId);
        }
    }
    
    /**
     * iii. Remove element based on scenario
     * Removes all bills for a specific patient
     */
    public void removeBill(Scanner scanner) {
        System.out.println("\n--- Remove Bill ---");
        System.out.print("Enter Patient ID to remove all bills: ");
        int patientId = scanner.nextInt();
        scanner.nextLine();
        
        if (billMap.containsKey(patientId)) {
            // Remove from HashMap
            List<PatientBill> billsToRemove = billMap.get(patientId);
            
            // Remove from ArrayList
            billList.removeAll(billsToRemove);
            
            // Remove from HashMap
            billMap.remove(patientId);
            
            System.out.println("✓ All bills for Patient ID " + patientId + " removed successfully");
        } else {
            System.out.println("✗ No bills found for Patient ID: " + patientId);
        }
    }
    
    /**
     * iv. Iterate through the Utility class
     * Display all bills with enhanced formatting
     */
    public void displayAllBills() {
        System.out.println("\n--- All Bills in System ---");
        if (billList.isEmpty()) {
            System.out.println("✗ No bills in the system");
            return;
        }
        
        System.out.println("=====================================");
        double totalBills = 0;
        for (PatientBill bill : billList) {
            System.out.println(bill);
            totalBills += bill.billAmount;
        }
        System.out.println("=====================================");
        System.out.println("Total Bills: " + billList.size());
        System.out.println("Total Amount: OMR " + String.format("%.2f", totalBills));
        System.out.println("=====================================");
    }
    
    /**
     * Display menu and handle user operations
     */
    public void menu(Scanner scanner) {
        while (true) {
            System.out.println("\n====== PATIENT BILL COLLECTION MENU ======");
            System.out.println("1. Add Bill");
            System.out.println("2. Display Bill (by Patient ID)");
            System.out.println("3. Remove Bill (by Patient ID)");
            System.out.println("4. Display All Bills");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    addBill(scanner);
                    break;
                case 2:
                    displayBill(scanner);
                    break;
                case 3:
                    removeBill(scanner);
                    break;
                case 4:
                    displayAllBills();
                    break;
                case 5:
                    System.out.println("Thank you for using the system!");
                    return;
                default:
                    System.out.println("✗ Invalid choice. Please try again");
            }
        }
    }
}