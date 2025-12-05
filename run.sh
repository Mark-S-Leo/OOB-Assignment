#!/bin/bash
cd src/main/java
javac Main.java model/*.java file/*.java service/*.java ui/*.java
java Main