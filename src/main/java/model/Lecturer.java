package model;

public class Lecturer extends User {
    
    public Lecturer(String tp, String name, String email, String password) {
        super(tp, "LECTURER", name, email, password);
    }
}
