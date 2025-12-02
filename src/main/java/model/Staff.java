package model;

public class Staff extends User {
    
    public Staff(String tp, String name, String email, String password) {
        super(tp, "STAFF", name, email, password);
    }
}
