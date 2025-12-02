package service;

import model.*;
import file.*;
import java.util.ArrayList;

public class StudentService {
    private String studentTp;

    public StudentService(String studentTp) {
        this.studentTp = studentTp;
    }

    // View all available slots (status = OPEN)
    public ArrayList<Slot> viewAvailableSlots() {
        return SlotFileManager.getAvailableSlots();
    }

    // Create a consultation request
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

    // View own requests
    public ArrayList<Request> viewOwnRequests() {
        return RequestFileManager.getRequestsByStudent(studentTp);
    }

    // Cancel request (only if PENDING or APPROVED)
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

    // View own appointments
    public ArrayList<Appointment> viewOwnAppointments() {
        return AppointmentFileManager.getAppointmentsByStudent(studentTp);
    }
}
