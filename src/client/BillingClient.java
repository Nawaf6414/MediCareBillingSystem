package client;

/**
 * BillingClient - Main client class
 * vi. Create a main class, instantiate Thread object, and start the Thread
 */
public class BillingClient {
    public static void main(String[] args) {
        String serverHost = "localhost";
        int serverPort = 5000;
        
        // vi. Create thread and start it
        ClientThread clientThread = new ClientThread(serverHost, serverPort);
        clientThread.start();
    }
}