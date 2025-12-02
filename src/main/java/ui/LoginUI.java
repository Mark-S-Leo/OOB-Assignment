package ui;

import model.User;
import file.UserFileManager;
import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {
    private JTextField tpField;
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

        formPanel.add(new JLabel("TP Number:"));
        tpField = new JTextField();
        formPanel.add(tpField);

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
        String tp = tpField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (tp.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both TP and password.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = UserFileManager.validateLogin(tp, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid TP or password.", 
                                         "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            return;
        }

        // Login successful - open appropriate dashboard
        JOptionPane.showMessageDialog(this, "Login successful! Welcome " + user.getName(), 
                                     "Success", JOptionPane.INFORMATION_MESSAGE);
        
        dispose(); // Close login window

        // Open dashboard based on role
        switch (user.getRole().toUpperCase()) {
            case "STUDENT":
                new StudentDashboard(user).setVisible(true);
                break;
            case "LECTURER":
                new LecturerDashboard(user).setVisible(true);
                break;
            case "STAFF":
                new StaffDashboard(user).setVisible(true);
                break;
            case "ADMIN":
                new AdminDashboard(user).setVisible(true);
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
