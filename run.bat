@echo off
REM Clean previous build
if exist target\classes rmdir /s /q target\classes
mkdir target\classes

REM Compile to target/classes directory
javac -d target/classes -sourcepath src/main/java src/main/java/Main.java src/main/java/model/*.java src/main/java/file/*.java src/main/java/service/*.java src/main/java/ui/*.java src/main/java/util/*.java

REM Copy resources to target
xcopy /s /y /q src\main\resources\*.* target\classes\ >nul 2>&1

REM Run from target/classes
cd target\classes
java Main