# Login System Changes Documentation

## Overview
This document explains all changes made to convert the consultation management system from TP-based login to email-based login with secure password hashing.

---

## Summary of Changes

### 1. **New Utility Class Created**
**File:** `src/main/java/util/PasswordUtil.java`

**Purpose:** Provides password hashing and email validation utilities

**Key Methods:**
- `hashPassword(String password)` - Hashes passwords using SHA-256
- `verifyPassword(String plainPassword, String hashedPassword)` - Validates passwords
- `isValidEmail(String email)` - Validates email format (must contain '@')

---

### 2. **LoginUI.java Updates**

**Changes Made:**

#### Field Changes:
- **CHANGED:** `tpField` → `emailField`
  - Now accepts email address instead of TP number

#### Label Changes:
- **CHANGED:** "TP Number:" → "Email:"

#### Validation Enhancements:
- **ADDED:** Email format validation (must contain '@')
- **ADDED:** Error message for invalid email format
- **CHANGED:** Error messages now reference "email" instead of "TP"

#### Authentication Method:
- **CHANGED:** `UserFileManager.validateLogin(tp, password)` → `UserFileManager.validateLoginByEmail(email, password)`
- **ADDED:** Password hashing during login verification

#### Role-Based Redirection (Maintained):
- Student → StudentDashboard (Student Module)
- Lecturer → LecturerDashboard (Lecturer Module)
- Staff → StaffDashboard (Appointment Module)
- Admin → AdminDashboard (Admin Module)

---

### 3. **UserFileManager.java Updates**

**Changes Made:**

#### New Import:
- **ADDED:** `import util.PasswordUtil;` for password hashing

#### New Methods:

**validateLoginByEmail(String email, String password)**
- Validates user credentials using email and hashed password
- Searches users by email (case-insensitive)
- Uses SHA-256 hash comparison for password verification
- Returns User object if valid, null otherwise

**findByEmail(String email)**
- Finds user by email address
- Case-insensitive email matching
- Returns User object if found, null otherwise

#### Existing Methods (Maintained):
- All original methods remain functional
- `validateLogin(String tp, String password)` kept for backward compatibility if needed

---

### 4. **users.txt File Format**

**Format Specification:**
```
tp|role|name|email|hashedPassword
```

**Example Updated File Structure:**

```
TP001|STUDENT|Mark Leo|mark@email.com|5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5
TP002|LECTURER|Dr Lim|lim@email.com|6ca13d52ca70c883e0f0bb101e425a89e8624de51db2d2392593af6a84118090
TP003|STAFF|Sarah Wong|sarah@email.com|10176e7b7b24d317acfcf8d2064cfd2f24e154f7b5a96603077d5ef813d6a6b6
TP004|ADMIN|Admin User|admin@email.com|240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
TP005|STUDENT|Jane Doe|jane@email.com|9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c
TP006|LECTURER|Prof Smith|smith@email.com|20461ef242c74790b27d763452fc346358fc29299576928aa89e8e0e2cd0e72a
```

**Test Credentials:**

| Role     | Email              | Password     | Hashed Password (SHA-256)                                          |
|----------|--------------------|--------------|--------------------------------------------------------------------|
| Student  | mark@email.com     | 12345        | 5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5   |
| Lecturer | lim@email.com      | abc123       | 6ca13d52ca70c883e0f0bb101e425a89e8624de51db2d2392593af6a84118090   |
| Staff    | sarah@email.com    | staff123     | 10176e7b7b24d317acfcf8d2064cfd2f24e154f7b5a96603077d5ef813d6a6b6   |
| Admin    | admin@email.com    | admin123     | 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9   |
| Student  | jane@email.com     | pass123      | 9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c   |
| Lecturer | smith@email.com    | lecture123   | 20461ef242c74790b27d763452fc346358fc29299576928aa89e8e0e2cd0e72a   |

---

## Security Improvements

### 1. **Password Hashing (SHA-256)**
- All passwords are now hashed using SHA-256 algorithm
- Plain text passwords are never stored in the database
- Hashing is one-way - cannot be reversed to get original password

### 2. **Email Validation**
- System validates email format before authentication
- Must contain '@' symbol
- Helps prevent invalid login attempts

### 3. **Case-Insensitive Email Matching**
- Users can log in with any case variation of their email
- Example: Mark@Email.com, mark@email.com, MARK@EMAIL.COM all work

---

## Backward Compatibility

### Maintained Features:
1. ✅ Single login page for all user types
2. ✅ Role-based authentication and redirection
3. ✅ Text file-based credential storage
4. ✅ All existing dashboard functionality
5. ✅ User management features (create, update, delete)

### Original Method Retention:
- `validateLogin(tp, password)` method retained in UserFileManager
- Can be used if TP-based login is needed in future

---

## How to Add New Users

### Method 1: Using PasswordUtil to hash passwords

```java
import util.PasswordUtil;

public class HashPasswordExample {
    public static void main(String[] args) {
        String plainPassword = "newpassword123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        System.out.println("Hashed: " + hashedPassword);
    }
}
```

### Method 2: Manual Entry
1. Hash the password using SHA-256 tool or the PasswordUtil class
2. Add entry to users.txt in format: `TP|ROLE|NAME|EMAIL|HASHED_PASSWORD`

---

## Testing the Changes

### Test Case 1: Valid Email Login
- Email: mark@email.com
- Password: 12345
- Expected: Login successful, redirect to Student Dashboard

### Test Case 2: Invalid Email Format
- Email: markatemail (no @)
- Password: 12345
- Expected: Error message "Invalid email format. Email must contain '@'."

### Test Case 3: Invalid Password
- Email: mark@email.com
- Password: wrongpassword
- Expected: Error message "Invalid email or password."

### Test Case 4: Empty Fields
- Email: (empty)
- Password: (empty)
- Expected: Error message "Please enter both email and password."

### Test Case 5: Role-Based Redirection
- Test each role (Student, Lecturer, Staff, Admin)
- Expected: Each role redirects to appropriate module

---

## Files Modified

1. **Created:**
   - `src/main/java/util/PasswordUtil.java` - Password hashing utility

2. **Modified:**
   - `src/main/java/ui/LoginUI.java` - Updated UI and validation logic
   - `src/main/java/file/UserFileManager.java` - Added email-based authentication
   - `src/main/resources/users.txt` - Updated with hashed passwords

3. **Unchanged:**
   - `src/main/java/model/User.java` - No changes needed
   - All dashboard files - Functionality maintained
   - All service classes - No impact from login changes

---

## Migration Notes

If migrating from old system:
1. Users must use their email address instead of TP number to log in
2. Old passwords need to be hashed and updated in users.txt
3. Use the provided hash values for test accounts
4. Email field is now the primary login identifier (TP still stored for internal use)

---

## Security Best Practices Implemented

✅ Password hashing (SHA-256)  
✅ No plain text password storage  
✅ Input validation (email format)  
✅ Case-insensitive email matching for better UX  
✅ Clear error messages without revealing security details  
✅ Secure password comparison using hash verification  

---

## Future Enhancements (Optional)

- Add salt to password hashing for additional security
- Implement password strength requirements
- Add "Forgot Password" functionality
- Enable two-factor authentication
- Add login attempt limiting to prevent brute force attacks
- Implement session management with timeout

---

**Last Updated:** December 6, 2025  
**Version:** 2.0  
**Author:** System Development Team
