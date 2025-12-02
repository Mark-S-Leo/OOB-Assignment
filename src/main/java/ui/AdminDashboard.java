package ui;

import model.User;
import service.AdminService;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {
    private User user;
    private AdminService adminService;

    public AdminDashboard(User user) {
        this.user = user;
        this.adminService = new AdminService();

        setTitle("Admin Dashboard - " + user.getName());
        setSize(600, 450);
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
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JButton createUserBtn = new JButton("Create User");
        JButton viewUsersBtn = new JButton("View All Users");
        JButton searchUserBtn = new JButton("Search User");
        JButton updateUserBtn = new JButton("Update User");
        JButton deleteUserBtn = new JButton("Delete User");
        JButton logoutBtn = new JButton("Logout");

        createUserBtn.addActionListener(e -> createUser());
        viewUsersBtn.addActionListener(e -> viewAllUsers());
        searchUserBtn.addActionListener(e -> searchUser());
        updateUserBtn.addActionListener(e -> updateUser());
        deleteUserBtn.addActionListener(e -> deleteUser());
        logoutBtn.addActionListener(e -> logout());

        buttonPanel.add(createUserBtn);
        buttonPanel.add(viewUsersBtn);
        buttonPanel.add(searchUserBtn);
        buttonPanel.add(updateUserBtn);
        buttonPanel.add(deleteUserBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void createUser() {
        JTextField tpField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"STUDENT", "LECTURER", "STAFF", "ADMIN"});
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        
        Object[] message = {
            "TP Number:", tpField,
            "Role:", roleBox,
            "Name:", nameField,
            "Email:", emailField,
            "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create New User", 
                                                  JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String tp = tpField.getText().trim();
            String role = (String) roleBox.getSelectedItem();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (tp.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = adminService.createUser(tp, role, name, email, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "User created successfully!", 
                                             "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create user. TP may already exist.", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewAllUsers() {
        ArrayList<User> users = adminService.viewAllUsers();
        
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No users found.", 
                                         "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("All Users:\n\n");
        for (User u : users) {
            sb.append(String.format("TP: %s | Role: %s | Name: %s | Email: %s\n", 
                     u.getTp(), u.getRole(), u.getName(), u.getEmail()));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "All Users", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchUser() {
        String tp = JOptionPane.showInputDialog(this, "Enter TP Number:");
        
        if (tp == null || tp.trim().isEmpty()) {
            return;
        }

        User foundUser = adminService.searchUser(tp.trim());
        
        if (foundUser == null) {
            JOptionPane.showMessageDialog(this, "User not found.", 
                                         "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String info = String.format("TP: %s\nRole: %s\nName: %s\nEmail: %s", 
                                       foundUser.getTp(), foundUser.getRole(), 
                                       foundUser.getName(), foundUser.getEmail());
            JOptionPane.showMessageDialog(this, info, "User Found", 
                                         JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateUser() {
        String tp = JOptionPane.showInputDialog(this, "Enter TP Number to update:");
        
        if (tp == null || tp.trim().isEmpty()) {
            return;
        }

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        
        Object[] message = {
            "New Name (leave empty to keep current):", nameField,
            "New Email (leave empty to keep current):", emailField,
            "New Password (leave empty to keep current):", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update User", 
                                                  JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            boolean success = adminService.updateUser(tp.trim(), name, email, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "User updated successfully!", 
                                             "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update user. User may not exist.", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteUser() {
        String tp = JOptionPane.showInputDialog(this, "Enter TP Number to delete:");
        
        if (tp == null || tp.trim().isEmpty()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                                                   "Are you sure you want to delete user " + tp + "?", 
                                                   "Confirm Delete", 
                                                   JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = adminService.deleteUser(tp.trim());
            if (success) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!", 
                                             "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user. User may not exist.", 
                                             "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
