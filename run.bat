@echo off
cd /d "%~dp0"

echo =====================================
echo Student Consultation Management System
echo =====================================
echo.

REM Define Maven paths to check
set MAVEN_PATH_1=C:\tools\apache-maven-3.9.6\bin\mvn.cmd
set MAVEN_PATH_2=C:\Program Files\Apache\maven\bin\mvn.cmd
set MAVEN_CMD=

REM Check if Maven is in system PATH
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    set MAVEN_CMD=mvn
    goto run_app
)

REM Check predefined locations
if exist "%MAVEN_PATH_1%" (
    set MAVEN_CMD=%MAVEN_PATH_1%
    goto run_app
)

if exist "%MAVEN_PATH_2%" (
    set MAVEN_CMD=%MAVEN_PATH_2%
    goto run_app
)

REM Maven not found - auto-install
echo Maven not found. Installing Maven...
if not exist C:\tools mkdir C:\tools

echo Downloading Maven 3.9.6...
powershell -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile '$env:TEMP\maven.zip'; Expand-Archive -Path '$env:TEMP\maven.zip' -DestinationPath 'C:\tools' -Force; Remove-Item '$env:TEMP\maven.zip' -Force"

if %ERRORLEVEL% NEQ 0 (
    echo Failed to install Maven
    pause
    exit /b 1
)

set MAVEN_CMD=%MAVEN_PATH_1%
echo Maven installed successfully.
echo.

:run_app
echo Running application...
echo.

REM Clean and compile
call %MAVEN_CMD% clean compile -q
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed
    pause
    exit /b 1
)

REM Build classpath file
call %MAVEN_CMD% dependency:build-classpath "-Dmdep.outputFile=target/classpath.txt" -q
if %ERRORLEVEL% NEQ 0 (
    echo Failed to build classpath
    pause
    exit /b 1
)

REM Read classpath from file
set /p CP=<target\classpath.txt

REM Run application with full classpath
java -cp "target\classes;%CP%" Main

pause
