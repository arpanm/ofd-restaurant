#!/bin/bash
################################################################################
# SET_JAVA_21.sh - Java 21 Environment Setup Script
#
# This script sets up Java 21 environment for the menu-service.
#
# Usage:
#   source ./scripts/SET_JAVA_21.sh
#
# Note: Must be sourced (not executed) to affect the current shell session
################################################################################

# Colors for output
readonly GREEN='\033[0;32m'
readonly RED='\033[0;31m'
readonly YELLOW='\033[0;33m'
readonly NC='\033[0m' # No Color

echo "Setting up Java 21 environment..."

# Check for macOS java_home utility
if command -v /usr/libexec/java_home &> /dev/null; then
    JAVA_21_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null || echo "")
    
    if [ -n "$JAVA_21_HOME" ]; then
        export JAVA_HOME="$JAVA_21_HOME"
        export PATH="$JAVA_HOME/bin:$PATH"
        echo -e "${GREEN}✓ Java 21 configured: $JAVA_HOME${NC}"
        java -version 2>&1 | head -n 1
    else
        echo -e "${RED}✗ Java 21 not found!${NC}"
        echo -e "${YELLOW}Install with: brew install openjdk@21${NC}"
        return 1 2>/dev/null || exit 1
    fi
else
    # Linux/other systems - check common locations
    for java_path in /usr/lib/jvm/java-21-openjdk /usr/lib/jvm/java-21-openjdk-amd64 /opt/java/openjdk-21; do
        if [ -d "$java_path" ]; then
            export JAVA_HOME="$java_path"
            export PATH="$JAVA_HOME/bin:$PATH"
            echo -e "${GREEN}✓ Java 21 configured: $JAVA_HOME${NC}"
            java -version 2>&1 | head -n 1
            return 0 2>/dev/null || exit 0
        fi
    done
    
    echo -e "${RED}✗ Java 21 not found!${NC}"
    echo -e "${YELLOW}Please install Java 21 manually${NC}"
    return 1 2>/dev/null || exit 1
fi

