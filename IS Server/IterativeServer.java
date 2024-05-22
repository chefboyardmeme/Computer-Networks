import java.io.*;
import java.net.*;
import java.util.Date;

public class IterativeServer 
{
    public static void main(String[] args) 
    {
    	
	        int port = Integer.parseInt(args[0]);
	        
	        try (ServerSocket serverSocket = new ServerSocket(port)) 
	        {
	            System.out.println("Server is listening for incoming connections on port:" + port);
	           
	            while (true) 
	            {
	                Socket clientSocket = serverSocket.accept();
	                System.out.println("Accepted connection from " + clientSocket.getInetAddress());
	                
	                try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true) ) 
	                {
	                	out.println("Choose a request: (1) Date and Time, (2) Uptime, (3) Memory Use, (4) Netstat, (5) Current Users, (6) Running Processes");
	                    String choice = in.readLine();

	                    String response = processRequest(Integer.parseInt(choice));
	                    out.println(response);
	                } 
	                catch (IOException e) 
	                {
	                    e.printStackTrace();
	                } 
	                finally
	                {
	                    clientSocket.close();
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		} 
    
    
    private static String processRequest(int choice) {
        switch (choice) {
            case 1:
                return getCurrentDateAndTime();
            case 2:
                return getUptime();
            case 3:
                return getMemoryUsage();
            case 4:
                return getNetworkConnections();
            case 5:
                return getCurrentUsers();
            case 6:
                return getRunningProcesses();
            default:
                return "Unknown request";
        }
    }

    private static String getCurrentDateAndTime() {
        Date date = new Date();
        return "Date and Time: " + date.toString();
    }

    private static String getUptime() {
        ProcessBuilder processBuilder = new ProcessBuilder("uptime");

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String uptimeInfo = "";

            while ((line = reader.readLine()) != null) {
                uptimeInfo = line.trim(); // Store the last line, which contains uptime
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // Extract and format the uptime part only
                uptimeInfo = extractUptime(uptimeInfo);
                if (uptimeInfo != null) {
                    return "Uptime Information: " + uptimeInfo;
                } else {
                    return "Uptime information format not recognized.";
                }
            } else {
                return "Failed to retrieve uptime information.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error while fetching uptime information.";
        }
    }

    private static String extractUptime(String rawUptime) {
        // Example uptime formats:
        // "14:17:56 up 302 days, 17:29"
        // "14:17:56 up 2 min,  3 users, load average: 0.00, 0.00, 0.00"
        
        // Split by ',' and take the first part
        String[] parts = rawUptime.split(",");
        if (parts.length >= 1) {
            return parts[0];
        }

        return null; // Format not recognized
    }

    private static String getMemoryUsage() {
    	 ProcessBuilder processBuilder = new ProcessBuilder("free");

    	    try {
    	        Process process = processBuilder.start();
    	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    	        String line;
    	        StringBuilder memoryInfo = new StringBuilder();

    	        while ((line = reader.readLine()) != null) {
    	            memoryInfo.append(line).append("\n");
    	        	
    	        }

    	        int exitCode = process.waitFor();
    	        if (exitCode == 0) {
    	            return "Memory Usage Information:\n"+memoryInfo.toString();
    	        } else {
    	            return "Failed to retrieve memory usage information.";
    	        }
    	    } catch (IOException | InterruptedException e) {
    	        e.printStackTrace();
    	        return "Error while fetching memory usage information.";
    	    }
    }

    private static String getNetworkConnections() {
    	ProcessBuilder processBuilder = new ProcessBuilder("netstat", "-tuln");

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder networkInfo = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                networkInfo.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "Network Connections Information:\n" + networkInfo.toString();
            } else {
                return "Failed to retrieve network connections information.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error while fetching network connections information.";
        }
    }

    private static String getCurrentUsers() {
    	ProcessBuilder processBuilder = new ProcessBuilder("who");

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder userInformation = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                userInformation.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "Current Users Information:\n" + userInformation.toString();
            } else {
                return "Failed to retrieve current users information.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error while fetching current users information.";
        }
    }

    private static String getRunningProcesses() {
    	ProcessBuilder processBuilder = new ProcessBuilder("ps", "aux");

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder processInfo = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                processInfo.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "Running Processes Information: \n" + processInfo.toString();
            } else {
                return "Failed to retrieve running processes information.";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error while fetching running processes information.";
        }
    }
}