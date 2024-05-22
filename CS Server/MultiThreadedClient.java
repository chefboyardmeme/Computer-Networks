import java.io.*;
import java.net.*;

public class MultiThreadedClient {
    public static void main(String[] args) {
        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);
        int numClients = Integer.parseInt(args[2]);

        // Prompt the user for their choice
        System.out.println("Choose a request: (1) Date and Time, (2) Uptime, (3) Memory Use, (4) Netstat, (5) Current Users, (6) Running Processes");
        System.out.print("Enter your choice (1-6): ");
        String choice = "";
        try {
            BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
            choice = consoleIn.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long totalTurnAroundTime = 0;

        for (int i = 0; i < numClients; i++) {
            ClientThread clientThread = new ClientThread(serverAddress, serverPort, choice);
            clientThread.start();
            try {
                clientThread.join();
                totalTurnAroundTime += clientThread.getTurnAroundTime();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        double averageTurnAroundTime = (double) totalTurnAroundTime / numClients;

        System.out.println("Total Turn-around Time: " + totalTurnAroundTime + " ms");
        System.out.println("Average Turn-around Time: " + averageTurnAroundTime + " ms");
    }
}

class ClientThread extends Thread {
    private String serverAddress;
    private int serverPort;
    private String choice;
    private long startTime;
    private long endTime;

    public ClientThread(String serverAddress, int serverPort, String choice) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.choice = choice;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to the server.");

            // Prompt the user for their choice only once
            out.println(choice);

            // Record the start time when the request is sent
            startTime = System.currentTimeMillis();

            // Read and display the server's response
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseBuilder.append(line).append("\n");
            }
            String response = responseBuilder.toString();
            System.out.println("Server response:\n" + response);

            // Record the end time when the response is received
            endTime = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getTurnAroundTime() {
        return endTime - startTime;
    }
}
