#!/bin/bash

# -------------------------
# Check if Maven is installed
# -------------------------
if ! command -v mvn >/dev/null 2>&1; then
    echo "Maven is not installed. Installing Maven..."

    # Detect OS / package manager
    if command -v apt >/dev/null 2>&1; then
        sudo apt update && sudo apt install -y maven
    elif command -v dnf >/dev/null 2>&1; then
        sudo dnf install -y maven
    elif command -v yum >/dev/null 2>&1; then
        sudo yum install -y maven
    elif command -v pacman >/dev/null 2>&1; then
        sudo pacman -Sy --noconfirm maven
    else
        echo "Unsupported package manager. Please install Maven manually."
        exit 1
    fi
else
    echo "Maven is already installed."
fi

# -------------------------
# Compile the project
# -------------------------
echo "Downloading dependencies and compiling..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

# -------------------------
# Run the application
# -------------------------
echo "Running application..."

# Build classpath
CP=$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout 2>/dev/null)

# Run main class from project root
java -cp "target/classes:$CP" Main
