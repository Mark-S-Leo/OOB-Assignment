
## 📋 Prerequisites

- **Java 25** (JDK 25)
- **Apache NetBeans IDE 28** (supports Java 25) - *if running from IDE*
- Maven will be auto-installed by the run scripts

## 🚀 How to Run

### Option 1: Using Run Scripts (Recommended)

**Windows:**
```batch
.\run.bat
```

**macOS/Linux:**
```bash
bash run.sh
```

The scripts will automatically:
- Check and install Maven if missing
- Download dependencies
- Compile the project
- Run the application

### Option 2: Using Apache NetBeans IDE

1. Open Apache NetBeans IDE 28 (Java 25 support required)
2. Open the project folder
3. Click **Run Project** or press `F6`

### Option 3: Manual Maven Commands

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="Main"
```

##  Default Users
 Role     | Email              | Password | Name        |
----------|--------------------|----------|-------------|
 Student  | student@email.com  | password123   | Mark Leo    |
 Lecturer | lecturer@email.com | password123   | Dr Lim      |
 Staff    | staff@email.com    | password123   | Sarah Wong  |
 Admin    | admin@email.com    | password123   | Admin User  |




