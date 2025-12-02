# Student Consultation Management System (SCMS)

## Project Structure

This is a Java-based Student Consultation Management System with GUI interface using Swing.

### Package Structure
```
src/main/java/
├── model/          - Data model classes
├── file/           - File management classes
├── service/        - Business logic services
├── ui/             - Swing GUI components
└── Main.java       - Application entry point

src/main/resources/
├── users.txt       - User data
├── slots.txt       - Consultation slots
├── requests.txt    - Consultation requests
└── appointments.txt - Approved appointments
```

### Features by Role

**Student**
- View available consultation slots
- Create consultation requests
- View own requests
- Cancel requests (PENDING/APPROVED only)

**Lecturer**
- Create consultation slots
- View own slots
- View approved bookings
- Cancel slots (OPEN only)

**Staff**
- View pending requests
- Approve/reject requests
- View all appointments
- Update appointments

**Admin**
- Create users
- View all users
- Search users
- Update users
- Delete users

### How to Run

1. Compile all Java files
2. Run `Main.java`
3. Login with test credentials:
   - Student: TP001 / 12345
   - Lecturer: TP002 / abc123
   - Staff: TP003 / staff123
   - Admin: TP004 / admin123

### Technologies Used
- Java SE
- Swing (GUI)
- File I/O (text files)
- OOP principles (inheritance, encapsulation)
