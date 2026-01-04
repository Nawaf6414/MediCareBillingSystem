package server;

import java.io.*;
import java.net.*;

/**
 * BillingServer - Main server class
 * Listens for client connections and spawns threads
 * ix. Create a main class, instantiate Thread object, and start the Thread
 */
public class BillingServer {
    private static final int PORT = 5000;
    
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("=== MediCare Billing Server ===");
            System.out.println("Server started on port " + PORT);
            System.out.println("Waiting for client connections...\n");
            
            while (true) {
                // Accept client connection
                Socket clientSocket = serverSocket.accept();
                
                // Create new thread for each client
                BillingServerThread serverThread = new BillingServerThread(clientSocket);
                serverThread.start();
            }
            
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}