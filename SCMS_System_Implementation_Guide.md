# Student Consultation Management System  
## **System-Building Requirements (.md for Implementation)**  
### *(Beginner-friendly, step-by-step, practical guide for building the Java system)*

This file contains the **actual system requirements, structures, file layouts, class lists, GUI screens, and implementation steps** needed to **build** the system.  
NOT documentation.  
NOT report.  
This is the **developer guide** used while coding.

---

# 1. Project Structure (Simple & Required)

Create folders like this:

```
src/
  model/        -> classes storing data (User, Slot, Request…)
  file/         -> reading/writing text files
  service/      -> logic for each role
  ui/           -> Swing GUI screens
```

---

# 2. Text Files (Core of the System)
These files MUST exist for the system to run.

## 2.1 users.txt
Stores ALL users.

Format:
```
tp|role|name|email|password
```

Example:
```
TP001|STUDENT|Mark Leo|mark@email.com|12345
TP002|LECTURER|Dr Lim|lim@email.com|abc123
```

---

## 2.2 slots.txt
Stores lecturer-created consultation slots.

```
slotId|lecturerTp|date|startTime|endTime|status
```

Example:
```
S1|TP002|2025-12-10|14:00|14:30|OPEN
```

---

## 2.3 requests.txt
Stores student consultation requests.

```
requestId|studentTp|lecturerTp|slotId|reason|status
```

Example:
```
R1|TP001|TP002|S1|Need help with assignment|PENDING
```

---

## 2.4 appointments.txt
Stores approved appointments.

```
appointmentId|requestId|studentTp|lecturerTp|slotId|date|startTime|status
```

Example:
```
A1|R1|TP001|TP002|S1|2025-12-10|14:00|SCHEDULED
```

---

# 3. Java Classes You Must Create

## 3.1 Model Classes (Simple Data Holders)
These store information loaded from files.

- `User`
- `Student extends User`
- `Lecturer extends User`
- `Staff extends User`
- `Admin extends User`
- `Slot`
- `Request`
- `Appointment`

Each class:  
- private variables  
- constructor  
- getters/setters  
- toString()  

---

## 3.2 File Managers (Reading/Writing Text Files)

Create these classes in *file/*:

- `UserFileManager`
- `SlotFileManager`
- `RequestFileManager`
- `AppointmentFileManager`

Each file manager MUST have:
```
loadAll()      -> read all lines into ArrayList<Object>
saveAll(list)  -> overwrite file
appendOne()    -> add a new row
findById()
update()
```

All file I/O uses:
```
Scanner
BufferedReader
PrintWriter
FileWriter
```

---

# 4. Service Classes (Logic of the System)

These classes handle the actions of each role.

## 4.1 StudentService
- viewAvailableSlots()
- createConsultationRequest()
- viewOwnRequests()
- cancelRequest()

Rules:
- can only cancel PENDING or APPROVED

---

## 4.2 LecturerService
- createSlot()
- viewOwnSlots()
- viewApprovedBookings()
- cancelSlot()

Rules:
- cannot create slot in the past

---

## 4.3 StaffService
- viewPendingRequests()
- approveRequest() → creates appointment
- cancelRequest()
- updateAppointment()

Rules:
- new date/time must match lecturer slot

---

## 4.4 AdminService
- createUser()
- viewAllUsers()
- searchUser()
- updateUser()
- deleteUser()

Rules:
- only admin can change TP

---

# 5. GUI Screens Needed (Simple Swing)

## 5.1 Login
Fields:
- TP
- Password  
Buttons:
- Login

When login succeeds → open dashboard based on role.

---

## 5.2 Student Dashboard
Buttons:
- View Available Slots  
- Create Request  
- My Requests  
- Update Profile  

Each opens JFrame/JDialog.

---

## 5.3 Lecturer Dashboard
Buttons:
- Create Slot  
- View My Slots  
- View Bookings  
- Update Profile  

---

## 5.4 Staff Dashboard
Buttons:
- Pending Requests  
- View Appointments  
- Update Appointment  
- Update Profile  

---

## 5.5 Admin Dashboard
Buttons:
- Create User  
- View Users  
- Search User  
- Update User  
- Delete User  
- Update Profile  

---

# 6. Step-by-Step Building Guide (Very Simple)

## STEP 1 — Create basic project & packages  
Create folders:
```
model/
file/
service/
ui/
```

## STEP 2 — Create model classes  
Start with:
- User
- Student
- Lecturer

Then Slot, Request, Appointment.

## STEP 3 — Create File Managers  
Implement:
- read file line by line
- split using .split("\|")
- store into ArrayList

## STEP 4 — Build Login Screen  
- read users.txt
- check tp + password
- open correct dashboard

## STEP 5 — Build Student features  
- load available slots from file  
- create request (append to requests.txt)  
- show student’s requests  
- cancel request if allowed  

## STEP 6 — Build Lecturer features  
- create slot (append to slots.txt)  
- view slots and bookings  

## STEP 7 — Build Staff features  
- load pending requests  
- approve → create appointment  
- cancel/request  

## STEP 8 — Build Admin features  
- simple CRUD on users.txt  

## STEP 9 — Test everything manually  
Check each role.

---

# 7. Minimal Things That MUST Exist To Pass (and get Distinction)

✔ Model classes  
✔ File managers  
✔ GUI for all roles  
✔ Each feature works  
✔ Text file CRUD is correct  
✔ OOP (inheritance + encapsulation)  
✔ Clear steps and code structure  

---

# 8. Ready-To-Code Class List Summary

```
model/
  User.java
  Student.java
  Lecturer.java
  Staff.java
  Admin.java
  Slot.java
  Request.java
  Appointment.java

file/
  UserFileManager.java
  SlotFileManager.java
  RequestFileManager.java
  AppointmentFileManager.java

service/
  StudentService.java
  LecturerService.java
  StaffService.java
  AdminService.java

ui/
  LoginUI.java
  StudentDashboard.java
  LecturerDashboard.java
  StaffDashboard.java
  AdminDashboard.java
```

---

# END OF SYSTEM IMPLEMENTATION FILE
