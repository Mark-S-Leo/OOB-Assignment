package model;

public class Admin extends User {
    
    public Admin(String tp, String name, String email, String password) {
        super(tp, "ADMIN", name, email, password);
    }
}
