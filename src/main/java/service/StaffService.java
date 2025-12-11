package service;

import model.Request;
import model.Slot;
import model.Appointment;
import file.RequestFileManager;
import file.SlotFileManager;
import file.AppointmentFileManager;
import java.util.ArrayList;

public class StaffService {

    // View all pending requests
    public ArrayList<Request> viewPendingRequests() {
        return RequestFileManager.getPendingRequests();
    }

    // Approve request - creates an appointment and deletes the request
    public boolean approveRequest(String requestId) {
        Request request = RequestFileManager.findById(requestId);
        
        if (request == null) {
            return false;
        }

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            return false;
        }

        // Get slot details
        Slot slot = SlotFileManager.findById(request.getSlotId());
        if (slot == null) {
            System.out.println("Slot not found.");
            return false;
        }

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

        // Delete the slot since it's now booked
        SlotFileManager.delete(request.getSlotId());

        // Delete the request from requests.txt
        RequestFileManager.delete(requestId);
        return true;
    }

    // Reject/Cancel request with reason - creates cancelled appointment
    public boolean cancelRequest(String requestId, String cancelReason) {
        Request request = RequestFileManager.findById(requestId);
        
        if (request == null) {
            return false;
        }

        // Get slot details
        Slot slot = SlotFileManager.findById(request.getSlotId());
        if (slot == null) {
            return false;
        }

        // Release the slot back to OPEN
        SlotFileManager.updateStatus(request.getSlotId(), "OPEN");

        // Create cancelled appointment with cancel reason
        ArrayList<Appointment> allAppointments = AppointmentFileManager.loadAll();
        String appointmentId = "A" + (allAppointments.size() + 1);
        
        Appointment cancelledAppointment = new Appointment(
            appointmentId,
            requestId,
            request.getStudentTp(),
            request.getLecturerTp(),
            request.getSlotId(),
            slot.getDate(),
            slot.getStartTime(),
            "CANCELLED",
            cancelReason != null ? cancelReason : "Cancelled by staff"
        );
        
        AppointmentFileManager.appendOne(cancelledAppointment);

        // Delete the request from requests.txt
        RequestFileManager.delete(requestId);

        System.out.println("Request cancelled and appointment created with status CANCELLED.");
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
            SlotFileManager.updateStatus(appointment.getSlotId(), "OPEN");
        }

        System.out.println("Appointment cancelled successfully.");
        return true;
    }

    // Complete appointment
    public boolean completeAppointment(String appointmentId) {
        Appointment appointment = AppointmentFileManager.findById(appointmentId);
        
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return false;
        }

        appointment.setStatus("COMPLETED");
        AppointmentFileManager.update(appointment);

        // Delete the slot (completed appointments don't need slots anymore)
        SlotFileManager.delete(appointment.getSlotId());

        System.out.println("Appointment completed successfully.");
        return true;
    }
}
