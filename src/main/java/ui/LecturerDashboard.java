package ui;

/*

 * This file uses LGoodDatePicker library
 * Copyright (c) 2016 LGoodDatePicker
 * Licensed under MIT License
 * https://github.com/LGoodDatePicker/LGoodDatePicker

 */

import static ui.UITheme.*;

import model.User;
import model.Slot;
import model.Appointment;
import service.LecturerService;
import file.UserFileManager;
import file.SlotFileManager;
import util.ImageUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// LGoodDatePicker imports
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.zinternaltools.TimeChangeEvent;

public class LecturerDashboard extends JFrame {
    private User user;
    private LecturerService lecturerService;
    private Map<String, String> studentMap; // tp -> name (cached for performance)
    private JPopupMenu profileMenu;

    public LecturerDashboard(User user) {
        this.user = user;
        this.lecturerService = new LecturerService(user.getTp());
        this.studentMap = new HashMap<>();
        loadStudentData();

        setTitle("Lecturer Consultation Portal");
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
     * Load student data once at initialization for better performance
     */
    private void loadStudentData() {
        UserFileManager.loadAll().stream()
            .filter(u -> "STUDENT".equalsIgnoreCase(u.getRole()))
            .forEach(u -> studentMap.put(u.getTp(), u.getName()));
    }
    
    private String getStudentName(String tp) {
        return studentMap.getOrDefault(tp, "Unknown");
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_XL, SPACING_XXL, SPACING_XL, SPACING_XXL));
        
        JLabel titleLabel = new JLabel("Lecturer Consultation Portal");
        titleLabel.setFont(FONT_HEADER_LARGE);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_LG, 0));
        profilePanel.setBackground(PRIMARY_COLOR);
        
        JButton profileBtn = new JButton();
        ImageIcon profileIcon = ImageUtil.createHeaderProfileImage(user.getProfilePicture());
        profileBtn.setIcon(profileIcon);
        profileBtn.setBorderPainted(false);
        profileBtn.setContentAreaFilled(false);
        profileBtn.setFocusPainted(false);
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileBtn.setToolTipText("Click for profile options");
        profileBtn.setPreferredSize(new Dimension(50, 50));
        
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
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        panel.add(createActionCard("Create Slot", 
            "Create new consultation time slots", 
            "➕", e -> createSlot()));
            
        panel.add(createActionCard("My Slots", 
            "View and manage your consultation slots", 
            "◉", e -> viewMySlots()));
            
        panel.add(createActionCard("My Appointments", 
            "View confirmed consultation appointments", 
            "✓", e -> viewBookings()));
            

        
        return panel;
    }
    
    private JPanel createActionCard(String title, String description, String icon, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(25, SPACING_XL, 25, SPACING_XL)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FONT_ICON_LARGE);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_HEADER_SMALL);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(FONT_SMALL);
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descLabel);
        
        card.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1) {
                    action.actionPerformed(null);
                }
            }
            public void mouseEntered(MouseEvent evt) {
                card.setBackground(HOVER_COLOR);
            }
            public void mouseExited(MouseEvent evt) {
                card.setBackground(CARD_COLOR);
            }
        });
        
        return card;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_MD, SPACING_XL, SPACING_XL, SPACING_XL));
        return panel;
    }

    private void createSlot() {
        JDialog dialog = new JDialog(this, "Create Consultation Slot", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        JLabel titleLabel = new JLabel("Create New Consultation Slot");
        titleLabel.setFont(FONT_HEADER_MEDIUM);
        titleLabel.setForeground(TEXT_COLOR);
        formPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        // Date picker using LGoodDatePicker
        gbc.gridx = 0;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(FONT_BODY);
        formPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        DatePicker datePicker = new DatePicker();
        datePicker.setDateToToday();
        datePicker.getComponentDateTextField().setFont(FONT_BODY);
        datePicker.getComponentDateTextField().setPreferredSize(new Dimension(200, 35));
        formPanel.add(datePicker, gbc);
        
        // Start Time picker using LGoodDatePicker
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel startLabel = new JLabel("Start Time:");
        startLabel.setFont(FONT_BODY);
        formPanel.add(startLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        TimePicker startTimePicker = new TimePicker();
        LocalTime nextHour = LocalTime.now().plusHours(1).withMinute(0).withSecond(0);
        startTimePicker.setTime(nextHour);
        startTimePicker.getComponentTimeTextField().setFont(FONT_BODY);
        startTimePicker.getComponentTimeTextField().setPreferredSize(new Dimension(200, 35));
        // Force 24-hour format (military time)
        startTimePicker.getSettings().setFormatForDisplayTime("HH:mm");
        startTimePicker.getSettings().setFormatForMenuTimes("HH:mm");
        formPanel.add(startTimePicker, gbc);
        
        // End Time picker using LGoodDatePicker
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel endLabel = new JLabel("End Time:");
        endLabel.setFont(FONT_BODY);
        formPanel.add(endLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        TimePicker endTimePicker = new TimePicker();
        LocalTime endTime = nextHour.plusHours(2);
        endTimePicker.setTime(endTime);
        endTimePicker.getComponentTimeTextField().setFont(FONT_BODY);
        endTimePicker.getComponentTimeTextField().setPreferredSize(new Dimension(200, 35));
        // Force 24-hour format (military time)
        endTimePicker.getSettings().setFormatForDisplayTime("HH:mm");
        endTimePicker.getSettings().setFormatForMenuTimes("HH:mm");
        formPanel.add(endTimePicker, gbc);
        
        // Auto-update end time when start time changes
        startTimePicker.addTimeChangeListener((TimeChangeEvent event) -> {
            if (event.getNewTime() != null) {
                endTimePicker.setTime(event.getNewTime().plusHours(2));
            }
        });
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton createBtn = createStyledButton("Create Slot", SUCCESS_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", TEXT_SECONDARY);
        
        createBtn.addActionListener(e -> {
            LocalDate selectedDate = datePicker.getDate();
            LocalTime selectedStartTime = startTimePicker.getTime();
            LocalTime selectedEndTime = endTimePicker.getTime();

            if (selectedDate == null || selectedStartTime == null || selectedEndTime == null) {
                showError("All fields are required.");
                return;
            }
            
            if (selectedEndTime.isBefore(selectedStartTime) || selectedEndTime.equals(selectedStartTime)) {
                showError("End time must be after start time.");
                return;
            }

            String date = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String startTime = selectedStartTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            String endTimeStr = selectedEndTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            boolean success = lecturerService.createSlot(date, startTime, endTimeStr);
            if (success) {
                JOptionPane.showMessageDialog(dialog, 
                    "Slot created successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                showError("Failed to create slot. Check date/time format.");
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(createBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void viewMySlots() {
        ArrayList<Slot> slots = lecturerService.viewOwnSlots();
        
        if (slots.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "You have no consultation slots yet.\nCreate a slot to get started.", 
                "No Slots", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "My Consultation Slots", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        String[] columns = {"Slot ID", "Date", "Start Time", "End Time", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        for (Slot slot : slots) {
            tableModel.addRow(new Object[]{
                slot.getSlotId(),
                slot.getDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus()
            });
        }
        
        JTable table = new JTable(tableModel);
        table.setFont(FONT_BODY);
        table.setRowHeight(TABLE_ROW_HEIGHT);
        table.setGridColor(GRID_COLOR);
        table.setSelectionBackground(SELECTION_COLOR);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setBackground(CARD_COLOR);
        header.setForeground(TEXT_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel with Delete and Close
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        
        JButton closeBtn = createStyledButton("Close", TEXT_SECONDARY);
        JButton deleteBtn = createStyledButton("Delete Slot", DANGER_COLOR);
        
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                showError("Please select a slot to delete.");
                return;
            }
            
            String slotId = (String) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 4);
            
            if ("BOOKED".equalsIgnoreCase(status)) {
                showError("Cannot delete a booked slot. Please cancel the appointment first.");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(dialog,
                "Are you sure you want to delete this slot?\nSlot ID: " + slotId,
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = SlotFileManager.delete(slotId);
                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                        "Slot deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    tableModel.removeRow(selectedRow);
                    
                    if (tableModel.getRowCount() == 0) {
                        dialog.dispose();
                    }
                } else {
                    showError("Failed to delete slot. Please try again.");
                }
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        
        buttonPanel.add(closeBtn);
        buttonPanel.add(deleteBtn);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(750, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    

    private void viewBookings() {
        ArrayList<Appointment> appointments = lecturerService.viewApprovedBookings();
        
        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "You have no approved appointments yet.", 
                "No Appointments", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columns = {"Appointment ID", "Student", "Date", "Time", "Status"};
        Object[][] data = new Object[appointments.size()][5];
        
        for (int i = 0; i < appointments.size(); i++) {
            Appointment apt = appointments.get(i);
            data[i] = new Object[]{
                apt.getAppointmentId(),
                getStudentName(apt.getStudentTp()),
                apt.getDate(),
                apt.getStartTime(),
                apt.getStatus()
            };
        }
        
        JTable table = createStyledTable(data, columns);
        table.setRowHeight(35);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "My Approved Appointments", JOptionPane.PLAIN_MESSAGE);
    }
    
    private JTable createStyledTable(Object[][] data, String[] columns) {
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable table = new JTable(model);
        table.setFont(FONT_BODY);
        table.setRowHeight(TABLE_ROW_HEIGHT);
        table.setGridColor(GRID_COLOR);
        table.setSelectionBackground(SELECTION_COLOR);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setBackground(CARD_COLOR);
        header.setForeground(TEXT_COLOR);
        
        return table;
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

    private void openProfileDialog() {
        JDialog profileDialog = new JDialog(this, "My Profile", true);
        profileDialog.setLayout(new BorderLayout(10, 10));
        profileDialog.setSize(650, 700);
        profileDialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        
        JLabel titleLabel = new JLabel("Profile Management");
        titleLabel.setFont(FONT_HEADER_LARGE);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(25));
        
        JPanel picPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        picPanel.setBackground(Color.WHITE);
        picPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        picPanel.setMaximumSize(new Dimension(650, 150));
        
        JLabel profileImageLabel = new JLabel();
        ImageIcon profileIcon = ImageUtil.createDialogProfileImage(user.getProfilePicture());
        profileImageLabel.setIcon(profileIcon);
        profileImageLabel.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 2));
        
        JButton changePicBtn = createStyledButton("Change Profile Picture", PRIMARY_COLOR);
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
        
        JTextField tpField = new JTextField(user.getTp());
        tpField.setEditable(false);
        tpField.setFocusable(false);
        tpField.setBackground(CARD_COLOR);
        contentPanel.add(createProfileFieldEditable("ID Number:", tpField));
        contentPanel.add(Box.createVerticalStrut(15));
        
        JTextField nameField = new JTextField(user.getName());
        nameField.setEditable(false);
        nameField.setFocusable(false);
        nameField.setBackground(CARD_COLOR);
        contentPanel.add(createProfileFieldEditable("Name:", nameField));
        contentPanel.add(Box.createVerticalStrut(15));
        
        JTextField emailField = new JTextField(user.getEmail());
        emailField.setEditable(false);
        emailField.setFocusable(false);
        emailField.setBackground(CARD_COLOR);
        contentPanel.add(createProfileFieldEditable("Email:", emailField));
        contentPanel.add(Box.createVerticalStrut(15));
        
        JTextField roleField = new JTextField(user.getRole());
        roleField.setEditable(false);
        roleField.setFocusable(false);
        roleField.setBackground(CARD_COLOR);
        contentPanel.add(createProfileFieldEditable("Role:", roleField));
        contentPanel.add(Box.createVerticalStrut(20));
        
        JTextField phoneField = new JTextField(user.getPhoneNumber());
        contentPanel.add(createProfileFieldEditable("Phone Number:", phoneField));
        contentPanel.add(Box.createVerticalStrut(15));
        
        JTextField addressField = new JTextField(user.getAddress());
        contentPanel.add(createProfileFieldEditable("Address:", addressField));
        contentPanel.add(Box.createVerticalStrut(15));
        
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
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton saveBtn = createStyledButton("Save Changes", SUCCESS_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", TEXT_SECONDARY);
        
        saveBtn.addActionListener(e -> {
            String phone = phoneField.getText().trim();
            if (!phone.isEmpty() && !phone.matches("^[0-9+\\-() ]+$")) {
                showError("Invalid phone number format.");
                return;
            }
            
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
            
            user.setPhoneNumber(phone);
            user.setAddress(addressField.getText().trim());
            user.setDescription(descArea.getText().trim());
            
            boolean success = UserFileManager.update(user);
            if (success) {
                Component[] components = ((JPanel)getContentPane().getComponent(0)).getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        JPanel panel = (JPanel) comp;
                        for (Component inner : panel.getComponents()) {
                            if (inner instanceof JButton) {
                                JButton btn = (JButton) inner;
                                if (btn.getToolTipText() != null && 
                                    btn.getToolTipText().contains("profile")) {
                                    btn.setIcon(ImageUtil.createHeaderProfileImage(user.getProfilePicture()));
                                    break;
                                }
                            }
                        }
                    }
                }
                
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
        
        cancelBtn.addActionListener(e -> profileDialog.dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        profileDialog.add(buttonPanel, BorderLayout.SOUTH);
        profileDialog.setVisible(true);
    }

    private JPanel createProfileFieldEditable(String label, JTextField field) {
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

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
                                                   "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginUI().setVisible(true);
        }
    }
}
