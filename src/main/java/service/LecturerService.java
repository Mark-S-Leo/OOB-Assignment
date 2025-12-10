package service;

import model.*;
import file.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LecturerService {
    private final String lecturerTp;

    public LecturerService(String lecturerTp) {
        this.lecturerTp = lecturerTp;
    }

    /**
     * Creates a new consultation slot
     * Validates that the date is not in the past
     * @param date The date in YYYY-MM-DD format
     * @param startTime The start time in HH:mm format
     * @param endTime The end time in HH:mm format
     * @return true if slot created successfully, false otherwise
     */
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

    /**
     * Retrieves all slots created by this lecturer
     * @return List of slots belonging to this lecturer
     */
    public ArrayList<Slot> viewOwnSlots() {
        return SlotFileManager.getSlotsByLecturer(lecturerTp);
    }

    /**
     * Retrieves all approved appointments for this lecturer
     * @return List of appointments for this lecturer
     */
    public ArrayList<Appointment> viewApprovedBookings() {
        return AppointmentFileManager.getAppointmentsByLecturer(lecturerTp);
    }

    /**
     * Cancels an open consultation slot
     * Only OPEN slots can be cancelled
     * @param slotId The ID of the slot to cancel
     * @return true if cancelled successfully, false otherwise
     */
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

    /**
     * Updates an existing consultation slot
     * @param slotId The ID of the slot to update
     * @param newDate The new date in YYYY-MM-DD format
     * @param newStartTime The new start time in HH:mm format
     * @param newEndTime The new end time in HH:mm format
     * @return true if updated successfully, false otherwise
     */
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
