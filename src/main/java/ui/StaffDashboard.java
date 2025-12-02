package ui;

import model.User;
import model.Request;
import model.Appointment;
import service.StaffService;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StaffDashboard extends JFrame {
    private User user;
    private StaffService staffService;

    public StaffDashboard(User user) {
        this.user = user;
        this.staffService = new StaffService();

        setTitle("Staff Dashboard - " + user.getName());
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

        JButton viewPendingBtn = new JButton("View Pending Requests");
        JButton viewAppointmentsBtn = new JButton("View All Appointments");
        JButton manageRequestBtn = new JButton("Approve/Cancel Request");
        JButton updateProfileBtn = new JButton("Update Profile");
        JButton logoutBtn = new JButton("Logout");

        viewPendingBtn.addActionListener(e -> viewPendingRequests());
        viewAppointmentsBtn.addActionListener(e -> viewAllAppointments());
        manageRequestBtn.addActionListener(e -> manageRequest());
        updateProfileBtn.addActionListener(e -> updateProfile());
        logoutBtn.addActionListener(e -> logout());

        buttonPanel.add(viewPendingBtn);
        buttonPanel.add(viewAppointmentsBtn);
        buttonPanel.add(manageRequestBtn);
        buttonPanel.add(updateProfileBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void viewPendingRequests() {
        ArrayList<Request> requests = staffService.viewPendingRequests();
        
        if (requests.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No pending requests found.", 
                                         "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Pending Consultation Requests:\n\n");
        for (Request req : requests) {
            sb.append(String.format("ID: %s | Student: %s | Lecturer: %s | Slot: %s\nReason: %s\n\n", 
                     req.getRequestId(), req.getStudentTp(), req.getLecturerTp(), 
                     req.getSlotId(), req.getReason()));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Pending Requests", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewAllAppointments() {
        ArrayList<Appointment> appointments = staffService.viewAllAppointments();
        
        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments found.", 
                                         "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("All Appointments:\n\n");
        for (Appointment apt : appointments) {
            sb.append(String.format("ID: %s | Student: %s | Lecturer: %s | Date: %s | Time: %s | Status: %s\n", 
                     apt.getAppointmentId(), apt.getStudentTp(), apt.getLecturerTp(), 
                     apt.getDate(), apt.getStartTime(), apt.getStatus()));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "All Appointments", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }

    private void manageRequest() {
        String requestId = JOptionPane.showInputDialog(this, "Enter Request ID:");
        
        if (requestId == null || requestId.trim().isEmpty()) {
            return;
        }

        String[] options = {"Approve", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, 
                                                 "What would you like to do with request " + requestId + "?",
                                                 "Manage Request",
                                                 JOptionPane.DEFAULT_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE,
                                                 null,
                                                 options,
                                                 options[0]);

        if (choice == 0) {
            // Approve
            boolean success = staffService.approveRequest(requestId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Request approved successfully!", 
                                             "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to approve request.", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (choice == 1) {
            // Cancel
            boolean success = staffService.cancelRequest(requestId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Request cancelled successfully!", 
                                             "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel request.", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
