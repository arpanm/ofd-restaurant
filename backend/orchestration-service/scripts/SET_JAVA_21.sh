#!/bin/bash
#===============================================================================
# FoodAI Platform - Java 21 Environment Setup Script
# Service: orchestration-service
#===============================================================================

# Common Java 21 locations on macOS
JAVA_LOCATIONS=(
    "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
    "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home"
    "/Library/Java/JavaVirtualMachines/amazon-corretto-21.jdk/Contents/Home"
    "/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"
    "/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
    "/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
)

# Find Java 21
find_java_21() {
    for location in "${JAVA_LOCATIONS[@]}"; do
        if [ -d "$location" ]; then
            echo "$location"
            return 0
        fi
    done
    return 1
}

# Set JAVA_HOME
JAVA_21_HOME=$(find_java_21)

if [ -n "$JAVA_21_HOME" ]; then
    export JAVA_HOME="$JAVA_21_HOME"
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "✅ Java 21 configured: $JAVA_HOME"
    java -version
else
    echo "⚠️  Java 21 not found. Please install Temurin JDK 21:"
    echo "   brew install --cask temurin@21"
    exit 1
fi

