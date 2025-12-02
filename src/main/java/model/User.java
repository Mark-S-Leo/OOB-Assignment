package model;

public class User {
    private String tp;
    private String role;
    private String name;
    private String email;
    private String password;

    public User(String tp, String role, String name, String email, String password) {
        this.tp = tp;
        this.role = role;
        this.name = name;
        this.email = email;
        this.password = password;
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

    @Override
    public String toString() {
        return tp + "|" + role + "|" + name + "|" + email + "|" + password;
    }
}
