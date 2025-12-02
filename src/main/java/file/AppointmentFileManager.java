package file;

import model.Appointment;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AppointmentFileManager {
    private static final String FILE_PATH = "src/main/resources/appointments.txt";

    // Load all appointments from file
    public static ArrayList<Appointment> loadAll() {
        ArrayList<Appointment> appointments = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            return appointments;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    String appointmentId = parts[0];
                    String requestId = parts[1];
                    String studentTp = parts[2];
                    String lecturerTp = parts[3];
                    String slotId = parts[4];
                    String date = parts[5];
                    String startTime = parts[6];
                    String status = parts[7];
                    
                    Appointment appointment = new Appointment(appointmentId, requestId, studentTp, 
                                                             lecturerTp, slotId, date, startTime, status);
                    appointments.add(appointment);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Appointment file not found: " + e.getMessage());
        }
        
        return appointments;
    }

    // Save all appointments to file
    public static void saveAll(ArrayList<Appointment> appointments) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Appointment appointment : appointments) {
                writer.println(appointment.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving appointments: " + e.getMessage());
        }
    }

    // Append one appointment to file
    public static void appendOne(Appointment appointment) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             PrintWriter writer = new PrintWriter(fw)) {
            writer.println(appointment.toString());
        } catch (IOException e) {
            System.err.println("Error appending appointment: " + e.getMessage());
        }
    }

    // Find appointment by ID
    public static Appointment findById(String appointmentId) {
        ArrayList<Appointment> appointments = loadAll();
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                return appointment;
            }
        }
        return null;
    }

    // Update appointment
    public static boolean update(Appointment updatedAppointment) {
        ArrayList<Appointment> appointments = loadAll();
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getAppointmentId().equals(updatedAppointment.getAppointmentId())) {
                appointments.set(i, updatedAppointment);
                saveAll(appointments);
                return true;
            }
        }
        return false;
    }

    // Delete appointment by ID
    public static boolean delete(String appointmentId) {
        ArrayList<Appointment> appointments = loadAll();
        boolean removed = appointments.removeIf(appointment -> 
            appointment.getAppointmentId().equals(appointmentId));
        if (removed) {
            saveAll(appointments);
        }
        return removed;
    }

    // Get appointments by lecturer TP
    public static ArrayList<Appointment> getAppointmentsByLecturer(String lecturerTp) {
        ArrayList<Appointment> allAppointments = loadAll();
        ArrayList<Appointment> lecturerAppointments = new ArrayList<>();
        for (Appointment appointment : allAppointments) {
            if (appointment.getLecturerTp().equals(lecturerTp)) {
                lecturerAppointments.add(appointment);
            }
        }
        return lecturerAppointments;
    }

    // Get appointments by student TP
    public static ArrayList<Appointment> getAppointmentsByStudent(String studentTp) {
        ArrayList<Appointment> allAppointments = loadAll();
        ArrayList<Appointment> studentAppointments = new ArrayList<>();
        for (Appointment appointment : allAppointments) {
            if (appointment.getStudentTp().equals(studentTp)) {
                studentAppointments.add(appointment);
            }
        }
        return studentAppointments;
    }
}
