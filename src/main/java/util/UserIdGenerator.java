package util;

import model.User;
import java.util.ArrayList;

/**
 * Utility class for generating unique user IDs based on role
 * 
 * ID Formats:
 * - Student: TP001, TP002, TP003... (existing format)
 * - Lecturer: LEC-001, LEC-002, LEC-003...
 * - Staff: STF-001, STF-002, STF-003...
 * - Admin: ADM-001, ADM-002, ADM-003...
 */
public class UserIdGenerator {
    
    /**
     * Generate a unique user ID based on role and existing users
     * @param role User role (STUDENT, LECTURER, STAFF, ADMIN)
     * @param name User's name (used for alternative format if needed)
     * @param existingUsers List of existing users to check for uniqueness
     * @return Generated unique user ID
     */
    public static String generateUserId(String role, String name, ArrayList<User> existingUsers) {
        String prefix = getPrefixForRole(role);
        int nextNumber = getNextNumberForRole(role, existingUsers);
        
        return prefix + String.format("%03d", nextNumber);
    }
    
    /**
     * Generate ID with name-based code (alternative format)
     * Format: PREFIX-XXX where XXX is first 3 letters of name
     * If duplicate, appends number: PREFIX-XXX2, PREFIX-XXX3, etc.
     */
    public static String generateUserIdWithName(String role, String name, ArrayList<User> existingUsers) {
        String prefix = getPrefixForRole(role);
        String nameCode = extractNameCode(name);
        String baseId = prefix + nameCode;
        
        // Check for uniqueness
        String candidateId = baseId;
        int counter = 1;
        
        while (idExists(candidateId, existingUsers)) {
            counter++;
            candidateId = baseId + counter;
        }
        
        return candidateId;
    }
    
    /**
     * Get the prefix for a given role
     */
    private static String getPrefixForRole(String role) {
        switch (role.toUpperCase()) {
            case "STUDENT":
                return "TP";
            case "LECTURER":
                return "LEC-";
            case "STAFF":
                return "STF-";
            case "ADMIN":
                return "ADM-";
            default:
                return "USR-";
        }
    }
    
    /**
     * Get the next available number for a given role
     */
    private static int getNextNumberForRole(String role, ArrayList<User> existingUsers) {
        String prefix = getPrefixForRole(role);
        int maxNumber = 0;
        
        for (User user : existingUsers) {
            if (user.getRole().equalsIgnoreCase(role)) {
                String userId = user.getTp();
                try {
                    // Extract number from ID
                    String numberPart = userId.replace(prefix, "").replaceAll("[^0-9]", "");
                    if (!numberPart.isEmpty()) {
                        int number = Integer.parseInt(numberPart);
                        if (number > maxNumber) {
                            maxNumber = number;
                        }
                    }
                } catch (NumberFormatException e) {
                    // Skip if can't parse number
                }
            }
        }
        
        return maxNumber + 1;
    }
    
    /**
     * Extract a code from the name (first 3 letters, uppercase)
     */
    private static String extractNameCode(String name) {
        if (name == null || name.isEmpty()) {
            return "USR";
        }
        
        // Remove spaces and special characters, take first word
        String cleanName = name.trim().split("\\s+")[0].replaceAll("[^a-zA-Z]", "");
        
        if (cleanName.length() >= 3) {
            return cleanName.substring(0, 3).toUpperCase();
        } else if (cleanName.length() > 0) {
            // Pad with X if name is too short
            return (cleanName + "XXX").substring(0, 3).toUpperCase();
        } else {
            return "USR";
        }
    }
    
    /**
     * Check if an ID already exists in the user list
     */
    private static boolean idExists(String id, ArrayList<User> existingUsers) {
        for (User user : existingUsers) {
            if (user.getTp().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Validate if an ID matches the expected format for a role
     */
    public static boolean isValidIdForRole(String id, String role) {
        String prefix = getPrefixForRole(role);
        return id != null && id.toUpperCase().startsWith(prefix);
    }
    
    /**
     * Get a display-friendly version of the ID
     * For backwards compatibility with existing code that expects "TP" format
     */
    public static String getDisplayId(User user) {
        return user.getTp();
    }
    
    /**
     * Migrate old TP number to new format for non-students
     * This is used during data migration
     */
    public static String migrateToNewFormat(String oldId, String role, String name, ArrayList<User> existingUsers) {
        // Students keep their TP numbers
        if ("STUDENT".equalsIgnoreCase(role)) {
            // Ensure it's in TP### format
            if (!oldId.startsWith("TP")) {
                return "TP" + String.format("%03d", getNextNumberForRole(role, existingUsers));
            }
            return oldId;
        }
        
        // Other roles get new format
        return generateUserId(role, name, existingUsers);
    }
}
