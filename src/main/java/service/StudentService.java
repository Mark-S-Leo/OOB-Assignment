package service;

import model.*;
import file.*;
import java.util.ArrayList;

public class StudentService {
    private final String studentTp;

    public StudentService(String studentTp) {
        this.studentTp = studentTp;
    }

    /**
     * Retrieves all available consultation slots (status = OPEN)
     * @return List of available slots
     */
    public ArrayList<Slot> viewAvailableSlots() {
        return SlotFileManager.getAvailableSlots();
    }

    /**
     * Creates a consultation request for a specific slot
     * @param lecturerTp The lecturer's TP number
     * @param slotId The ID of the requested slot
     * @param reason The reason for consultation
     * @return true if request created successfully, false otherwise
     */
    public boolean createConsultationRequest(String lecturerTp, String slotId, String reason) {
        // Check if slot exists and is open
        Slot slot = SlotFileManager.findById(slotId);
        if (slot == null || !"OPEN".equalsIgnoreCase(slot.getStatus())) {
            System.out.println("Slot not available.");
            return false;
        }

        // Generate request ID
        ArrayList<Request> allRequests = RequestFileManager.loadAll();
        String requestId = "R" + (allRequests.size() + 1);

        // Create new request
        Request newRequest = new Request(requestId, studentTp, lecturerTp, slotId, reason, "PENDING");
        RequestFileManager.appendOne(newRequest);

        System.out.println("Request created successfully: " + requestId);
        return true;
    }

    /**
     * Retrieves all consultation requests made by this student
     * @return List of requests belonging to this student
     */
    public ArrayList<Request> viewOwnRequests() {
        return RequestFileManager.getRequestsByStudent(studentTp);
    }

    /**
     * Cancels a pending or approved consultation request
     * @param requestId The ID of the request to cancel
     * @return true if cancelled successfully, false otherwise
     */
    public boolean cancelRequest(String requestId) {
        Request request = RequestFileManager.findById(requestId);
        
        if (request == null) {
            System.out.println("Request not found.");
            return false;
        }

        if (!request.getStudentTp().equals(studentTp)) {
            System.out.println("You can only cancel your own requests.");
            return false;
        }

        String status = request.getStatus().toUpperCase();
        if (!"PENDING".equals(status) && !"APPROVED".equals(status)) {
            System.out.println("Can only cancel PENDING or APPROVED requests.");
            return false;
        }

        request.setStatus("CANCELLED");
        RequestFileManager.update(request);
        System.out.println("Request cancelled successfully.");
        return true;
    }

    /**
     * Retrieves all approved appointments for this student
     * @return List of appointments for this student
     */
    public ArrayList<Appointment> viewOwnAppointments() {
        return AppointmentFileManager.getAppointmentsByStudent(studentTp);
    }
}
