#!/bin/bash
# Clean previous build
rm -rf target/classes
mkdir -p target/classes

# Compile to target/classes directory
javac -d target/classes -sourcepath src/main/java \
  src/main/java/Main.java \
  src/main/java/model/*.java \
  src/main/java/file/*.java \
  src/main/java/service/*.java \
  src/main/java/ui/*.java \
  src/main/java/util/*.java

# Copy resources to target
cp -r src/main/resources/* target/classes/ 2>/dev/null || true

# Run from target/classes
cd target/classes
java Main