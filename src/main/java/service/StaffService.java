package service;

import model.*;
import file.*;
import java.util.ArrayList;

public class StaffService {

    // View all pending requests
    public ArrayList<Request> viewPendingRequests() {
        return RequestFileManager.getPendingRequests();
    }

    // Approve request - creates an appointment
    public boolean approveRequest(String requestId) {
        Request request = RequestFileManager.findById(requestId);
        
        if (request == null) {
            System.out.println("Request not found.");
            return false;
        }

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            System.out.println("Can only approve PENDING requests.");
            return false;
        }

        // Get slot details
        Slot slot = SlotFileManager.findById(request.getSlotId());
        if (slot == null) {
            System.out.println("Slot not found.");
            return false;
        }

        // Update request status
        request.setStatus("APPROVED");
        RequestFileManager.update(request);

        // Create appointment
        ArrayList<Appointment> allAppointments = AppointmentFileManager.loadAll();
        String appointmentId = "A" + (allAppointments.size() + 1);
        
        Appointment newAppointment = new Appointment(
            appointmentId,
            requestId,
            request.getStudentTp(),
            request.getLecturerTp(),
            request.getSlotId(),
            slot.getDate(),
            slot.getStartTime(),
            "SCHEDULED"
        );
        
        AppointmentFileManager.appendOne(newAppointment);

        // Update slot status to BOOKED
        slot.setStatus("BOOKED");
        SlotFileManager.update(slot);

        System.out.println("Request approved and appointment created: " + appointmentId);
        return true;
    }

    // Reject/Cancel request
    public boolean cancelRequest(String requestId) {
        Request request = RequestFileManager.findById(requestId);
        
        if (request == null) {
            System.out.println("Request not found.");
            return false;
        }

        request.setStatus("CANCELLED");
        RequestFileManager.update(request);
        System.out.println("Request cancelled successfully.");
        return true;
    }

    // View all appointments
    public ArrayList<Appointment> viewAllAppointments() {
        return AppointmentFileManager.loadAll();
    }

    // Update appointment (change date/time)
    public boolean updateAppointment(String appointmentId, String newDate, String newStartTime) {
        Appointment appointment = AppointmentFileManager.findById(appointmentId);
        
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        // Verify new date/time matches a lecturer slot
        Slot slot = SlotFileManager.findById(appointment.getSlotId());
        if (slot == null) {
            System.out.println("Slot not found.");
            return false;
        }

        // Check if new date and time match slot
        if (!slot.getDate().equals(newDate) || !slot.getStartTime().equals(newStartTime)) {
            System.out.println("New date/time must match lecturer's slot.");
            return false;
        }

        appointment.setDate(newDate);
        appointment.setStartTime(newStartTime);
        AppointmentFileManager.update(appointment);
        
        System.out.println("Appointment updated successfully.");
        return true;
    }

    // Cancel appointment
    public boolean cancelAppointment(String appointmentId) {
        Appointment appointment = AppointmentFileManager.findById(appointmentId);
        
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        appointment.setStatus("CANCELLED");
        AppointmentFileManager.update(appointment);

        // Free up the slot
        Slot slot = SlotFileManager.findById(appointment.getSlotId());
        if (slot != null) {
            slot.setStatus("OPEN");
            SlotFileManager.update(slot);
        }

        System.out.println("Appointment cancelled successfully.");
        return true;
    }
}
