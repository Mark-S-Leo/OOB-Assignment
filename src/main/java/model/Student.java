package model;

public class Student extends User {
    
    public Student(String tp, String name, String email, String password) {
        super(tp, "STUDENT", name, email, password);
    }
}
