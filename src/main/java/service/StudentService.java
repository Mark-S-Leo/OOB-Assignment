package service;

import model.Request;
import model.Slot;
import model.Appointment;
import file.RequestFileManager;
import file.SlotFileManager;
import file.AppointmentFileManager;
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
            return false;
        }

        // Generate request ID
        ArrayList<Request> allRequests = RequestFileManager.loadAll();
        String requestId = "R" + (allRequests.size() + 1);

        // Create new request with slot date and time information
        Request newRequest = new Request(requestId, studentTp, lecturerTp, slotId, 
                                        slot.getDate(), slot.getStartTime(), slot.getEndTime(),
                                        reason, "PENDING");
        RequestFileManager.appendOne(newRequest);

        // Set slot to ON_HOLD
        SlotFileManager.updateStatus(slotId, "ON_HOLD");
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
     * Cancels a pending consultation request and creates cancelled appointment
     * @param requestId The ID of the request to cancel
     * @return true if cancelled successfully, false otherwise
     */
    public boolean cancelRequest(String requestId) {
        Request request = RequestFileManager.findById(requestId);
        
        if (request == null) {
            return false;
        }

        if (!request.getStudentTp().equals(studentTp)) {
            return false;
        }

        String status = request.getStatus().toUpperCase();
        if (!"PENDING".equals(status)) {
            return false;
        }

        // Get slot details
        Slot slot = SlotFileManager.findById(request.getSlotId());
        if (slot == null) {
            return false;
        }

        // Release the slot back to OPEN
        SlotFileManager.updateStatus(request.getSlotId(), "OPEN");

        // Create cancelled appointment
        ArrayList<Appointment> allAppointments = AppointmentFileManager.loadAll();
        String appointmentId = "A" + (allAppointments.size() + 1);
        
        Appointment cancelledAppointment = new Appointment(
            appointmentId,
            requestId,
            studentTp,
            request.getLecturerTp(),
            request.getSlotId(),
            slot.getDate(),
            slot.getStartTime(),
            "CANCELLED",
            "Cancelled by student"
        );
        
        AppointmentFileManager.appendOne(cancelledAppointment);

        // Delete the request from requests.txt
        RequestFileManager.delete(requestId);
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
