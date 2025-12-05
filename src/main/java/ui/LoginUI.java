package ui;

import model.User;
import file.UserFileManager;
import util.PasswordUtil; // CHANGE: Added import for password hashing utility
import javax.swing.*;
import java.awt.*;

/**
 * CHANGE: Updated login system from TP/password to email/password
 * Now validates email format and uses SHA-256 hashed passwords
 */
public class LoginUI extends JFrame {
    private JTextField emailField; // CHANGE: Changed from tpField to emailField
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginUI() {
        setTitle("Student Consultation Management System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("SCMS Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // CHANGE: Updated label and field from "TP Number" to "Email"
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(); // CHANGE: Using emailField instead of tpField
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.addActionListener(e -> handleLogin());
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Allow Enter key to trigger login
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin() {
        // CHANGE: Get email instead of TP number
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // CHANGE: Updated validation messages to reference email
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // CHANGE: Added email format validation
        if (!PasswordUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format. Email must contain '@'.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // CHANGE: Updated to use email-based login validation with hashed password
        User user = UserFileManager.validateLoginByEmail(email, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", 
                                         "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            return;
        }

        // Login successful - open appropriate dashboard
        JOptionPane.showMessageDialog(this, "Login successful! Welcome " + user.getName(), 
                                     "Success", JOptionPane.INFORMATION_MESSAGE);
        
        dispose(); // Close login window

        // CHANGE: Maintained role-based redirection to appropriate modules
        // Student → Student Module, Lecturer → Lecturer Module, 
        // Staff → Appointment Module, Admin → Admin Module
        switch (user.getRole().toUpperCase()) {
            case "STUDENT":
                new StudentDashboard(user).setVisible(true); // Student Module
                break;
            case "LECTURER":
                new LecturerDashboard(user).setVisible(true); // Lecturer Module
                break;
            case "STAFF":
                new StaffDashboard(user).setVisible(true); // Appointment Module
                break;
            case "ADMIN":
                new AdminDashboard(user).setVisible(true); // Admin Module
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown role: " + user.getRole(), 
                                             "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });
    }
}
