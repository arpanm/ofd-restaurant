#!/bin/bash

# =============================================================================
# Maven Wrapper Script for Order Service
# =============================================================================
# This script provides a consistent Maven build environment by:
# 1. Setting JAVA_HOME to Java 21
# 2. Optionally checking MongoDB availability
# 3. Running Maven commands with proper configuration
# =============================================================================

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Print colored message
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# Find Java 21 installation
find_java_21() {
    # Check common Java 21 locations
    local java_paths=(
        "/opt/homebrew/opt/openjdk@21/bin/java"
        "/usr/local/opt/openjdk@21/bin/java"
        "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home/bin/java"
        "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home/bin/java"
        "/Library/Java/JavaVirtualMachines/openjdk-21.jdk/Contents/Home/bin/java"
        "$HOME/.sdkman/candidates/java/21*/bin/java"
    )

    for java_path in "${java_paths[@]}"; do
        # Handle glob patterns
        for expanded_path in $java_path; do
            if [ -x "$expanded_path" ]; then
                JAVA_HOME=$(dirname $(dirname "$expanded_path"))
                export JAVA_HOME
                export PATH="$JAVA_HOME/bin:$PATH"
                print_message "$GREEN" "✓ Using Java 21 from: $JAVA_HOME"
                return 0
            fi
        done
    done

    # Fallback: check if current Java is version 21
    if command -v java &> /dev/null; then
        local java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$java_version" = "21" ]; then
            print_message "$GREEN" "✓ Current Java is version 21"
            return 0
        fi
    fi

    print_message "$RED" "✗ Java 21 not found. Please install Java 21."
    echo "  Install options:"
    echo "    - brew install openjdk@21"
    echo "    - sdk install java 21-tem"
    exit 1
}

# Check if PostgreSQL is available (optional)
check_postgres() {
    if [ "$SKIP_DB_CHECK" != "true" ]; then
        if command -v pg_isready &> /dev/null; then
            if pg_isready -h localhost -p 5432 &> /dev/null; then
                print_message "$GREEN" "✓ PostgreSQL is available"
            else
                print_message "$YELLOW" "⚠ PostgreSQL is not running (tests will use H2)"
            fi
        fi
    fi
}

# Check if MongoDB is available (optional)
check_mongodb() {
    if [ "$SKIP_DB_CHECK" != "true" ]; then
        if command -v mongosh &> /dev/null; then
            if mongosh --quiet --eval "db.adminCommand('ping')" &> /dev/null 2>&1; then
                print_message "$GREEN" "✓ MongoDB is available"
            else
                print_message "$YELLOW" "⚠ MongoDB is not running (tests may use embedded)"
            fi
        elif command -v mongo &> /dev/null; then
            if mongo --quiet --eval "db.adminCommand('ping')" &> /dev/null 2>&1; then
                print_message "$GREEN" "✓ MongoDB is available"
            else
                print_message "$YELLOW" "⚠ MongoDB is not running (tests may use embedded)"
            fi
        fi
    fi
}

# Main execution
main() {
    print_message "$GREEN" "=== Order Service Maven Wrapper ==="
    
    # Find and set Java 21
    find_java_21
    
    # Display Java version
    java -version 2>&1 | head -1
    
    # Check databases (optional)
    check_postgres
    check_mongodb
    
    # Run Maven with all arguments
    print_message "$GREEN" "Running: mvn $@"
    exec mvn "$@"
}

main "$@"

