package ui;

/*
 * Staff Consultation Dashboard
 * Copyright (c) 2025 OOB Assignment Team
 * 
 * This software is the intellectual property of the development team.
 * All rights reserved.
 */

import static ui.UITheme.*;

import model.User;
import model.Request;
import model.Appointment;
import service.StaffService;
import file.UserFileManager;
import file.AppointmentFileManager;
import util.ImageUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// LGoodDatePicker imports
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;

/**
 * StaffDashboard provides the main interface for staff to:
 * - Manage pending consultation requests (approve/reject)
 * - View and edit all appointments
 * - Update their profile
 */
public class StaffDashboard extends JFrame {
    private User user;
    private StaffService staffService;
    private Map<String, String> studentMap; // tp -> name (cached for performance)
    private Map<String, String> lecturerMap; // tp -> name (cached for performance)
    private JPopupMenu profileMenu;

    public StaffDashboard(User user) {
        this.user = user;
        this.staffService = new StaffService();
        this.studentMap = new HashMap<>();
        this.lecturerMap = new HashMap<>();
        loadUserData();

        setTitle("Appointment Management Module");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BACKGROUND_COLOR);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }
    
    /**
     * Load student and lecturer data once at initialization for better performance
     */
    private void loadUserData() {
        UserFileManager.loadAll().forEach(u -> {
            if ("STUDENT".equalsIgnoreCase(u.getRole())) {
                studentMap.put(u.getTp(), u.getName());
            } else if ("LECTURER".equalsIgnoreCase(u.getRole())) {
                lecturerMap.put(u.getTp(), u.getName());
            }
        });
    }
    
    private String getStudentName(String tp) {
        return studentMap.getOrDefault(tp, tp);
    }
    
    private String getLecturerName(String tp) {
        return lecturerMap.getOrDefault(tp, tp);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(STAFF_COLOR); // Staff-specific teal color
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_XL, SPACING_XXL, SPACING_XL, SPACING_XXL));
        
        JLabel titleLabel = new JLabel("Appointment Management Module");
        titleLabel.setFont(FONT_HEADER_LARGE);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        // Right side: Profile picture + name
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_LG, 0));
        profilePanel.setBackground(STAFF_COLOR);
        
        // Profile picture button (clickable circular image)
        JButton profileBtn = new JButton();
        ImageIcon profileIcon = ImageUtil.createHeaderProfileImage(user.getProfilePicture());
        profileBtn.setIcon(profileIcon);
        profileBtn.setBorderPainted(false);
        profileBtn.setContentAreaFilled(false);
        profileBtn.setFocusPainted(false);
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileBtn.setToolTipText("Click for profile options");
        profileBtn.setPreferredSize(new Dimension(50, 50));
        
        // Create profile dropdown menu
        profileMenu = new JPopupMenu();
        profileMenu.setBorder(BorderFactory.createLineBorder(INPUT_BORDER));
        
        JMenuItem viewProfileItem = new JMenuItem("View/Edit Profile");
        viewProfileItem.setFont(FONT_BODY);
        viewProfileItem.addActionListener(e -> openProfileDialog());
        
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(FONT_BODY);
        logoutItem.addActionListener(e -> logout());
        
        profileMenu.add(viewProfileItem);
        profileMenu.addSeparator();
        profileMenu.add(logoutItem);
        
        profileBtn.addActionListener(e -> {
            profileMenu.show(profileBtn, 0, profileBtn.getHeight());
        });
        
        JLabel userLabel = new JLabel("Welcome, " + user.getName());
        userLabel.setFont(FONT_BODY);
        userLabel.setForeground(Color.WHITE);
        
        profilePanel.add(userLabel);
        profilePanel.add(profileBtn);
        
        panel.add(profilePanel, BorderLayout.EAST);
        
        return panel;
    }
    

    /**
     * Create main content panel with action cards
     * Merged: "View Pending Requests" and "Manage Requests" into single interface
     * Removed: "My Profile" card (redundant with header profile button)
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Create action cards with icons
        panel.add(createActionCard("Manage Requests", 
            "Review, approve or reject consultation requests", 
            "✔", e -> manageRequests()));
            
        panel.add(createActionCard("All Appointments", 
            "View and edit all scheduled consultations", 
            "◷", e -> viewAllAppointments()));
        
        return panel;
    }
    
    private JPanel createActionCard(String title, String description, String icon, java.awt.event.ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(25, SPACING_XL, 25, SPACING_XL)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FONT_ICON_LARGE);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_HEADER_SMALL);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(FONT_SMALL);
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descLabel);
        
        // Make card clickable with single click
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1) {
                    action.actionPerformed(null);
                }
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(CARD_COLOR);
            }
        });
        
        return card;
    }
    
    /**
     * Create footer panel
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_MD, SPACING_XL, SPACING_XL, SPACING_XL));
        return panel;
    }

    /**
     * Merged method: View and manage pending requests in one interface
     * Combines viewing with approve/reject actions
     */
    private void manageRequests() {
        ArrayList<Request> pendingRequests = staffService.viewPendingRequests();
        
        if (pendingRequests.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No pending requests to manage.", 
                "No Pending Requests", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog(this, "Manage Consultation Requests", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Create table with full request details
        String[] columns = {"Request ID", "Student", "Lecturer", "Date", "Time", "Reason", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        pendingRequests.forEach(req -> {
            String reason = req.getReason();
            tableModel.addRow(new Object[]{
                req.getRequestId(),
                getStudentName(req.getStudentTp()),
                getLecturerName(req.getLecturerTp()),
                req.getDate(),
                req.getStartTime() + " - " + req.getEndTime(),
                reason.length() > 50 ? reason.substring(0, 50) + "..." : reason,
                req.getStatus()
            });
        });
        
        JTable table = new JTable(tableModel);
        table.setFont(FONT_SMALL);
        table.setRowHeight(TABLE_ROW_HEIGHT);
        table.setGridColor(GRID_COLOR);
        table.setSelectionBackground(SELECTION_COLOR);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setPreferredScrollableViewportSize(new Dimension(900, 350));
        table.setFillsViewportHeight(true);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setBackground(CARD_COLOR);
        header.setForeground(TEXT_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton closeBtn = createStyledButton("Close", TEXT_SECONDARY);
        JButton rejectBtn = createStyledButton("Reject", DANGER_COLOR);
        JButton approveBtn = createStyledButton("Approve", SUCCESS_COLOR);
        
        approveBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                showError("Please select a request to approve.");
                return;
            }
            
            String requestId = (String) tableModel.getValueAt(selectedRow, 0);
            boolean success = staffService.approveRequest(requestId);
            
            if (success) {
                JOptionPane.showMessageDialog(dialog, 
                    "Request approved successfully!\nAppointment has been created.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                tableModel.removeRow(selectedRow);
                if (tableModel.getRowCount() == 0) {
                    dialog.dispose();
                }
            } else {
                showError("Failed to approve request. It may have already been processed.");
            }
        });
        
        rejectBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                showError("Please select a request to reject.");
                return;
            }
            
            // Create dialog to capture cancel reason
            JDialog cancelDialog = new JDialog(dialog, "Cancel Request", true);
            cancelDialog.setLayout(new BorderLayout(10, 10));
            cancelDialog.setSize(450, 250);
            cancelDialog.setLocationRelativeTo(dialog);
            
            JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel label = new JLabel("Please provide a reason for cancellation:");
            label.setFont(FONT_SMALL);
            contentPanel.add(label, BorderLayout.NORTH);
            
            JTextArea reasonArea = new JTextArea(5, 30);
            reasonArea.setFont(FONT_SMALL);
            reasonArea.setLineWrap(true);
            reasonArea.setWrapStyleWord(true);
            reasonArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            JScrollPane reasonScroll = new JScrollPane(reasonArea);
            contentPanel.add(reasonScroll, BorderLayout.CENTER);
            
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            btnPanel.setBackground(Color.WHITE);
            
            JButton cancelBtn = createStyledButton("Cancel", TEXT_SECONDARY);
            JButton confirmBtn = createStyledButton("Confirm", DANGER_COLOR);
            
            cancelBtn.addActionListener(ev -> cancelDialog.dispose());
            
            confirmBtn.addActionListener(ev -> {
                String cancelReason = reasonArea.getText().trim();
                if (cancelReason.isEmpty()) {
                    JOptionPane.showMessageDialog(cancelDialog,
                        "Please provide a reason for cancellation.",
                        "Missing Reason",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String requestId = (String) tableModel.getValueAt(selectedRow, 0);
                boolean success = staffService.cancelRequest(requestId, cancelReason);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Request rejected successfully.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.removeRow(selectedRow);
                    if (tableModel.getRowCount() == 0) {
                        dialog.dispose();
                    }
                    cancelDialog.dispose();
                } else {
                    showError("Failed to reject request. It may have already been processed.");
                }
            });
            
            btnPanel.add(cancelBtn);
            btnPanel.add(confirmBtn);
            contentPanel.add(btnPanel, BorderLayout.SOUTH);
            
            cancelDialog.add(contentPanel);
            cancelDialog.setVisible(true);
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(approveBtn);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(950, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * View all appointments with edit functionality
     */
    private void viewAllAppointments() {
        ArrayList<Appointment> appointments = staffService.viewAllAppointments();
        
        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No appointments found.", 
                "No Appointments", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog(this, "All Appointments", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Create table
        String[] columns = {"Appointment ID", "Student", "Lecturer", "Date", "Time", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        // Store appointment objects for editing
        Map<String, Appointment> appointmentMap = new HashMap<>();
        
        for (Appointment apt : appointments) {
            appointmentMap.put(apt.getAppointmentId(), apt);
            tableModel.addRow(new Object[]{
                apt.getAppointmentId(),
                getStudentName(apt.getStudentTp()),
                getLecturerName(apt.getLecturerTp()),
                apt.getDate(),
                apt.getStartTime(),
                apt.getStatus()
            });
        }
        
        JTable table = new JTable(tableModel);
        table.setFont(FONT_SMALL);
        table.setRowHeight(35);
        table.setGridColor(GRID_COLOR);
        table.setSelectionBackground(SELECTION_COLOR);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setBackground(CARD_COLOR);
        header.setForeground(TEXT_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton closeBtn = createStyledButton("Close", TEXT_SECONDARY);
        JButton editBtn = createStyledButton("Edit Appointment", PRIMARY_COLOR);
        
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                showError("Please select an appointment to edit.");
                return;
            }
            
            String appointmentId = (String) tableModel.getValueAt(selectedRow, 0);
            Appointment appointment = appointmentMap.get(appointmentId);
            
            if (appointment != null) {
                openEditAppointmentDialog(appointment, tableModel, selectedRow);
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeBtn);
        buttonPanel.add(editBtn);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(950, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Edit appointment dialog with date/time pickers
     */
    private void openEditAppointmentDialog(Appointment appointment, DefaultTableModel tableModel, int row) {
        JDialog editDialog = new JDialog(this, "Edit Appointment - " + appointment.getAppointmentId(), true);
        editDialog.setLayout(new BorderLayout(10, 10));
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Edit Appointment Details");
        titleLabel.setFont(FONT_HEADER_MEDIUM);
        titleLabel.setForeground(TEXT_COLOR);
        formPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        // Date picker
        gbc.gridx = 0;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(FONT_BODY);
        formPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        DatePicker datePicker = new DatePicker();
        datePicker.setDate(java.time.LocalDate.parse(appointment.getDate()));
        datePicker.setPreferredSize(new Dimension(200, 35));
        formPanel.add(datePicker, gbc);
        
        // Start time picker
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel startTimeLabel = new JLabel("Start Time:");
        startTimeLabel.setFont(FONT_BODY);
        formPanel.add(startTimeLabel, gbc);
        
        gbc.gridx = 1;
        TimePicker startTimePicker = new TimePicker();
        startTimePicker.setTime(java.time.LocalTime.parse(appointment.getStartTime()));
        startTimePicker.getSettings().setFormatForDisplayTime("HH:mm");
        startTimePicker.getSettings().setFormatForMenuTimes("HH:mm");
        startTimePicker.setPreferredSize(new Dimension(200, 35));
        formPanel.add(startTimePicker, gbc);
        
        // Status dropdown
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(FONT_BODY);
        formPanel.add(statusLabel, gbc);
        
        gbc.gridx = 1;
        String[] statuses = {"SCHEDULED", "COMPLETED", "CANCELLED"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(appointment.getStatus());
        statusCombo.setFont(FONT_BODY);
        formPanel.add(statusCombo, gbc);
        
        editDialog.add(formPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton saveBtn = createStyledButton("Save Changes", SUCCESS_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", TEXT_SECONDARY);
        
        saveBtn.addActionListener(e -> {
            java.time.LocalDate selectedDate = datePicker.getDate();
            java.time.LocalTime selectedTime = startTimePicker.getTime();
            String selectedStatus = (String) statusCombo.getSelectedItem();
            
            if (selectedDate == null || selectedTime == null) {
                showError("Please select both date and time.");
                return;
            }
            
            // Update appointment object
            appointment.setDate(selectedDate.toString());
            appointment.setStartTime(selectedTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
            appointment.setStatus(selectedStatus);
            
            // Save to file
            if (AppointmentFileManager.update(appointment)) {
                // Update table display
                tableModel.setValueAt(appointment.getDate(), row, 3);
                tableModel.setValueAt(appointment.getStartTime(), row, 4);
                tableModel.setValueAt(appointment.getStatus(), row, 5);
                
                JOptionPane.showMessageDialog(editDialog, 
                    "Appointment updated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                editDialog.dispose();
            } else {
                showError("Failed to update appointment. Please try again.");
            }
        });
        
        cancelBtn.addActionListener(e -> editDialog.dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        editDialog.pack();
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    /**
     * Functional profile dialog - allows updating profile picture, description, and password
     */
    private void openProfileDialog() {
        JDialog profileDialog = new JDialog(this, "My Profile", true);
        profileDialog.setLayout(new BorderLayout(10, 10));
        profileDialog.setSize(650, 650);
        profileDialog.setLocationRelativeTo(this);
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Profile Management");
        titleLabel.setFont(FONT_HEADER_LARGE);
        titleLabel.setForeground(STAFF_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(25));
        
        // Profile Picture Section
        JPanel picPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        picPanel.setBackground(Color.WHITE);
        picPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        picPanel.setMaximumSize(new Dimension(650, 150));
        
        JLabel profileImageLabel = new JLabel();
        ImageIcon profileIcon = ImageUtil.createDialogProfileImage(user.getProfilePicture());
        profileImageLabel.setIcon(profileIcon);
        profileImageLabel.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 2));
        
        JButton changePicBtn = createStyledButton("Change Profile Picture", STAFF_COLOR);
        changePicBtn.setPreferredSize(new Dimension(200, 35));
        changePicBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Profile Picture");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));
            
            int result = fileChooser.showOpenDialog(profileDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                
                if (!ImageUtil.isValidImageFile(selectedFile)) {
                    showError("Invalid file type. Please select a JPG or PNG image.");
                    return;
                }
                
                String imagePath = selectedFile.getAbsolutePath();
                user.setProfilePicture(imagePath);
                
                ImageIcon newIcon = ImageUtil.createDialogProfileImage(imagePath);
                profileImageLabel.setIcon(newIcon);
                
                JOptionPane.showMessageDialog(profileDialog,
                    "Profile picture updated! Click 'Save Changes' to apply.",
                    "Image Selected",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JPanel imageContainer = new JPanel();
        imageContainer.setLayout(new BoxLayout(imageContainer, BoxLayout.Y_AXIS));
        imageContainer.setBackground(Color.WHITE);
        profileImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        changePicBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageContainer.add(profileImageLabel);
        imageContainer.add(Box.createVerticalStrut(15));
        imageContainer.add(changePicBtn);
        
        picPanel.add(imageContainer);
        contentPanel.add(picPanel);
        contentPanel.add(Box.createVerticalStrut(25));
        
        // Read-only fields
        JTextField tpField = new JTextField(user.getTp());
        tpField.setEditable(false);
        tpField.setFocusable(false);
        tpField.setBackground(CARD_COLOR);
        contentPanel.add(createProfileField("TP Number:", tpField));
        contentPanel.add(Box.createVerticalStrut(15));
        
        JTextField nameField = new JTextField(user.getName());
        nameField.setEditable(false);
        nameField.setFocusable(false);
        nameField.setBackground(CARD_COLOR);
        contentPanel.add(createProfileField("Name:", nameField));
        contentPanel.add(Box.createVerticalStrut(15));
        
        JTextField emailField = new JTextField(user.getEmail());
        emailField.setEditable(false);
        emailField.setFocusable(false);
        emailField.setBackground(CARD_COLOR);
        contentPanel.add(createProfileField("Email:", emailField));
        contentPanel.add(Box.createVerticalStrut(15));
        
        JTextField roleField = new JTextField(user.getRole());
        roleField.setEditable(false);
        roleField.setFocusable(false);
        roleField.setBackground(CARD_COLOR);
        contentPanel.add(createProfileField("Role:", roleField));
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Editable description
        JTextArea descArea = new JTextArea(user.getDescription(), 3, 30);
        descArea.setFont(FONT_BODY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        JPanel descPanel = new JPanel(new BorderLayout(10, 5));
        descPanel.setBackground(Color.WHITE);
        descPanel.setMaximumSize(new Dimension(650, 120));
        
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(FONT_SUBHEADER);
        descPanel.add(descLabel, BorderLayout.NORTH);
        descPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);
        
        contentPanel.add(descPanel);
        contentPanel.add(Box.createVerticalStrut(25));
        
        // Password Change Section
        JPanel passPanel = new JPanel();
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
        passPanel.setBackground(CARD_COLOR);
        passPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRID_COLOR),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        passPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passPanel.setMaximumSize(new Dimension(550, 180));
        
        JLabel passTitle = new JLabel("Change Password");
        passTitle.setFont(FONT_SUBHEADER);
        passTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        passPanel.add(passTitle);
        passPanel.add(Box.createVerticalStrut(15));
        
        JPasswordField currentPassField = new JPasswordField();
        passPanel.add(createPasswordFieldPanel("Current Password:", currentPassField));
        passPanel.add(Box.createVerticalStrut(10));
        
        JPasswordField newPassField = new JPasswordField();
        passPanel.add(createPasswordFieldPanel("New Password:", newPassField));
        passPanel.add(Box.createVerticalStrut(10));
        
        JPasswordField confirmPassField = new JPasswordField();
        passPanel.add(createPasswordFieldPanel("Confirm Password:", confirmPassField));
        
        contentPanel.add(passPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        profileDialog.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton saveBtn = createStyledButton("Save Changes", SUCCESS_COLOR);
        JButton cancelBtnDialog = createStyledButton("Cancel", TEXT_SECONDARY);
        
        saveBtn.addActionListener(e -> {
            // Handle password change
            String currentPass = new String(currentPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());
            
            if (!currentPass.isEmpty() || !newPass.isEmpty() || !confirmPass.isEmpty()) {
                if (!currentPass.equals(user.getPassword())) {
                    showError("Current password is incorrect.");
                    return;
                }
                
                if (newPass.isEmpty()) {
                    showError("New password cannot be empty.");
                    return;
                }
                
                if (newPass.length() < 6) {
                    showError("New password must be at least 6 characters long.");
                    return;
                }
                
                if (!newPass.equals(confirmPass)) {
                    showError("New passwords do not match.");
                    return;
                }
                
                user.setPassword(newPass);
            }
            
            // Update description
            user.setDescription(descArea.getText().trim());
            
            // Save to file
            boolean success = UserFileManager.update(user);
            if (success) {
                // Update header profile image
                updateHeaderProfileImage();
                
                JOptionPane.showMessageDialog(profileDialog,
                    "Profile updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                profileDialog.dispose();
                
                if (!currentPass.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Password changed. Please log in again.",
                        "Security Notice",
                        JOptionPane.INFORMATION_MESSAGE);
                    logout();
                }
            } else {
                showError("Failed to update profile. Please try again.");
            }
        });
        
        cancelBtnDialog.addActionListener(e -> profileDialog.dispose());
        
        buttonPanel.add(cancelBtnDialog);
        buttonPanel.add(saveBtn);
        
        profileDialog.add(buttonPanel, BorderLayout.SOUTH);
        profileDialog.setVisible(true);
    }
    
    private JPanel createProfileField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(600, 40));
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_SUBHEADER);
        lbl.setPreferredSize(new Dimension(150, 30));
        
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        panel.add(lbl, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPasswordFieldPanel(String label, JPasswordField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(CARD_COLOR);
        panel.setMaximumSize(new Dimension(600, 35));
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_SMALL);
        lbl.setPreferredSize(new Dimension(140, 30));
        
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        panel.add(lbl, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateHeaderProfileImage() {
        ImageIcon newHeaderIcon = ImageUtil.createHeaderProfileImage(user.getProfilePicture());
        Component[] components = ((JPanel)getContentPane().getComponent(0)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component inner : panel.getComponents()) {
                    if (inner instanceof JButton) {
                        JButton btn = (JButton) inner;
                        if (btn.getToolTipText() != null && 
                            btn.getToolTipText().contains("profile")) {
                            btn.setIcon(newHeaderIcon);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, 
            "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BODY);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginUI().setVisible(true);
        }
    }
}
