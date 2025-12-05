package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for password hashing and validation
 * CHANGE: Added SHA-256 password hashing for security
 */
public class PasswordUtil {
    
    /**
     * Hashes a password using SHA-256 algorithm
     * @param password Plain text password
     * @return Hashed password as hexadecimal string
     */
    public static String hashPassword(String password) {
        try {
            // Create SHA-256 MessageDigest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Apply hash function to password bytes
            byte[] hashBytes = digest.digest(password.getBytes());
            
            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Validates if a plain password matches a hashed password
     * @param plainPassword Plain text password to check
     * @param hashedPassword Stored hashed password
     * @return true if passwords match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        String hashOfInput = hashPassword(plainPassword);
        return hashOfInput.equals(hashedPassword);
    }
    
    /**
     * Validates email format
     * CHANGE: Added email validation to ensure proper format
     * @param email Email address to validate
     * @return true if email contains '@', false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.indexOf("@") > 0 
               && email.indexOf("@") < email.length() - 1;
    }
}
