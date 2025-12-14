@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

echo =====================================
echo Student Consultation Management System
echo =====================================

REM -------------------------
REM Check if Maven exists
REM -------------------------
where mvn >nul 2>nul
IF %ERRORLEVEL% NEQ 0 (
    echo Maven is not installed. Attempting auto-install...
    echo.

    REM Try Chocolatey first (most common on Windows)
    where choco >nul 2>nul
    IF %ERRORLEVEL% EQU 0 (
        echo Installing Maven using Chocolatey...
        choco install maven -y
        IF %ERRORLEVEL% EQU 0 (
            echo Maven installed successfully via Chocolatey.
            goto :verify_maven
        )
    )

    REM Try Winget as fallback (Windows 10/11)
    where winget >nul 2>nul
    IF %ERRORLEVEL% EQU 0 (
        echo Installing Maven using Winget...
        winget install --id Apache.Maven -e --source winget --accept-package-agreements --accept-source-agreements
        IF %ERRORLEVEL% EQU 0 (
            echo Maven installed successfully via Winget.
            goto :verify_maven
        )
    )

    REM If both failed
    echo.
    echo ==========================================
    echo  Maven Installation Failed
    echo ==========================================
    echo Please install Maven manually:
    echo.
    echo Option 1 - Chocolatey (Recommended):
    echo   1. Install Chocolatey from https://chocolatey.org/install
    echo   2. Run: choco install maven
    echo.
    echo Option 2 - Manual:
    echo   1. Download from https://maven.apache.org/download.cgi
    echo   2. Extract and add bin folder to PATH
    echo.
    echo Option 3 - Winget (Windows 10/11):
    echo   1. Update Windows to latest version
    echo   2. Run: winget install Apache.Maven
    echo.
    pause
    exit /b 1
)

:verify_maven
REM Refresh environment for newly installed Maven
call refreshenv >nul 2>nul

REM -------------------------
REM Verify Maven is accessible
REM -------------------------
where mvn >nul 2>nul
IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo Maven was installed but not yet available in PATH.
    echo Please close this window and run the script again.
    pause
    exit /b 1
) ELSE (
    echo Maven is already installed.
)

REM -------------------------
REM Compile Project
REM -------------------------
echo.
echo Downloading dependencies and compiling...
mvn clean compile

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo Compilation failed!
    pause
    exit /b 1
)

REM -------------------------
REM Build Classpath
REM -------------------------
echo.
echo Building classpath...
mvn dependency:build-classpath -q -Dmdep.outputFile=classpath.tmp

SET /P CP=<classpath.tmp
del classpath.tmp

REM -------------------------
REM Run Application
REM -------------------------
echo.
echo Running application...
java -cp "target\classes;%CP%" Main

pause
ENDLOCAL
