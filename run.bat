@echo off
setlocal enabledelayedexpansion

echo Checking if Maven is installed...

:: ------------------------------------
:: Check if Maven exists
:: ------------------------------------
mvn -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Maven is not installed.
    echo Opening Maven download page...

    start https://downloads.apache.org/maven/maven-3/  || (
        echo Failed to open browser. Please install Maven manually.
        exit /b 1
    )

    echo After installing Maven, run this script again.
    pause
    exit /b 0
) else (
    echo Maven is already installed.
)

:: ------------------------------------
:: Compile the project
:: ------------------------------------
echo Downloading dependencies and compiling...
mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    exit /b 1
)

:: ------------------------------------
:: Run the application
:: ------------------------------------
echo Running application...

cd target\classes || (
    echo target\classes not found!
    exit /b 1
)

echo Building classpath...

:: Run Maven again but capture output to CP variable
for /f "delims=" %%i in ('mvn -q dependency:build-classpath -Dmdep.outputFile=CON') do (
    set CP=%%i
)

:: Running program
echo Launching Java program...
java -cp ".;!CP!" Main

endlocal
