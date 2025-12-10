package model;

public class User {
    private String tp;
    private String role;
    private String name;
    private String email;
    private String password;
    private String profilePicture;  // URL or emoji for profile picture
    private String description;     // Student bio/description
    private String phoneNumber;     // Contact number
    private String address;         // Address

    public User(String tp, String role, String name, String email, String password) {
        this.tp = tp;
        this.role = role;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profilePicture = "👤";  // Default profile icon
        this.description = "";
        this.phoneNumber = "";
        this.address = "";
    }
    
    public User(String tp, String role, String name, String email, String password, 
                String profilePicture, String description, String phoneNumber, String address) {
        this.tp = tp;
        this.role = role;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profilePicture = (profilePicture != null && !profilePicture.isEmpty()) ? profilePicture : "👤";
        this.description = (description != null) ? description : "";
        this.phoneNumber = (phoneNumber != null) ? phoneNumber : "";
        this.address = (address != null) ? address : "";
    }

    // Getters
    public String getTp() {
        return tp;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setTp(String tp) {
        this.tp = tp;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Get a display-friendly version of the user ID
     * Shows role-specific format (e.g., LEC-001, STF-002, TP001)
     */
    public String getDisplayId() {
        return tp;
    }
    
    /**
     * Get the user's role-specific ID prefix
     */
    public String getIdPrefix() {
        switch (role.toUpperCase()) {
            case "STUDENT":
                return "TP";
            case "LECTURER":
                return "LEC";
            case "STAFF":
                return "STF";
            case "ADMIN":
                return "ADM";
            default:
                return "USR";
        }
    }

    @Override
    public String toString() {
        // Format: tp|role|name|email|password|profilePicture|description|phoneNumber|address
        return tp + "|" + role + "|" + name + "|" + email + "|" + password + "|" + 
               profilePicture + "|" + description + "|" + phoneNumber + "|" + address;
    }
}
