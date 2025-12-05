# Quick Reference Guide - Email Login System

## 📧 Test Login Credentials

Use these credentials to test the new email-based login system:

### Student Account
- **Email:** student@email.com  
  **Password:** student1

### Lecturer Account
- **Email:** lecturer@email.com  
  **Password:** lecturer1

### Staff Account
- **Email:** staff@email.com  
  **Password:** staff1

### Admin Account
- **Email:** admin@email.com  
  **Password:** admin1

---

## 🔐 What Changed?

| Before | After |
|--------|-------|
| Login with TP Number | Login with Email |
| Plain text passwords | SHA-256 hashed passwords |
| No email validation | Email must contain '@' |
| TP-based authentication | Email-based authentication |

---

## ✅ How to Use

1. **Run the Application:**
   ```bash
   cd src/main/java
   javac Main.java model/*.java file/*.java service/*.java ui/*.java util/*.java
   java Main
   ```

2. **Login:**
   - Enter your **email address** (e.g., mark@email.com)
   - Enter your **password** (e.g., 12345)
   - Click **Login**

3. **Role-Based Access:**
   - **Students** → Student Dashboard (view slots, create requests)
   - **Lecturers** → Lecturer Dashboard (create slots, view bookings)
   - **Staff** → Staff Dashboard (manage appointments, approve requests)
   - **Admins** → Admin Dashboard (manage users)

---

## 🛠️ For Developers

### Generate Password Hash
```java
import util.PasswordUtil;

String password = "mypassword";
String hash = PasswordUtil.hashPassword(password);
System.out.println(hash);
```

### Validate Email
```java
import util.PasswordUtil;

boolean valid = PasswordUtil.isValidEmail("user@example.com"); // true
boolean invalid = PasswordUtil.isValidEmail("invalid.email"); // false
```

### Add New User to users.txt
Format: `TP|ROLE|NAME|EMAIL|HASHED_PASSWORD`

Example:
```
TP007|STUDENT|John Smith|john@email.com|[SHA-256 hash of password]
```

---

## 📝 Files Modified

✅ `LoginUI.java` - Updated UI for email login  
✅ `UserFileManager.java` - Added email-based authentication  
✅ `PasswordUtil.java` - NEW: Password hashing utility  
✅ `users.txt` - Updated with hashed passwords  

---

## ❗ Common Issues

**Issue:** "Invalid email format"  
**Solution:** Make sure email contains '@' symbol

**Issue:** "Invalid email or password"  
**Solution:** Check email spelling and password, both are case-sensitive for password

**Issue:** Cannot find user  
**Solution:** Verify user exists in users.txt with correct email

---

## 📚 Documentation

For detailed technical documentation, see:
- `LOGIN_SYSTEM_CHANGES.md` - Complete change log
- `PasswordHashGenerator.java` - Test password hashing

---

**System Version:** 2.0 (Email Authentication)  
**Last Updated:** December 6, 2025
