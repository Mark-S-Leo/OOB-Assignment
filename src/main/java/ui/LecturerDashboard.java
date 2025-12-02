package ui;

import model.User;
import model.Slot;
import model.Appointment;
import service.LecturerService;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LecturerDashboard extends JFrame {
    private User user;
    private LecturerService lecturerService;

    public LecturerDashboard(User user) {
        this.user = user;
        this.lecturerService = new LecturerService(user.getTp());

        setTitle("Lecturer Dashboard - " + user.getName());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Welcome, " + user.getName() + " (" + user.getTp() + ")");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JButton createSlotBtn = new JButton("Create Consultation Slot");
        JButton viewSlotsBtn = new JButton("View My Slots");
        JButton viewBookingsBtn = new JButton("View Bookings");
        JButton updateProfileBtn = new JButton("Update Profile");
        JButton logoutBtn = new JButton("Logout");

        createSlotBtn.addActionListener(e -> createSlot());
        viewSlotsBtn.addActionListener(e -> viewMySlots());
        viewBookingsBtn.addActionListener(e -> viewBookings());
        updateProfileBtn.addActionListener(e -> updateProfile());
        logoutBtn.addActionListener(e -> logout());

        buttonPanel.add(createSlotBtn);
        buttonPanel.add(viewSlotsBtn);
        buttonPanel.add(viewBookingsBtn);
        buttonPanel.add(updateProfileBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void createSlot() {
        JTextField dateField = new JTextField();
        JTextField startTimeField = new JTextField();
        JTextField endTimeField = new JTextField();
        
        Object[] message = {
            "Date (YYYY-MM-DD):", dateField,
            "Start Time (HH:MM):", startTimeField,
            "End Time (HH:MM):", endTimeField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create Consultation Slot", 
                                                  JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String date = dateField.getText().trim();
            String startTime = startTimeField.getText().trim();
            String endTime = endTimeField.getText().trim();

            if (date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = lecturerService.createSlot(date, startTime, endTime);
            if (success) {
                JOptionPane.showMessageDialog(this, "Slot created successfully!", 
                                             "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create slot. Check date format.", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewMySlots() {
        ArrayList<Slot> slots = lecturerService.viewOwnSlots();
        
        if (slots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no consultation slots.", 
                                         "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Your Consultation Slots:\n\n");
        for (Slot slot : slots) {
            sb.append(String.format("ID: %s | Date: %s | Time: %s-%s | Status: %s\n", 
                     slot.getSlotId(), slot.getDate(), slot.getStartTime(), 
                     slot.getEndTime(), slot.getStatus()));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "My Slots", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewBookings() {
        ArrayList<Appointment> appointments = lecturerService.viewApprovedBookings();
        
        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no bookings.", 
                                         "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Your Approved Bookings:\n\n");
        for (Appointment apt : appointments) {
            sb.append(String.format("ID: %s | Student: %s | Date: %s | Time: %s | Status: %s\n", 
                     apt.getAppointmentId(), apt.getStudentTp(), apt.getDate(), 
                     apt.getStartTime(), apt.getStatus()));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "My Bookings", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateProfile() {
        JOptionPane.showMessageDialog(this, "Profile update feature coming soon!", 
                                     "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
                                                   "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginUI().setVisible(true);
        }
    }
}
