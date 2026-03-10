#!/bin/bash

# =============================================================================
# Script to set JAVA_HOME to Java 21
# =============================================================================
# Usage: source scripts/SET_JAVA_21.sh
# =============================================================================

# Check common Java 21 locations
JAVA_PATHS=(
    "/opt/homebrew/opt/openjdk@21"
    "/usr/local/opt/openjdk@21"
    "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
    "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home"
    "/Library/Java/JavaVirtualMachines/openjdk-21.jdk/Contents/Home"
)

for JAVA_PATH in "${JAVA_PATHS[@]}"; do
    if [ -d "$JAVA_PATH" ]; then
        export JAVA_HOME="$JAVA_PATH"
        export PATH="$JAVA_HOME/bin:$PATH"
        echo "JAVA_HOME set to: $JAVA_HOME"
        java -version
        exit 0
    fi
done

# Check SDKMAN
if [ -d "$HOME/.sdkman/candidates/java" ]; then
    JAVA21_PATH=$(ls -d $HOME/.sdkman/candidates/java/21* 2>/dev/null | head -1)
    if [ -n "$JAVA21_PATH" ]; then
        export JAVA_HOME="$JAVA21_PATH"
        export PATH="$JAVA_HOME/bin:$PATH"
        echo "JAVA_HOME set to: $JAVA_HOME"
        java -version
        exit 0
    fi
fi

echo "Java 21 not found. Please install it:"
echo "  brew install openjdk@21"
echo "  sdk install java 21-tem"
exit 1

