package file;

import model.*;
import util.UserIdGenerator;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * CHANGE: Updated file format to support email-based authentication and profile fields
 * File format: tp|role|name|email|hashedPassword|profilePicture|description|phoneNumber|address
 */
public class UserFileManager {
    private static final String FILE_PATH = "users.txt";

    // Load all users from file
    public static ArrayList<User> loadAll() {
        ArrayList<User> users = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            return users;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String tp = parts[0];
                    String role = parts[1];
                    String name = parts[2];
                    String email = parts[3];
                    String password = parts[4];
                    
                    // Handle optional profile fields (backward compatible)
                    String profilePicture = (parts.length > 5 && !parts[5].isEmpty()) ? parts[5] : "👤";
                    String description = (parts.length > 6) ? parts[6] : "";
                    String phoneNumber = (parts.length > 7) ? parts[7] : "";
                    String address = (parts.length > 8) ? parts[8] : "";
                    
                    User user;
                    switch (role.toUpperCase()) {
                        case "STUDENT":
                            user = new Student(tp, name, email, password);
                            break;
                        case "LECTURER":
                            user = new Lecturer(tp, name, email, password);
                            break;
                        case "STAFF":
                            user = new Staff(tp, name, email, password);
                            break;
                        case "ADMIN":
                            user = new Admin(tp, name, email, password);
                            break;
                        default:
                            user = new User(tp, role, name, email, password);
                    }
                    // Set profile fields
                    user.setProfilePicture(profilePicture);
                    user.setDescription(description);
                    user.setPhoneNumber(phoneNumber);
                    user.setAddress(address);
                    
                    users.add(user);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("User file not found: " + e.getMessage());
        }
        
        return users;
    }

    // Save all users to file
    public static void saveAll(ArrayList<User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                writer.println(user.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    // Append one user to file
    public static void appendOne(User user) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             PrintWriter writer = new PrintWriter(fw)) {
            writer.println(user.toString());
        } catch (IOException e) {
            System.err.println("Error appending user: " + e.getMessage());
        }
    }

    // Find user by TP
    public static User findById(String tp) {
        ArrayList<User> users = loadAll();
        for (User user : users) {
            if (user.getTp().equals(tp)) {
                return user;
            }
        }
        return null;
    }

    // Update user
    public static boolean update(User updatedUser) {
        ArrayList<User> users = loadAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getTp().equals(updatedUser.getTp())) {
                users.set(i, updatedUser);
                saveAll(users);
                return true;
            }
        }
        return false;
    }

    // Delete user by TP
    public static boolean delete(String tp) {
        ArrayList<User> users = loadAll();
        boolean removed = users.removeIf(user -> user.getTp().equals(tp));
        if (removed) {
            saveAll(users);
        }
        return removed;
    }

    // Validate login
    public static User validateLogin(String tp, String password) {
        User user = findById(tp);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    // CHANGE: Updated email-based login validation to use plain text passwords
    /**
     * Validates user login using email and password
     * @param email User's email address
     * @param password Plain text password
     * @return User object if credentials are valid, null otherwise
     */
    public static User validateLoginByEmail(String email, String password) {
        ArrayList<User> users = loadAll();
        System.out.println("DEBUG: Loaded " + users.size() + " users");
        System.out.println("DEBUG: Looking for email: '" + email + "' with password: '" + password + "'");
        
        for (User user : users) {
            System.out.println("DEBUG: Checking user - Email: '" + user.getEmail() + "', Password: '" + user.getPassword() + "'");
            // Check if email matches and password matches (plain text)
            if (user.getEmail().equalsIgnoreCase(email)) {
                System.out.println("DEBUG: Email matched!");
                if (user.getPassword().equals(password)) {
                    System.out.println("DEBUG: Password matched!");
                    return user;
                } else {
                    System.out.println("DEBUG: Password did not match. Expected: '" + user.getPassword() + "', Got: '" + password + "'");
                }
            }
        }
        System.out.println("DEBUG: No match found");
        return null;
    }

    // CHANGE: Added method to find user by email
    /**
     * Find user by email address
     * @param email User's email
     * @return User object if found, null otherwise
     */
    public static User findByEmail(String email) {
        ArrayList<User> users = loadAll();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Generate a unique user ID for a new user based on their role
     * @param role User role (STUDENT, LECTURER, STAFF, ADMIN)
     * @param name User's name
     * @return Generated unique user ID
     */
    public static String generateNewUserId(String role, String name) {
        ArrayList<User> existingUsers = loadAll();
        return UserIdGenerator.generateUserId(role, name, existingUsers);
    }
    
    /**
     * Create a new user with auto-generated ID
     * @param role User role
     * @param name User's name
     * @param email User's email
     * @param plainPassword Plain text password (stored as-is)
     * @return Created User object
     */
    public static User createUserWithAutoId(String role, String name, String email, String plainPassword) {
        String userId = generateNewUserId(role, name);
        // Store password as plain text (not recommended for production)
        
        User user;
        switch (role.toUpperCase()) {
            case "STUDENT":
                user = new Student(userId, name, email, plainPassword);
                break;
            case "LECTURER":
                user = new Lecturer(userId, name, email, plainPassword);
                break;
            case "STAFF":
                user = new Staff(userId, name, email, plainPassword);
                break;
            case "ADMIN":
                user = new Admin(userId, name, email, plainPassword);
                break;
            default:
                user = new User(userId, role, name, email, plainPassword);
        }
        
        appendOne(user);
        return user;
    }
    
    /**
     * Migrate existing users to new ID format
     * This should be run once to convert old TP-based IDs to role-specific IDs
     * Students keep TP numbers, others get new format
     */
    public static void migrateToNewIdFormat() {
        ArrayList<User> users = loadAll();
        ArrayList<User> migratedUsers = new ArrayList<>();
        boolean needsMigration = false;
        
        for (User user : users) {
            String oldId = user.getTp();
            String role = user.getRole();
            
            // Check if migration is needed (non-students with TP format)
            if (!role.equalsIgnoreCase("STUDENT") && oldId.startsWith("TP")) {
                needsMigration = true;
                String newId = UserIdGenerator.migrateToNewFormat(oldId, role, user.getName(), migratedUsers);
                user.setTp(newId);
            }
            
            migratedUsers.add(user);
        }
        
        if (needsMigration) {
            // Backup old file
            backupUsersFile();
            // Save with new IDs
            saveAll(migratedUsers);
            System.out.println("User IDs migrated successfully!");
        } else {
            System.out.println("No migration needed - IDs are already in correct format.");
        }
    }
    
    /**
     * Create a backup of the users file
     */
    private static void backupUsersFile() {
        try {
            File source = new File(FILE_PATH);
            File backup = new File(FILE_PATH + ".backup");
            
            try (BufferedReader reader = new BufferedReader(new FileReader(source));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(backup))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            System.out.println("Backup created: " + backup.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
        }
    }
}

