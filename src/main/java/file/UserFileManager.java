package file;

import model.*;
import util.PasswordUtil; // CHANGE: Added import for password hashing utility
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * CHANGE: Updated file format to support email-based authentication
 * File format: tp|role|name|email|hashedPassword
 */
public class UserFileManager {
    private static final String FILE_PATH = "../resources/users.txt"; // CHANGE: Fixed path to work from java directory

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

    // CHANGE: Added email-based login validation with password hashing
    /**
     * Validates user login using email and password
     * @param email User's email address
     * @param password Plain text password (will be hashed for comparison)
     * @return User object if credentials are valid, null otherwise
     */
    public static User validateLoginByEmail(String email, String password) {
        ArrayList<User> users = loadAll();
        for (User user : users) {
            // Check if email matches and password hash matches
            if (user.getEmail().equalsIgnoreCase(email)) {
                // CHANGE: Use password hashing for secure comparison
                if (PasswordUtil.verifyPassword(password, user.getPassword())) {
                    return user;
                }
            }
        }
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
}
