package service;

import model.*;
import file.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LecturerService {
    private String lecturerTp;

    public LecturerService(String lecturerTp) {
        this.lecturerTp = lecturerTp;
    }

    // Create a consultation slot
    public boolean createSlot(String date, String startTime, String endTime) {
        // Check if date is not in the past
        try {
            LocalDate slotDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate today = LocalDate.now();
            
            if (slotDate.isBefore(today)) {
                System.out.println("Cannot create slot in the past.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return false;
        }

        // Generate slot ID
        ArrayList<Slot> allSlots = SlotFileManager.loadAll();
        String slotId = "S" + (allSlots.size() + 1);

        // Create new slot
        Slot newSlot = new Slot(slotId, lecturerTp, date, startTime, endTime, "OPEN");
        SlotFileManager.appendOne(newSlot);

        System.out.println("Slot created successfully: " + slotId);
        return true;
    }

    // View own slots
    public ArrayList<Slot> viewOwnSlots() {
        return SlotFileManager.getSlotsByLecturer(lecturerTp);
    }

    // View approved bookings (appointments)
    public ArrayList<Appointment> viewApprovedBookings() {
        return AppointmentFileManager.getAppointmentsByLecturer(lecturerTp);
    }

    // Cancel slot (can only cancel if OPEN)
    public boolean cancelSlot(String slotId) {
        Slot slot = SlotFileManager.findById(slotId);
        
        if (slot == null) {
            System.out.println("Slot not found.");
            return false;
        }

        if (!slot.getLecturerTp().equals(lecturerTp)) {
            System.out.println("You can only cancel your own slots.");
            return false;
        }

        if (!"OPEN".equalsIgnoreCase(slot.getStatus())) {
            System.out.println("Can only cancel OPEN slots.");
            return false;
        }

        slot.setStatus("CANCELLED");
        SlotFileManager.update(slot);
        System.out.println("Slot cancelled successfully.");
        return true;
    }

    // Update slot
    public boolean updateSlot(String slotId, String newDate, String newStartTime, String newEndTime) {
        Slot slot = SlotFileManager.findById(slotId);
        
        if (slot == null) {
            System.out.println("Slot not found.");
            return false;
        }

        if (!slot.getLecturerTp().equals(lecturerTp)) {
            System.out.println("You can only update your own slots.");
            return false;
        }

        slot.setDate(newDate);
        slot.setStartTime(newStartTime);
        slot.setEndTime(newEndTime);
        SlotFileManager.update(slot);
        
        System.out.println("Slot updated successfully.");
        return true;
    }
}
