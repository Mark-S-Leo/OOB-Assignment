package file;

import model.Request;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class RequestFileManager {
    private static final String FILE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "requests.txt";

    // Load all requests from file
    public static ArrayList<Request> loadAll() {
        ArrayList<Request> requests = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            return requests;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length >= 9) {
                    String requestId = parts[0];
                    String studentTp = parts[1];
                    String lecturerTp = parts[2];
                    String slotId = parts[3];
                    String date = parts[4];
                    String startTime = parts[5];
                    String endTime = parts[6];
                    String reason = parts[7];
                    String status = parts[8];
                    String cancelReason = parts.length >= 10 ? parts[9] : "";
                    
                    Request request = new Request(requestId, studentTp, lecturerTp, slotId, 
                                                 date, startTime, endTime, reason, status, cancelReason);
                    requests.add(request);
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, return empty list
        }
        
        return requests;
    }

    // Save all requests to file
    public static void saveAll(ArrayList<Request> requests) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Request request : requests) {
                writer.println(request.toString());
            }
        } catch (IOException e) {
            // Error saving, silent fail
        }
    }

    // Append one request to file
    public static void appendOne(Request request) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             PrintWriter writer = new PrintWriter(fw)) {
            writer.println(request.toString());
        } catch (IOException e) {
            // Error appending, silent fail
        }
    }

    // Find request by ID
    public static Request findById(String requestId) {
        ArrayList<Request> requests = loadAll();
        for (Request request : requests) {
            if (request.getRequestId().equals(requestId)) {
                return request;
            }
        }
        return null;
    }

    // Update request
    public static boolean update(Request updatedRequest) {
        ArrayList<Request> requests = loadAll();
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getRequestId().equals(updatedRequest.getRequestId())) {
                requests.set(i, updatedRequest);
                saveAll(requests);
                return true;
            }
        }
        return false;
    }

    // Delete request by ID
    public static boolean delete(String requestId) {
        ArrayList<Request> requests = loadAll();
        boolean removed = requests.removeIf(request -> request.getRequestId().equals(requestId));
        if (removed) {
            saveAll(requests);
        }
        return removed;
    }

    // Get requests by student TP
    public static ArrayList<Request> getRequestsByStudent(String studentTp) {
        ArrayList<Request> allRequests = loadAll();
        ArrayList<Request> studentRequests = new ArrayList<>();
        for (Request request : allRequests) {
            if (request.getStudentTp().equals(studentTp)) {
                studentRequests.add(request);
            }
        }
        return studentRequests;
    }

    // Get pending requests
    public static ArrayList<Request> getPendingRequests() {
        ArrayList<Request> allRequests = loadAll();
        ArrayList<Request> pendingRequests = new ArrayList<>();
        for (Request request : allRequests) {
            if ("PENDING".equalsIgnoreCase(request.getStatus())) {
                pendingRequests.add(request);
            }
        }
        return pendingRequests;
    }
    
    /**
     * Cancel a request and move it to cancelled_requests.txt
     * @param requestId The request ID to cancel
     * @param reason Cancellation reason
     * @return true if cancelled successfully, false if request not found or already processed
     */
    public static boolean cancelRequest(String requestId, String reason) {
        Request request = findById(requestId);
        if (request == null) {
            return false;
        }
        
        // Only allow cancellation of pending requests
        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            return false;
        }
        
        // Release the slot back to OPEN status
        SlotFileManager.updateStatus(request.getSlotId(), "OPEN");
        
        // Update request status and cancel reason
        request.setStatus("CANCELLED");
        request.setCancelReason(reason);
        
        // Update the request in file (or just delete it)
        // For now, we'll just delete cancelled requests
        return delete(requestId);
    }
}
