package ui;

import static ui.UITheme.*;

import model.User;
import model.Slot;
import model.Request;
import service.StudentService;
import file.UserFileManager;
import file.RequestFileManager;
import util.ImageUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StudentDashboard extends JFrame {
    private User user;
    private StudentService studentService;
    private Map<String, String> lecturerMap; // tp -> name
    private Map<String, String> lecturerNameToTpMap; // name -> tp (optimized reverse lookup)
    private JPopupMenu profileMenu;

    public StudentDashboard(User user) {
        this.user = user;
        this.studentService = new StudentService(user.getTp());
        this.lecturerMap = new HashMap<>();
        this.lecturerNameToTpMap = new HashMap<>();
        loadLecturerData();

        setTitle("Student Consultation Portal");
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
     * Load lecturer data once at initialization for better performance
     * Creates both forward (tp->name) and reverse (name->tp) lookups
     */
    private void loadLecturerData() {
        UserFileManager.loadAll().stream()
            .filter(u -> "LECTURER".equalsIgnoreCase(u.getRole()))
            .forEach(u -> {
                lecturerMap.put(u.getTp(), u.getName());
                lecturerNameToTpMap.put(u.getName(), u.getTp());
            });
    }
    
    private String getLecturerName(String tp) {
        return lecturerMap.getOrDefault(tp, "Unknown");
    }
    
    /**
     * Create modern header panel with profile picture and dropdown
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_XL, SPACING_XXL, SPACING_XL, SPACING_XXL));
        
        JLabel titleLabel = new JLabel("Student Consultation Portal");
        titleLabel.setFont(FONT_HEADER_LARGE);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        // Right side: Profile picture + name
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_LG, 0));
        profilePanel.setBackground(PRIMARY_COLOR);
        
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
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Create action cards with professional icons
        panel.add(createActionCard("View Available Slots", 
            "Browse consultation slots from lecturers", 
            "◷", e -> viewAvailableSlots()));
            
        panel.add(createActionCard("Create Request", 
            "Request a consultation with a lecturer", 
            "⊕", e -> createRequest()));
            
        panel.add(createActionCard("My Requests", 
            "View your consultation request history", 
            "≡", e -> viewMyRequests()));
            
        panel.add(createActionCard("My Appointments", 
            "View approved consultation appointments", 
            "✔", e -> viewMyAppointments()));
        
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
     * Create footer panel (now empty since logout moved to profile menu)
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_MD, SPACING_XL, SPACING_XL, SPACING_XL));
        return panel;
    }

    private void viewAvailableSlots() {
        ArrayList<Slot> slots = studentService.viewAvailableSlots();
        
        if (slots.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No available slots found at the moment.\nPlease check back later.", 
                "No Slots Available", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columns = {"Lecturer Name", "Date", "Time", "Slot ID"};
        Object[][] data = new Object[slots.size()][4];
        
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            data[i] = new Object[]{
                getLecturerName(slot.getLecturerTp()),
                slot.getDate(),
                slot.getStartTime() + " - " + slot.getEndTime(),
                slot.getSlotId()
            };
        }
        
        JTable table = createStyledTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Available Consultation Slots", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Create consultation request with enhanced form and validation
     */
    private void createRequest() {
        JDialog dialog = new JDialog(this, "Create Consultation Request", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
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
        JLabel titleLabel = new JLabel("Request a Consultation");
        titleLabel.setFont(FONT_HEADER_MEDIUM);
        titleLabel.setForeground(TEXT_COLOR);
        formPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        // Lecturer dropdown
        gbc.gridx = 0;
        JLabel lecturerLabel = new JLabel("Select Lecturer:");
        lecturerLabel.setFont(FONT_BODY);
        formPanel.add(lecturerLabel, gbc);
        
        gbc.gridx = 1;
        JComboBox<String> lecturerCombo = new JComboBox<>();
        lecturerCombo.addItem("-- Select Lecturer --");
        
        // Only show lecturers who have available slots
        ArrayList<Slot> availableSlots = studentService.viewAvailableSlots();
        Map<String, String> lecturersWithSlots = new HashMap<>();
        for (Slot slot : availableSlots) {
            String lecturerName = lecturerMap.get(slot.getLecturerTp());
            if (lecturerName != null) {
                lecturersWithSlots.put(slot.getLecturerTp(), lecturerName);
            }
        }
        for (String lecturerName : lecturersWithSlots.values()) {
            lecturerCombo.addItem(lecturerName);
        }
        
        lecturerCombo.setFont(FONT_BODY);
        formPanel.add(lecturerCombo, gbc);
        
        // Slot dropdown
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel slotLabel = new JLabel("Select Time Slot:");
        slotLabel.setFont(FONT_BODY);
        formPanel.add(slotLabel, gbc);
        
        gbc.gridx = 1;
        JComboBox<String> slotCombo = new JComboBox<>();
        slotCombo.addItem("-- Select Slot --");
        slotCombo.setFont(FONT_BODY);
        slotCombo.setEnabled(false);
        formPanel.add(slotCombo, gbc);
        
        // Map to store slot IDs
        Map<String, String> slotIdMap = new HashMap<>();
        
        // Update slots when lecturer is selected
        lecturerCombo.addActionListener(e -> {
            String selectedLecturer = (String) lecturerCombo.getSelectedItem();
            slotCombo.removeAllItems();
            slotCombo.addItem("-- Select Slot --");
            slotIdMap.clear();
            
            if (selectedLecturer != null && !selectedLecturer.startsWith("--")) {
                // Use optimized reverse lookup map instead of iterating
                String lecturerTp = lecturerNameToTpMap.get(selectedLecturer);
                
                if (lecturerTp != null) {
                    ArrayList<Slot> slots = studentService.viewAvailableSlots();
                    
                    // Filter and sort in one pass
                    slots.stream()
                        .filter(slot -> slot.getLecturerTp().equals(lecturerTp))
                        .sorted((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()))
                        .forEach(slot -> {
                            String displayText = slot.getDate() + " | " + 
                                               slot.getStartTime() + " - " + slot.getEndTime();
                            slotCombo.addItem(displayText);
                            slotIdMap.put(displayText, slot.getSlotId());
                        });
                    
                    slotCombo.setEnabled(slotCombo.getItemCount() > 1);
                }
            }
        });
        
        // Reason text area
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel reasonLabel = new JLabel("Reason:");
        reasonLabel.setFont(FONT_BODY);
        formPanel.add(reasonLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JTextArea reasonArea = new JTextArea(8, 30);
        reasonArea.setFont(FONT_BODY);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER),
            BorderFactory.createEmptyBorder(SPACING_SM, SPACING_SM, SPACING_SM, SPACING_SM)
        ));
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        formPanel.add(reasonScroll, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton submitBtn = createStyledButton("Create Request", PRIMARY_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", TEXT_SECONDARY);
        
        submitBtn.addActionListener(e -> {
            String selectedLecturer = (String) lecturerCombo.getSelectedItem();
            String selectedSlot = (String) slotCombo.getSelectedItem();
            String reason = reasonArea.getText();
            
            if (!validateRequestInput(selectedLecturer, selectedSlot, reason)) return;
            
            // Use optimized reverse lookup instead of stream
            String lecturerTp = lecturerNameToTpMap.get(selectedLecturer);
            String slotId = slotIdMap.get(selectedSlot);
            
            if (lecturerTp != null && slotId != null && 
                studentService.createConsultationRequest(lecturerTp, slotId, reason.trim())) {
                JOptionPane.showMessageDialog(dialog, 
                    "Request submitted successfully!\nYou will be notified once approved.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                showError("Failed to create request. The slot may no longer be available.");
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Let the layout compute correct sizes so buttons are visible
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * View student's consultation requests in a modern table with cancellation
     */
    private void viewMyRequests() {
        ArrayList<Request> allRequests = studentService.viewOwnRequests();
        
        // All requests in requests.txt are PENDING (approved/cancelled go to appointments)
        if (allRequests.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "You have no pending consultation requests.", 
                "No Requests", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create dialog to hold table and buttons
        JDialog dialog = new JDialog(this, "My Consultation Requests", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Create table data - removed Cancel Reason column
        String[] columns = {"Request ID", "Lecturer", "Date", "Time", "Status", "Reason"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        allRequests.forEach(req -> {
            String reason = req.getReason();
            tableModel.addRow(new Object[]{
                req.getRequestId(),
                getLecturerName(req.getLecturerTp()),
                req.getDate(),
                req.getStartTime() + " - " + req.getEndTime(),
                req.getStatus(),
                reason.length() > 30 ? reason.substring(0, 30) + "..." : reason
            });
        });
        
        JTable table = new JTable(tableModel);
        table.setFont(FONT_SMALL);
        table.setRowHeight(TABLE_ROW_HEIGHT);
        table.setGridColor(GRID_COLOR);
        table.setSelectionBackground(SELECTION_COLOR);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setPreferredScrollableViewportSize(new Dimension(850, 350));
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
        JButton cancelBtn = createStyledButton("Cancel Request", DANGER_COLOR);

        
        cancelBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                showError("Please select a request to cancel.");
                return;
            }
            
            String requestId = (String) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 4);
            
            if (!status.equalsIgnoreCase("PENDING")) {
                showError("Only pending requests can be cancelled.\nThis request is already " + status + ".");
                return;
            }
            
            showCancelRequestDialog(requestId, dialog, tableModel, selectedRow);
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeBtn);
        buttonPanel.add(cancelBtn);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set proper dialog size and center
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);
        
        // Add right-click context menu
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem cancelMenuItem = new JMenuItem("⊗ Cancel Request");
        cancelMenuItem.setFont(FONT_SMALL);
        cancelMenuItem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String requestId = (String) tableModel.getValueAt(row, 0);
                String status = (String) tableModel.getValueAt(row, 4);
                
                if (status.equalsIgnoreCase("PENDING")) {
                    showCancelRequestDialog(requestId, dialog, tableModel, row);
                } else {
                    showError("Only pending requests can be cancelled.");
                }
            }
        });
        contextMenu.add(cancelMenuItem);
        
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }
            
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }
            
            private void showContextMenu(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0 && row < table.getRowCount()) {
                    table.setRowSelectionInterval(row, row);
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        dialog.setVisible(true);
    }
    
    /**
     * Show dialog to input cancellation reason
     */
    private void showCancelRequestDialog(String requestId, JDialog parentDialog, 
                                        DefaultTableModel tableModel, int row) {
        JDialog cancelDialog = new JDialog(parentDialog, "Cancel Request", true);
        cancelDialog.setLayout(new BorderLayout(10, 10));
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Please provide a reason for cancellation:");
        titleLabel.setFont(FONT_SUBHEADER);
        titleLabel.setForeground(TEXT_COLOR);
        
        JTextArea reasonArea = new JTextArea(5, 30);
        reasonArea.setFont(FONT_BODY);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER),
            BorderFactory.createEmptyBorder(SPACING_SM, SPACING_SM, SPACING_SM, SPACING_SM)
        ));
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(reasonScroll, BorderLayout.CENTER);
        
        cancelDialog.add(contentPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        
        JButton confirmBtn = createStyledButton("Confirm Cancel", DANGER_COLOR);
        JButton backBtn = createStyledButton("Back", TEXT_SECONDARY);
        
        confirmBtn.addActionListener(e -> {
            String reason = reasonArea.getText().trim();
            if (reason.isEmpty()) {
                showError("Please provide a reason for cancellation.");
                return;
            }
            
            if (reason.length() < 10) {
                showError("Cancellation reason must be at least 10 characters.");
                return;
            }
            
            // Cancel the request
            boolean success = RequestFileManager.cancelRequest(requestId, reason);
            if (success) {
                JOptionPane.showMessageDialog(cancelDialog,
                    "Request cancelled successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                cancelDialog.dispose();
                
                // Remove the row from table
                tableModel.removeRow(row);
                
                // Close parent dialog if no more rows
                if (tableModel.getRowCount() == 0) {
                    parentDialog.dispose();
                }
            } else {
                showError("Failed to cancel request. It may have already been processed.");
            }
        });
        
        backBtn.addActionListener(e -> cancelDialog.dispose());
        
        btnPanel.add(backBtn);
        btnPanel.add(confirmBtn);
        
        cancelDialog.add(btnPanel, BorderLayout.SOUTH);
        
        // Set proper size and center the dialog
        cancelDialog.setSize(450, 300);
        cancelDialog.setLocationRelativeTo(parentDialog);
        cancelDialog.setVisible(true);
    }
    
    private void viewMyAppointments() {
        ArrayList<model.Appointment> appointments = studentService.viewOwnAppointments();
        
        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "You have no appointments.", 
                "No Appointments", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog(this, "My Appointments", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);

        // Main panel with vertical layout for appointments
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        // Header panel
        JPanel headerPanel = new JPanel(new GridLayout(1, 5));
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        headerPanel.setPreferredSize(new Dimension(750, 35));
        
        String[] headers = {"Appointment ID", "Lecturer", "Date", "Time", "Status"};
        for (String header : headers) {
            JLabel headerLabel = new JLabel(header);
            headerLabel.setFont(FONT_TABLE_HEADER);
            headerLabel.setForeground(TEXT_COLOR);
            headerPanel.add(headerLabel);
            
        }
        mainPanel.add(headerPanel);

        // Track expanded states
        java.util.Set<String> expandedApts = new java.util.HashSet<>();

        // Create appointment rows
        for (model.Appointment apt : appointments) {
            JPanel rowContainer = new JPanel();
            rowContainer.setLayout(new BoxLayout(rowContainer, BoxLayout.Y_AXIS));
            rowContainer.setBackground(Color.WHITE);

            // Main appointment row
            JPanel aptRow = new JPanel(new GridLayout(1, 5));
            aptRow.setBackground(Color.WHITE);
            aptRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID_COLOR));
            aptRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, TABLE_ROW_HEIGHT));
            aptRow.setPreferredSize(new Dimension(750, TABLE_ROW_HEIGHT));

            JLabel idLabel = new JLabel(apt.getAppointmentId());
            JLabel lecLabel = new JLabel(lecturerMap.getOrDefault(apt.getLecturerTp(), apt.getLecturerTp()));
            JLabel dateLabel = new JLabel(apt.getDate());
            JLabel timeLabel = new JLabel(apt.getStartTime());
            
            JLabel statusLabel = new JLabel(apt.getStatus().equalsIgnoreCase("CANCELLED") ? "Cancelled ▼" : apt.getStatus());
            
            idLabel.setFont(FONT_SMALL);
            lecLabel.setFont(FONT_SMALL);
            dateLabel.setFont(FONT_SMALL);
            timeLabel.setFont(FONT_SMALL);
            statusLabel.setFont(FONT_SMALL);

            aptRow.add(idLabel);
            aptRow.add(lecLabel);
            aptRow.add(dateLabel);
            aptRow.add(timeLabel);
            aptRow.add(statusLabel);

            rowContainer.add(aptRow);

            // Add click listener for cancelled appointments
            if (apt.getStatus().equalsIgnoreCase("CANCELLED")) {
                aptRow.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                aptRow.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        String aptId = apt.getAppointmentId();
                        if (expandedApts.contains(aptId)) {
                            // Collapse - remove reason panel
                            if (rowContainer.getComponentCount() > 1) {
                                rowContainer.remove(1);
                            }
                            expandedApts.remove(aptId);
                            statusLabel.setText("Cancelled ▼");
                        } else {
                            // Expand - add reason panel
                            String reason = apt.getCancelReason();
                            if (reason == null || reason.trim().isEmpty()) {
                                reason = "No cancellation reason provided.";
                            }

                            JPanel reasonPanel = new JPanel(new BorderLayout());
                            reasonPanel.setBackground(new Color(255, 250, 240));
                            reasonPanel.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(0, 0, 1, 0, GRID_COLOR),
                                BorderFactory.createEmptyBorder(10, 15, 10, 15)
                            ));
                            reasonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

                            JLabel reasonLabel = new JLabel("Reason: " + reason);
                            reasonLabel.setFont(FONT_SMALL.deriveFont(java.awt.Font.ITALIC));
                            reasonPanel.add(reasonLabel, BorderLayout.WEST);

                            rowContainer.add(reasonPanel);
                            expandedApts.add(aptId);
                            statusLabel.setText("Cancelled ▲");
                        }
                        
                        rowContainer.revalidate();
                        rowContainer.repaint();
                    }
                });
            }

            mainPanel.add(rowContainer);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JButton closeBtn = createStyledButton("Close", TEXT_SECONDARY);
        closeBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
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

    private boolean validateRequestInput(String lecturer, String slot, String reason) {
        if (lecturer == null || lecturer.startsWith("--")) {
            showError("Please select a lecturer.");
            return false;
        }
        if (slot == null || slot.startsWith("--")) {
            showError("Please select a time slot.");
            return false;
        }
        if (reason.trim().isEmpty()) {
            showError("Please provide a reason for the consultation.");
            return false;
        }
        if (reason.trim().length() < 10) {
            showError("Reason must be at least 10 characters long.");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, 
            "Validation Error", JOptionPane.ERROR_MESSAGE);
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
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Profile Management");
        titleLabel.setFont(FONT_HEADER_LARGE);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(25));
        
        // Profile Picture Section with circular image
        JPanel picPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        picPanel.setBackground(Color.WHITE);
        picPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        picPanel.setMaximumSize(new Dimension(650, 150));
        
        // Circular profile image
        JLabel profileImageLabel = new JLabel();
        ImageIcon profileIcon = ImageUtil.createDialogProfileImage(user.getProfilePicture());
        profileImageLabel.setIcon(profileIcon);
        profileImageLabel.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 2));
        
        // Change picture button
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
                
                // Validate image file
                if (!ImageUtil.isValidImageFile(selectedFile)) {
                    showError("Invalid file type. Please select a JPG or PNG image.");
                    return;
                }
                
                // Update profile image
                String imagePath = selectedFile.getAbsolutePath();
                user.setProfilePicture(imagePath);
                
                // Refresh the image display
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
        
        // Read-only fields (non-editable, no cursor)
        JTextField tpField = new JTextField(user.getTp());
        tpField.setEditable(false);
        tpField.setFocusable(false);
        tpField.setBackground(CARD_COLOR);
        contentPanel.add(createProfileFieldEditable("TP Number:", tpField));
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
        
        // Editable fields
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
        
        // Password Change Section - Centered
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
        JButton cancelBtn = createStyledButton("Cancel", TEXT_SECONDARY);
        
        saveBtn.addActionListener(e -> {
            // Validate phone number
            String phone = phoneField.getText().trim();
            if (!phone.isEmpty() && !phone.matches("^[0-9+\\-() ]+$")) {
                showError("Invalid phone number format.");
                return;
            }
            
            // Handle password change (plain text)
            String currentPass = new String(currentPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());
            
            if (!currentPass.isEmpty() || !newPass.isEmpty() || !confirmPass.isEmpty()) {
                // Validate current password (plain text comparison)
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
                
                // Set new password (plain text)
                user.setPassword(newPass);
            }
            
            // Update profile fields (profilePicture already set by image chooser)
            user.setPhoneNumber(phone);
            user.setAddress(addressField.getText().trim());
            user.setDescription(descArea.getText().trim());
            
            // Save to file
            boolean success = UserFileManager.update(user);
            if (success) {
                // Update header profile image
                ImageIcon newHeaderIcon = ImageUtil.createHeaderProfileImage(user.getProfilePicture());
                // Find and update the profile button in header
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
                
                JOptionPane.showMessageDialog(profileDialog,
                    "Profile updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                profileDialog.dispose();
                
                // If password changed, logout for security
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
