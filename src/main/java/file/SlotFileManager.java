package file;

import model.Slot;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SlotFileManager {
    private static final String FILE_PATH = "../resources/slots.txt";

    // Load all slots from file
    public static ArrayList<Slot> loadAll() {
        ArrayList<Slot> slots = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            return slots;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    String slotId = parts[0];
                    String lecturerTp = parts[1];
                    String date = parts[2];
                    String startTime = parts[3];
                    String endTime = parts[4];
                    String status = parts[5];
                    
                    Slot slot = new Slot(slotId, lecturerTp, date, startTime, endTime, status);
                    slots.add(slot);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Slot file not found: " + e.getMessage());
        }
        
        return slots;
    }

    // Save all slots to file
    public static void saveAll(ArrayList<Slot> slots) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Slot slot : slots) {
                writer.println(slot.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving slots: " + e.getMessage());
        }
    }

    // Append one slot to file
    public static void appendOne(Slot slot) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             PrintWriter writer = new PrintWriter(fw)) {
            writer.println(slot.toString());
        } catch (IOException e) {
            System.err.println("Error appending slot: " + e.getMessage());
        }
    }

    // Find slot by ID
    public static Slot findById(String slotId) {
        ArrayList<Slot> slots = loadAll();
        for (Slot slot : slots) {
            if (slot.getSlotId().equals(slotId)) {
                return slot;
            }
        }
        return null;
    }

    // Update slot
    public static boolean update(Slot updatedSlot) {
        ArrayList<Slot> slots = loadAll();
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).getSlotId().equals(updatedSlot.getSlotId())) {
                slots.set(i, updatedSlot);
                saveAll(slots);
                return true;
            }
        }
        return false;
    }

    // Delete slot by ID
    public static boolean delete(String slotId) {
        ArrayList<Slot> slots = loadAll();
        boolean removed = slots.removeIf(slot -> slot.getSlotId().equals(slotId));
        if (removed) {
            saveAll(slots);
        }
        return removed;
    }

    // Get available slots (status = OPEN)
    public static ArrayList<Slot> getAvailableSlots() {
        ArrayList<Slot> allSlots = loadAll();
        ArrayList<Slot> availableSlots = new ArrayList<>();
        for (Slot slot : allSlots) {
            if ("OPEN".equalsIgnoreCase(slot.getStatus())) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }

    // Get slots by lecturer TP
    public static ArrayList<Slot> getSlotsByLecturer(String lecturerTp) {
        ArrayList<Slot> allSlots = loadAll();
        ArrayList<Slot> lecturerSlots = new ArrayList<>();
        for (Slot slot : allSlots) {
            if (slot.getLecturerTp().equals(lecturerTp)) {
                lecturerSlots.add(slot);
            }
        }
        return lecturerSlots;
    }
}
