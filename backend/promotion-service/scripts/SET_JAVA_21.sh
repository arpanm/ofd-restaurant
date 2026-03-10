#!/bin/bash
# Script to set Java 21 environment for promotion-service
# Usage: source scripts/SET_JAVA_21.sh

# Detect OS
OS_TYPE=$(uname -s)

if [ "$OS_TYPE" = "Darwin" ]; then
    # macOS - Check common Java 21 installation paths
    if [ -d "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
    elif [ -d "/Library/Java/JavaVirtualMachines/openjdk-21.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/openjdk-21.jdk/Contents/Home"
    elif [ -d "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home"
    elif [ -d "/opt/homebrew/opt/openjdk@21" ]; then
        export JAVA_HOME="/opt/homebrew/opt/openjdk@21"
    elif [ -d "/usr/local/opt/openjdk@21" ]; then
        export JAVA_HOME="/usr/local/opt/openjdk@21"
    else
        echo "Warning: Java 21 not found in common macOS locations"
        echo "Please install Java 21 using: brew install openjdk@21"
    fi
elif [ "$OS_TYPE" = "Linux" ]; then
    # Linux - Check common Java 21 installation paths
    if [ -d "/usr/lib/jvm/java-21-openjdk-amd64" ]; then
        export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
    elif [ -d "/usr/lib/jvm/java-21-openjdk" ]; then
        export JAVA_HOME="/usr/lib/jvm/java-21-openjdk"
    elif [ -d "/usr/lib/jvm/temurin-21-jdk-amd64" ]; then
        export JAVA_HOME="/usr/lib/jvm/temurin-21-jdk-amd64"
    else
        echo "Warning: Java 21 not found in common Linux locations"
        echo "Please install Java 21 using your package manager"
    fi
fi

if [ -n "$JAVA_HOME" ]; then
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "JAVA_HOME set to: $JAVA_HOME"
    echo "Java version:"
    java -version 2>&1 | head -1
else
    echo "Error: Could not find Java 21 installation"
    echo "Please install Java 21 and set JAVA_HOME manually"
    exit 1
fi

