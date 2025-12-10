package file;

import model.CancelledRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class CancelledRequestFileManager {
    private static final String FILE_PATH = "cancelled_requests.txt";

    // Load all cancelled requests from file
    public static ArrayList<CancelledRequest> loadAll() {
        ArrayList<CancelledRequest> cancelledRequests = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            return cancelledRequests;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    String requestId = parts[0];
                    String studentTp = parts[1];
                    String lecturerTp = parts[2];
                    String slotId = parts[3];
                    String originalReason = parts[4];
                    String cancellationReason = parts[5];
                    String cancelledBy = parts[6];
                    String cancelledDate = parts[7];
                    
                    CancelledRequest cancelledRequest = new CancelledRequest(
                        requestId, studentTp, lecturerTp, slotId, 
                        originalReason, cancellationReason, cancelledBy, cancelledDate
                    );
                    cancelledRequests.add(cancelledRequest);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Cancelled requests file not found: " + e.getMessage());
        }
        
        return cancelledRequests;
    }

    // Save all cancelled requests to file
    public static void saveAll(ArrayList<CancelledRequest> cancelledRequests) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (CancelledRequest cancelledRequest : cancelledRequests) {
                writer.println(cancelledRequest.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving cancelled requests: " + e.getMessage());
        }
    }

    // Append one cancelled request to file
    public static void appendOne(CancelledRequest cancelledRequest) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             PrintWriter writer = new PrintWriter(fw)) {
            writer.println(cancelledRequest.toString());
        } catch (IOException e) {
            System.err.println("Error appending cancelled request: " + e.getMessage());
        }
    }

    // Find cancelled request by ID
    public static CancelledRequest findById(String requestId) {
        ArrayList<CancelledRequest> cancelledRequests = loadAll();
        for (CancelledRequest cancelledRequest : cancelledRequests) {
            if (cancelledRequest.getRequestId().equals(requestId)) {
                return cancelledRequest;
            }
        }
        return null;
    }

    // Get cancelled requests by student TP
    public static ArrayList<CancelledRequest> getCancelledRequestsByStudent(String studentTp) {
        ArrayList<CancelledRequest> allCancelled = loadAll();
        ArrayList<CancelledRequest> studentCancelled = new ArrayList<>();
        for (CancelledRequest cancelledRequest : allCancelled) {
            if (cancelledRequest.getStudentTp().equals(studentTp)) {
                studentCancelled.add(cancelledRequest);
            }
        }
        return studentCancelled;
    }

    // Get cancelled requests by lecturer TP
    public static ArrayList<CancelledRequest> getCancelledRequestsByLecturer(String lecturerTp) {
        ArrayList<CancelledRequest> allCancelled = loadAll();
        ArrayList<CancelledRequest> lecturerCancelled = new ArrayList<>();
        for (CancelledRequest cancelledRequest : allCancelled) {
            if (cancelledRequest.getLecturerTp().equals(lecturerTp)) {
                lecturerCancelled.add(cancelledRequest);
            }
        }
        return lecturerCancelled;
    }

    // Get all cancelled requests (for staff/admin view)
    public static ArrayList<CancelledRequest> getAllCancelledRequests() {
        return loadAll();
    }
}
