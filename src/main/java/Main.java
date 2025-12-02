import ui.LoginUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Start the application by showing the login screen
        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });
    }
}
