package util;

/**
 * Utility program to generate SHA-256 hashed passwords for the users.txt file
 * Run this to generate hashed versions of plain text passwords
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        System.out.println("Password Hash Generator");
        System.out.println("=======================\n");
        
        // Original passwords from the system
        String[] passwords = {
            "12345",      // Mark Leo (Student)
            "abc123",     // Dr Lim (Lecturer)
            "staff123",   // Sarah Wong (Staff)
            "admin123",   // Admin User (Admin)
            "pass123",    // Jane Doe (Student)
            "lecture123"  // Prof Smith (Lecturer)
        };
        
        String[] users = {
            "mark@email.com (Student)",
            "lim@email.com (Lecturer)",
            "sarah@email.com (Staff)",
            "admin@email.com (Admin)",
            "jane@email.com (Student)",
            "smith@email.com (Lecturer)"
        };
        
        System.out.println("Generated SHA-256 Hashes:\n");
        
        for (int i = 0; i < passwords.length; i++) {
            String hashed = PasswordUtil.hashPassword(passwords[i]);
            System.out.println("User: " + users[i]);
            System.out.println("Plain: " + passwords[i]);
            System.out.println("Hash: " + hashed);
            System.out.println();
        }
        
        // Demonstrate password verification
        System.out.println("\n======================");
        System.out.println("Verification Test:");
        System.out.println("======================\n");
        
        String testPassword = "12345";
        String testHash = PasswordUtil.hashPassword(testPassword);
        
        System.out.println("Testing password: " + testPassword);
        System.out.println("Generated hash: " + testHash);
        System.out.println("Verification result: " + 
                          PasswordUtil.verifyPassword(testPassword, testHash));
        System.out.println("Wrong password test: " + 
                          PasswordUtil.verifyPassword("wrongpass", testHash));
        
        // Demonstrate email validation
        System.out.println("\n======================");
        System.out.println("Email Validation Test:");
        System.out.println("======================\n");
        
        String[] testEmails = {
            "mark@email.com",
            "invalid.email",
            "@email.com",
            "user@domain",
            ""
        };
        
        for (String email : testEmails) {
            System.out.println("Email: '" + email + "' - Valid: " + 
                              PasswordUtil.isValidEmail(email));
        }
    }
}
