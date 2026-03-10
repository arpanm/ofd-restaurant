#!/bin/bash

# Script to set Java 21 for the user-service project
# Usage: source ./scripts/SET_JAVA_21.sh

echo "🔧 Setting Java 21 for user-service..."
echo ""

# Set JAVA_HOME to Java 21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Verify
echo "✅ JAVA_HOME set to: $JAVA_HOME"
echo ""
java -version
echo ""
echo "🚀 You can now run: ./mvnw.sh test or mvn compile"
echo ""
echo "💡 To make this permanent, add this to your ~/.zshrc or ~/.bashrc:"
echo "   export JAVA_HOME=\$(/usr/libexec/java_home -v 21)"

