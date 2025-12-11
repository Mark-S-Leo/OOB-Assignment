package ui;

import static ui.UITheme.*;

import file.UserFileManager;
import model.User;
import service.AdminService;
import util.ImageUtil;
import util.UserIdGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;


public class AdminDashboard extends JFrame {
    private User user;
    private AdminService adminService;
    private JPopupMenu profileMenu;

    /**
     * Constructor - Initialize admin dashboard
     * @param user The authenticated admin user
     */
    public AdminDashboard(User user) {
        this.user = user;
        this.adminService = new AdminService();

        setTitle("User Management Module");
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
     * Create header panel with title and profile menu
     * @return JPanel containing header elements
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ADMIN_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_XL, SPACING_XXL, SPACING_XL, SPACING_XXL));
        
        JLabel titleLabel = new JLabel("User Management Module");
        titleLabel.setFont(FONT_HEADER_LARGE);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_LG, 0));
        profilePanel.setBackground(ADMIN_COLOR);
        
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

    /**
     * Create main panel with action cards for user management
     * @return JPanel containing action cards
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_XXL, SPACING_XXL, SPACING_XXL, SPACING_XXL));
        panel.setBackground(BACKGROUND_COLOR);
        
        panel.add(createActionCard("Create User", "Add new users to the system", "⊕", e -> createUser()));
        panel.add(createActionCard("Manage Users", "View, edit, and delete users", "≡", e -> manageUsers()));
        
        return panel;
    }
    
    /**
     * Create action card with icon, title, and description
     * @param title Card title
     * @param description Card description
     * @param icon Icon character to display
     * @param action Action listener for card click
     * @return JPanel configured as action card
     */
    private JPanel createActionCard(String title, String description, String icon, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(SPACING_SM, SPACING_SM));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(SPACING_LG, SPACING_LG, SPACING_LG, SPACING_LG)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_HEADER_SMALL);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(FONT_SMALL);
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, SPACING_SM));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel);
        textPanel.add(descLabel);
        
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                card.setBackground(HOVER_COLOR);
                textPanel.setBackground(HOVER_COLOR);
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                card.setBackground(Color.WHITE);
                textPanel.setBackground(Color.WHITE);
            }
            @Override
            public void mousePressed(MouseEvent evt) {
                action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, null));
            }
        });
        
        return card;
    }
    
    /**
     * Create footer panel with copyright information
     * @return JPanel containing footer elements
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_LG, 0));
        

        
        return panel;
    }
    
    /**
     * Open dialog to create new user
     * Auto-generates ID based on role, validates email format
     */
    private void createUser() {
        JDialog dialog = new JDialog(this, "Create New User", true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(SPACING_LG, SPACING_LG));
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, SPACING_MD, SPACING_MD));
        formPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_XL, SPACING_XL, SPACING_MD, SPACING_XL));
        formPanel.setBackground(Color.WHITE);
        
        // Role selection comes first
        String[] roles = {"Student", "Lecturer", "Staff", "Admin"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(FONT_BODY);
        
        // ID field (auto-generated, completely non-editable)
        JLabel idField = new JLabel("(Auto-generated after selecting role)");
        idField.setFont(FONT_BODY);
        idField.setOpaque(true);
        idField.setBackground(DISABLED_COLOR);
        idField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        idField.setForeground(TEXT_SECONDARY);
        
        JTextField nameField = new JTextField();
        nameField.setFont(FONT_BODY);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(FONT_BODY);
        JTextField emailField = new JTextField();
        emailField.setFont(FONT_BODY);
        
        // Profile picture selection with preview
        JPanel profilePicPanel = new JPanel(new BorderLayout(SPACING_SM, 0));
        profilePicPanel.setBackground(Color.WHITE);
        
        JLabel profilePreview = new JLabel();
        profilePreview.setPreferredSize(new Dimension(40, 40));
        profilePreview.setHorizontalAlignment(SwingConstants.CENTER);
        profilePreview.setIcon(ImageUtil.createCircularProfileImage("default", 40));
        
        JTextField profilePicPath = new JTextField("default");
        profilePicPath.setEditable(false);
        profilePicPath.setFont(FONT_SMALL);
        profilePicPath.setBackground(Color.WHITE);
        
        JButton browseBtn = new JButton("Browse...");
        browseBtn.setFont(FONT_SMALL);
        browseBtn.setPreferredSize(new Dimension(90, 30));
        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || ImageUtil.isValidImageFile(f);
                }
                @Override
                public String getDescription() {
                    return "Image Files (*.jpg, *.jpeg, *.png)";
                }
            });
            
            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String path = selectedFile.getAbsolutePath();
                profilePicPath.setText(path);
                profilePreview.setIcon(ImageUtil.createCircularProfileImage(path, 40));
            }
        });
        
        JPanel picInputPanel = new JPanel(new BorderLayout(SPACING_SM, 0));
        picInputPanel.setBackground(Color.WHITE);
        picInputPanel.add(profilePicPath, BorderLayout.CENTER);
        picInputPanel.add(browseBtn, BorderLayout.EAST);
        
        profilePicPanel.add(profilePreview, BorderLayout.WEST);
        profilePicPanel.add(picInputPanel, BorderLayout.CENTER);
        
        // Store the generated ID
        final String[] generatedId = new String[1];
        
        // When role changes, generate new ID
        roleCombo.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            ArrayList<User> existingUsers = adminService.viewAllUsers();
            String newId = UserIdGenerator.generateUserId(role, "", existingUsers);
            generatedId[0] = newId;
            idField.setText(newId);
            idField.setForeground(TEXT_COLOR);
        });
        
        // Trigger initial ID generation
        roleCombo.setSelectedIndex(0);
        
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleCombo);
        formPanel.add(new JLabel("ID (Auto):"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Profile Picture:"));
        formPanel.add(profilePicPanel);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_MD, SPACING_MD));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_MD, 0, SPACING_LG, 0));
        
        JButton createBtn = createStyledButton("Create User", ADMIN_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", TEXT_SECONDARY);
        
        createBtn.addActionListener(e -> {
            String tp = generatedId[0];
            String role = (String) roleCombo.getSelectedItem();
            String name = nameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String email = emailField.getText().trim();
            String profilePic = profilePicPath.getText().trim();
            
            // Validation
            if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
                showError("Name, Password, and Email are required!");
                return;
            }
            
            // Email validation
            if (!isValidEmail(email)) {
                showError("Invalid email format!\n\nPlease enter a valid email address (e.g., user@example.com)");
                return;
            }
            
            if (tp == null || tp.isEmpty()) {
                showError("Please select a role to generate an ID!");
                return;
            }
            
            boolean success = adminService.createUser(tp, role, name, email, password);
            if (success) {
                // Update additional fields
                User user = UserFileManager.findById(tp);
                if (user != null) {
                    user.setProfilePicture(profilePic);
                    UserFileManager.update(user);
                }
                JOptionPane.showMessageDialog(dialog, 
                    "User created successfully!\n\nID: " + tp + "\nName: " + name + "\nRole: " + role, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                showError("Failed to create user. User may already exist.");
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(createBtn);
        buttonPanel.add(cancelBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Open dialog to manage all users
     * Displays table of users, allows clicking to edit
     */
    private void manageUsers() {
        ArrayList<User> users = adminService.viewAllUsers();
        
        String[] columnNames = {"ID", "Name", "Role", "Email"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (User u : users) {
            model.addRow(new Object[]{
                u.getTp(),
                u.getName(),
                u.getRole(),
                u.getEmail(),
            });
        }
        
        JTable table = new JTable(model);
        table.setFont(FONT_BODY);
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(103, 58, 183, 40));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                
                if (row >= 0) {
                    String userId = (String) table.getValueAt(row, 0);
                    User user = UserFileManager.findById(userId);
                    
                    if (user != null) {
                        openEditUserDialog(user, model, row);
                    }
                }
            }
        });
        
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER_SMALL);
        header.setBackground(ADMIN_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        JDialog dialog = new JDialog(this, "Manage Users", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(SPACING_LG, SPACING_LG));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_LG, SPACING_LG, SPACING_SM, SPACING_LG));
        
        JLabel infoLabel = new JLabel("Click on any user to edit");
        infoLabel.setFont(FONT_SMALL);
        infoLabel.setForeground(TEXT_SECONDARY);
        headerPanel.add(infoLabel, BorderLayout.WEST);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, SPACING_LG, SPACING_LG, SPACING_LG));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeBtn = createStyledButton("Close", TEXT_SECONDARY);
        closeBtn.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeBtn);
        
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Open dialog to edit user details with delete functionality
     * @param user User object to edit
     * @param tableModel Table model to update after changes
     * @param row Row index in table
     */
    private void openEditUserDialog(User user, DefaultTableModel tableModel, int row) {
        JDialog dialog = new JDialog(this, "Edit User: " + user.getTp(), true);
        dialog.setSize(500, 580);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(SPACING_LG, SPACING_LG));
        
        JPanel mainPanel = new JPanel(new BorderLayout(SPACING_MD, SPACING_MD));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_XL, SPACING_XL, SPACING_XL, SPACING_XL));
        mainPanel.setBackground(Color.WHITE);
        
        JPanel formPanel = new JPanel(new GridLayout(7, 2, SPACING_MD, SPACING_MD));
        formPanel.setBackground(Color.WHITE);
        
        JTextField idField = new JTextField(user.getTp());
        idField.setEditable(false);
        idField.setBackground(DISABLED_COLOR);
        
        JTextField roleField = new JTextField(user.getRole());
        roleField.setEditable(false);
        roleField.setBackground(DISABLED_COLOR);
        
        JTextField nameField = new JTextField(user.getName());
        JPasswordField passwordField = new JPasswordField();
        passwordField.setToolTipText("Leave empty to keep current password");
        JTextField emailField = new JTextField(user.getEmail());
        
        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("New Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        
        // Delete section container (initially just has the delete button)
        JPanel deleteSectionContainer = new JPanel();
        deleteSectionContainer.setLayout(new BoxLayout(deleteSectionContainer, BoxLayout.Y_AXIS));
        deleteSectionContainer.setBackground(Color.WHITE);
        
        // Initial delete button
        JPanel deleteButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        deleteButtonPanel.setBackground(Color.WHITE);
        
        JButton deleteBtn = createStyledButton("Delete User", DANGER_COLOR);
        deleteButtonPanel.add(deleteBtn);
        deleteSectionContainer.add(deleteButtonPanel);
        
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog, 
                "Are you sure you want to delete " + user.getName() + " (" + user.getTp() + ")?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Remove the initial button and show the final warning panel
                deleteSectionContainer.removeAll();
                
                JPanel finalWarningPanel = new JPanel();
                finalWarningPanel.setLayout(new BoxLayout(finalWarningPanel, BoxLayout.Y_AXIS));
                finalWarningPanel.setBackground(new Color(255, 245, 245));
                finalWarningPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 205, 210), 2),
                    BorderFactory.createEmptyBorder(SPACING_SM, SPACING_MD, SPACING_SM, SPACING_MD)
                ));
                finalWarningPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
                
                JLabel warningLabel = new JLabel("⚠️ Delete this user permanently?");
                warningLabel.setFont(FONT_BODY);
                warningLabel.setForeground(DANGER_COLOR);
                warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                JLabel warningText = new JLabel("<html><center>This action cannot be undone!<br>User: " + user.getName() + " (" + user.getTp() + ")</center></html>");
                warningText.setFont(FONT_SMALL);
                warningText.setForeground(TEXT_SECONDARY);
                warningText.setAlignmentX(Component.CENTER_ALIGNMENT);
                warningText.setBorder(BorderFactory.createEmptyBorder(SPACING_XS, 0, SPACING_SM, 0));
                
                JPanel confirmButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_SM, 0));
                confirmButtonPanel.setBackground(new Color(255, 245, 245));
                confirmButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                confirmButtonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                
                JButton confirmDeleteBtn = createStyledButton("Confirm Delete", DANGER_COLOR);
                JButton cancelDeleteBtn = createStyledButton("Cancel", TEXT_SECONDARY);
                
                confirmDeleteBtn.addActionListener(evt -> {
                    boolean success = adminService.deleteUser(user.getTp());
                    if (success) {
                        tableModel.removeRow(row);
                        JOptionPane.showMessageDialog(dialog, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        showError("Failed to delete user.");
                    }
                });
                
                cancelDeleteBtn.addActionListener(evt -> {
                    // Restore the original delete button
                    deleteSectionContainer.removeAll();
                    deleteSectionContainer.add(deleteButtonPanel);
                    deleteSectionContainer.revalidate();
                    deleteSectionContainer.repaint();
                });
                
                confirmButtonPanel.add(confirmDeleteBtn);
                confirmButtonPanel.add(cancelDeleteBtn);
                
                finalWarningPanel.add(warningLabel);
                finalWarningPanel.add(warningText);
                finalWarningPanel.add(confirmButtonPanel);
                
                deleteSectionContainer.add(finalWarningPanel);
                deleteSectionContainer.revalidate();
                deleteSectionContainer.repaint();
            }
        });
        
        mainPanel.add(deleteSectionContainer, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_MD, SPACING_MD));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveBtn = createStyledButton("Save Changes", ADMIN_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", TEXT_SECONDARY);
        
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String email = emailField.getText().trim();

            
            if (name.isEmpty() || email.isEmpty()) {
                showError("Name and Email cannot be empty!");
                return;
            }
            
            if (!isValidEmail(email)) {
                showError("Invalid email format!");
                return;
            }
            
            boolean success = adminService.updateUser(user.getTp(), name, email, password.isEmpty() ? null : password);
            if (success) {
                User updatedUser = UserFileManager.findById(user.getTp());
                if (updatedUser != null) {
                    UserFileManager.update(updatedUser);
                    
                    tableModel.setValueAt(updatedUser.getName(), row, 1);
                    tableModel.setValueAt(updatedUser.getEmail(), row, 3);
                    tableModel.setValueAt(updatedUser.getPhoneNumber(), row, 4);
                }
                JOptionPane.showMessageDialog(dialog, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                showError("Failed to update user.");
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Open profile dialog for admin to view/edit their profile
     * Includes circular profile picture, editable fields, and password change
     */
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
        titleLabel.setForeground(ADMIN_COLOR);
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
        profileImageLabel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));
        
        JButton changePicBtn = createStyledButton("Change Profile Picture", ADMIN_COLOR);
        changePicBtn.setPreferredSize(new Dimension(200, 35));
        changePicBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Profile Picture");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));
            
            int result = fileChooser.showOpenDialog(profileDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
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
        tpField.setBackground(DISABLED_COLOR);
        contentPanel.add(createProfileFieldPanel("TP Number:", tpField));
        contentPanel.add(Box.createVerticalStrut(15));
        
        JTextField nameField = new JTextField(user.getName());
        nameField.setEditable(false);
        nameField.setFocusable(false);
        nameField.setBackground(DISABLED_COLOR);
        contentPanel.add(createProfileFieldPanel("Name:", nameField));
        contentPanel.add(Box.createVerticalStrut(15));
        
        JTextField emailField = new JTextField(user.getEmail());
        emailField.setEditable(false);
        emailField.setFocusable(false);
        emailField.setBackground(DISABLED_COLOR);
        contentPanel.add(createProfileFieldPanel("Email:", emailField));
        contentPanel.add(Box.createVerticalStrut(15));
        
        JTextField roleField = new JTextField(user.getRole());
        roleField.setEditable(false);
        roleField.setFocusable(false);
        roleField.setBackground(DISABLED_COLOR);
        contentPanel.add(createProfileFieldPanel("Role:", roleField));
        contentPanel.add(Box.createVerticalStrut(20));
        
        JTextField phoneField = new JTextField(user.getPhoneNumber());
        contentPanel.add(createProfileFieldPanel("Phone Number:", phoneField));
        contentPanel.add(Box.createVerticalStrut(15));
        
        JTextField addressField = new JTextField(user.getAddress());
        contentPanel.add(createProfileFieldPanel("Address:", addressField));
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
        descLabel.setFont(FONT_HEADER_SMALL);
        descPanel.add(descLabel, BorderLayout.NORTH);
        descPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);
        
        contentPanel.add(descPanel);
        contentPanel.add(Box.createVerticalStrut(25));
        
        JPanel passPanel = new JPanel();
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
        passPanel.setBackground(HOVER_COLOR);
        passPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        passPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passPanel.setMaximumSize(new Dimension(550, 180));
        
        JLabel passTitle = new JLabel("Change Password");
        passTitle.setFont(FONT_HEADER_SMALL);
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
        
        JButton saveBtn = createStyledButton("Save Changes", ADMIN_COLOR);
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
    
    /**
     * Create panel with label and text field for profile editing
     * @param label Field label text
     * @param field JTextField component
     * @return JPanel containing label and field
     */
    private JPanel createProfileFieldPanel(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(600, 40));
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_HEADER_SMALL);
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
    
    /**
     * Create panel with label and password field for password change section
     * @param label Field label text
     * @param field JPasswordField component
     * @return JPanel containing label and password field
     */
    private JPanel createPasswordFieldPanel(String label, JPasswordField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(HOVER_COLOR);
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
    
    /**
     * Create styled button with hover effect
     * @param text Button text
     * @param bgColor Background color
     * @return JButton with styling and hover effect
     */
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
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Display error message dialog
     * @param message Error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Validate email format
     * Checks for basic email pattern: text@text.text
     * @param email Email address to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Basic email regex pattern
        // Matches: username@domain.extension
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailPattern);
    }
    
    /**
     * Logout with confirmation and return to login screen
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginUI().setVisible(true);
        }
    }
}
