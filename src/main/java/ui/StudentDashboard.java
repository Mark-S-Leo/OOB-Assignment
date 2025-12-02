package ui;

import model.User;
import model.Slot;
import model.Request;
import model.Appointment;
import service.StudentService;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StudentDashboard extends JFrame {
    private User user;
    private StudentService studentService;

    public StudentDashboard(User user) {
        this.user = user;
        this.studentService = new StudentService(user.getTp());

        setTitle("Student Dashboard - " + user.getName());
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

        JButton viewSlotsBtn = new JButton("View Available Slots");
        JButton createRequestBtn = new JButton("Create Consultation Request");
        JButton viewRequestsBtn = new JButton("View My Requests");
        JButton updateProfileBtn = new JButton("Update Profile");
        JButton logoutBtn = new JButton("Logout");

        viewSlotsBtn.addActionListener(e -> viewAvailableSlots());
        createRequestBtn.addActionListener(e -> createRequest());
        viewRequestsBtn.addActionListener(e -> viewMyRequests());
        updateProfileBtn.addActionListener(e -> updateProfile());
        logoutBtn.addActionListener(e -> logout());

        buttonPanel.add(viewSlotsBtn);
        buttonPanel.add(createRequestBtn);
        buttonPanel.add(viewRequestsBtn);
        buttonPanel.add(updateProfileBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void viewAvailableSlots() {
        ArrayList<Slot> slots = studentService.viewAvailableSlots();
        
        if (slots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available slots found.", 
                                         "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Available Consultation Slots:\n\n");
        for (Slot slot : slots) {
            sb.append(String.format("ID: %s | Lecturer: %s | Date: %s | Time: %s-%s\n", 
                     slot.getSlotId(), slot.getLecturerTp(), slot.getDate(), 
                     slot.getStartTime(), slot.getEndTime()));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Available Slots", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }

    private void createRequest() {
        JTextField lecturerField = new JTextField();
        JTextField slotIdField = new JTextField();
        JTextArea reasonArea = new JTextArea(3, 20);
        
        Object[] message = {
            "Lecturer TP:", lecturerField,
            "Slot ID:", slotIdField,
            "Reason:", new JScrollPane(reasonArea)
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create Consultation Request", 
                                                  JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String lecturerTp = lecturerField.getText().trim();
            String slotId = slotIdField.getText().trim();
            String reason = reasonArea.getText().trim();

            if (lecturerTp.isEmpty() || slotId.isEmpty() || reason.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = studentService.createConsultationRequest(lecturerTp, slotId, reason);
            if (success) {
                JOptionPane.showMessageDialog(this, "Request created successfully!", 
                                             "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create request. Check slot availability.", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewMyRequests() {
        ArrayList<Request> requests = studentService.viewOwnRequests();
        
        if (requests.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no requests.", 
                                         "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Your Consultation Requests:\n\n");
        for (Request req : requests) {
            sb.append(String.format("ID: %s | Lecturer: %s | Slot: %s | Status: %s\nReason: %s\n\n", 
                     req.getRequestId(), req.getLecturerTp(), req.getSlotId(), 
                     req.getStatus(), req.getReason()));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "My Requests", 
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
