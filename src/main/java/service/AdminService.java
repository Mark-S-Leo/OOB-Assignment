package service;

import model.*;
import file.*;
import java.util.ArrayList;

public class AdminService {

    // Create a new user
    public boolean createUser(String tp, String role, String name, String email, String password) {
        // Check if user already exists
        User existingUser = UserFileManager.findById(tp);
        if (existingUser != null) {
            System.out.println("User with TP " + tp + " already exists.");
            return false;
        }

        // Create user based on role
        User newUser;
        switch (role.toUpperCase()) {
            case "STUDENT":
                newUser = new Student(tp, name, email, password);
                break;
            case "LECTURER":
                newUser = new Lecturer(tp, name, email, password);
                break;
            case "STAFF":
                newUser = new Staff(tp, name, email, password);
                break;
            case "ADMIN":
                newUser = new Admin(tp, name, email, password);
                break;
            default:
                System.out.println("Invalid role.");
                return false;
        }

        UserFileManager.appendOne(newUser);
        System.out.println("User created successfully: " + tp);
        return true;
    }

    // View all users
    public ArrayList<User> viewAllUsers() {
        return UserFileManager.loadAll();
    }

    // Search user by TP
    public User searchUser(String tp) {
        return UserFileManager.findById(tp);
    }

    // Update user
    public boolean updateUser(String tp, String newName, String newEmail, String newPassword) {
        User user = UserFileManager.findById(tp);
        
        if (user == null) {
            System.out.println("User not found.");
            return false;
        }

        if (newName != null && !newName.isEmpty()) {
            user.setName(newName);
        }
        if (newEmail != null && !newEmail.isEmpty()) {
            user.setEmail(newEmail);
        }
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }

        UserFileManager.update(user);
        System.out.println("User updated successfully.");
        return true;
    }

    // Update user TP (only admin can do this)
    public boolean updateUserTp(String oldTp, String newTp) {
        User user = UserFileManager.findById(oldTp);
        
        if (user == null) {
            System.out.println("User not found.");
            return false;
        }

        // Check if new TP already exists
        User existingUser = UserFileManager.findById(newTp);
        if (existingUser != null) {
            System.out.println("New TP already exists.");
            return false;
        }

        user.setTp(newTp);
        UserFileManager.delete(oldTp);
        UserFileManager.appendOne(user);
        
        System.out.println("User TP updated successfully.");
        return true;
    }

    // Delete user
    public boolean deleteUser(String tp) {
        boolean deleted = UserFileManager.delete(tp);
        
        if (deleted) {
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("User not found.");
        }
        
        return deleted;
    }
}
