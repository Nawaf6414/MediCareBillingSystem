import utility.PatientBillCollection;
import java.util.Scanner;

/**
 * Main class - Entry point for the Utility Class demonstration
 * Task 4: Menu-Driven Program using Collections
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("   MEDICARE BILLING SYSTEM - UTILITY    ║");
        System.out.println("         Collection Management  ");
        
        Scanner scanner = new Scanner(System.in);
        PatientBillCollection collection = new PatientBillCollection();
        
        // Demonstrate with sample data
        System.out.println("\n--- Loading Sample Bills ---");
        collection.addBill(scanner);
        
        // Show menu
        collection.menu(scanner);
        
        scanner.close();
    }
}