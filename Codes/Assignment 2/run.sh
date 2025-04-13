#!/bin/bash
# Compile Java files
javac -d out $(find src -name "*.java")

# Run the main class
java -cp out topaco.Main
