package ui;



import model.User;
import file.UserFileManager;
import javax.swing.*;
import java.awt.*;

/**
 * LoginUI provides email/password authentication.
 * Updated from TP-based to email-based login system.
 */
public class LoginUI extends JFrame {
    private JTextField emailField;
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

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
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

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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
        
        // Create dashboard on EDT to ensure proper Swing thread handling
        SwingUtilities.invokeLater(() -> {
            dispose(); // Close login window after switching to EDT
            
            try {
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
                        JOptionPane.showMessageDialog(null, "Unknown role: " + user.getRole(), 
                                                     "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error loading dashboard: " + e.getMessage() + "\nCheck console for details.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });
    }
}
