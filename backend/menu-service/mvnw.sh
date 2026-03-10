#!/bin/bash

################################################################################
# Maven Wrapper Script for Menu Service
#
# This script handles:
# - Java version setup (Java 21)
# - MongoDB availability check
# - Mock data loading (if database is empty)
# - Application startup with profile selection
#
# Usage:
#   ./mvnw.sh [command] [profile]
#
# Commands:
#   run       - Run the application (default)
#   test      - Run tests
#   build     - Build the application
#   clean     - Clean build artifacts
#   install   - Clean install
#
# Profiles:
#   dev       - Development profile (default)
#   test      - Test profile
#   prod      - Production profile
#
# Examples:
#   ./mvnw.sh                    # Run in dev mode
#   ./mvnw.sh run prod           # Run in production mode
#   ./mvnw.sh test               # Run tests
#   ./mvnw.sh build dev          # Build for dev
################################################################################

set -e  # Exit on error

# Colors for output
readonly GREEN='\033[0;32m'
readonly BLUE='\033[0;34m'
readonly RED='\033[0;31m'
readonly YELLOW='\033[0;33m'
readonly CYAN='\033[0;36m'
readonly BOLD='\033[1m'
readonly NC='\033[0m' # No Color

# Service configuration
readonly SERVICE_NAME="menu-service"
readonly SERVICE_PORT=8082
readonly DB_NAME_PREFIX="foodai_menu"
readonly COLLECTION_NAME="menuItems"

# Script directory
readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Check for help flag first
if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    show_help() {
        cat << EOF
$(echo -e "${BOLD}")Menu Service - Maven Wrapper$(echo -e "${NC}")

$(echo -e "${BOLD}")USAGE:$(echo -e "${NC}")
    ./mvnw.sh [command] [profile] [options]

$(echo -e "${BOLD}")COMMANDS:$(echo -e "${NC}")
    run          Run the application (default)
    test         Run tests
    build        Build the application
    clean        Clean build artifacts
    install      Clean install
    compile      Compile source code
    db-status    Show database status

$(echo -e "${BOLD}")PROFILES:$(echo -e "${NC}")
    dev          Development profile (default)
    test         Test profile
    prod         Production profile

$(echo -e "${BOLD}")OPTIONS:$(echo -e "${NC}")
    --skip-mongo-check    Skip MongoDB availability check
    --skip-data-check     Skip mock data loading check
    --force-clean         Run tests during install

$(echo -e "${BOLD}")EXAMPLES:$(echo -e "${NC}")
    $(echo -e "${GREEN}")./mvnw.sh$(echo -e "${NC}")                          # Run in dev mode
    $(echo -e "${GREEN}")./mvnw.sh run prod$(echo -e "${NC}")                 # Run in production mode
    $(echo -e "${GREEN}")./mvnw.sh test$(echo -e "${NC}")                     # Run tests
    $(echo -e "${GREEN}")./mvnw.sh build dev$(echo -e "${NC}")                # Build for dev
    $(echo -e "${GREEN}")./mvnw.sh db-status dev$(echo -e "${NC}")            # Show database status

$(echo -e "${BOLD}")PREREQUISITES:$(echo -e "${NC}")
    - Java 21 installed
    - MongoDB installed and running
    - Maven installed
EOF
    }
    show_help
    exit 0
fi

# Default values
COMMAND="${1:-run}"
PROFILE="${2:-dev}"
SKIP_MONGO_CHECK=false
SKIP_DATA_CHECK=false
FORCE_CLEAN=false

# Parse command line arguments
shift || true
while [[ $# -gt 0 ]]; do
    case $1 in
        --skip-mongo-check)
            SKIP_MONGO_CHECK=true
            shift
            ;;
        --skip-data-check)
            SKIP_DATA_CHECK=true
            shift
            ;;
        --force-clean)
            FORCE_CLEAN=true
            shift
            ;;
        dev|test|prod)
            PROFILE="$1"
            shift
            ;;
        *)
            shift
            ;;
    esac
done

################################################################################
# Helper Functions
################################################################################

print_header() {
    echo -e "\n${CYAN}${BOLD}========================================${NC}"
    echo -e "${CYAN}${BOLD}$1${NC}"
    echo -e "${CYAN}${BOLD}========================================${NC}\n"
}

print_step() {
    echo -e "${BLUE}[$(date +'%H:%M:%S')] $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo -e "${CYAN}ℹ $1${NC}"
}

################################################################################
# Java Version Check and Setup
################################################################################

setup_java() {
    print_step "Checking Java version..."
    
    # Try to find Java 21
    if command -v /usr/libexec/java_home &> /dev/null; then
        JAVA_21_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null || echo "")
        
        if [ -n "$JAVA_21_HOME" ]; then
            export JAVA_HOME="$JAVA_21_HOME"
            export PATH="$JAVA_HOME/bin:$PATH"
            print_success "Java 21 configured: $JAVA_HOME"
        else
            print_error "Java 21 not found!"
            print_info "Please install Java 21: brew install openjdk@21"
            exit 1
        fi
    else
        # Linux/other systems
        if [ -n "$JAVA_HOME" ]; then
            JAVA_VERSION=$("$JAVA_HOME/bin/java" -version 2>&1 | head -n 1 | awk -F '"' '{print $2}' | cut -d'.' -f1)
            if [ "$JAVA_VERSION" = "21" ]; then
                print_success "Java 21 already configured"
            else
                print_error "Java $JAVA_VERSION detected, but Java 21 is required"
                exit 1
            fi
        else
            print_error "JAVA_HOME not set and java_home utility not found"
            exit 1
        fi
    fi
    
    # Verify Java version
    java -version 2>&1 | head -n 1
}

################################################################################
# MongoDB Configuration Parsing
################################################################################

get_mongodb_config() {
    local profile=$1
    local config_file=""
    
    # Determine config file based on profile
    if [ "$profile" = "test" ]; then
        config_file="src/test/resources/application-test.yml"
    elif [ -f "src/main/resources/application-$profile.yml" ]; then
        config_file="src/main/resources/application-$profile.yml"
    else
        config_file="src/main/resources/application.yml"
    fi
    
    if [ ! -f "$config_file" ]; then
        print_warning "Config file not found: $config_file"
        echo "mongodb://localhost:27017/${DB_NAME_PREFIX}_dev"
        return
    fi
    
    # Parse MongoDB URI from YAML
    local mongo_uri=$(grep -A 5 "mongodb:" "$config_file" | grep "uri:" | awk '{print $2}' | tr -d '"' | head -n 1)
    
    if [ -n "$mongo_uri" ]; then
        echo "$mongo_uri"
    else
        echo "mongodb://localhost:27017/${DB_NAME_PREFIX}_${profile}"
    fi
}

get_database_name() {
    local mongo_uri=$1
    echo "$mongo_uri" | sed -E 's|mongodb://[^/]+/([^?]+).*|\1|'
}

################################################################################
# MongoDB Checks
################################################################################

check_mongodb() {
    if [ "$SKIP_MONGO_CHECK" = true ]; then
        print_warning "Skipping MongoDB check (--skip-mongo-check flag)"
        return 0
    fi
    
    print_step "Checking MongoDB..."
    
    # Check if MongoDB is running
    if ! brew services list 2>/dev/null | grep -q "mongodb-community@7.0.*started"; then
        if ! pgrep -x mongod > /dev/null; then
            print_error "MongoDB is not running!"
            print_info "Attempting to start MongoDB..."
            
            if command -v brew &> /dev/null; then
                brew services start mongodb-community@7.0 || {
                    print_error "Failed to start MongoDB"
                    print_info "Please start MongoDB manually:"
                    print_info "  brew services start mongodb-community@7.0"
                    exit 1
                }
                sleep 3
            else
                print_error "Cannot start MongoDB (brew not found)"
                print_info "Please start MongoDB manually"
                exit 1
            fi
        fi
    fi
    
    # Verify MongoDB is accessible
    if command -v mongosh &> /dev/null; then
        if mongosh --eval "db.version()" --quiet > /dev/null 2>&1; then
            local version=$(mongosh --eval "db.version()" --quiet 2>/dev/null | tr -d '"')
            print_success "MongoDB is running (version: $version)"
            return 0
        else
            print_error "MongoDB is not accessible"
            exit 1
        fi
    else
        print_warning "mongosh not found, skipping MongoDB version check"
        print_info "Install: brew install mongosh"
    fi
}

################################################################################
# Database Status
################################################################################

show_database_status() {
    local mongo_uri=$(get_mongodb_config "$PROFILE")
    local db_name=$(get_database_name "$mongo_uri")
    
    print_step "Database Status for: $db_name"
    echo
    
    if ! command -v mongosh &> /dev/null; then
        print_error "mongosh not found!"
        print_info "Install: brew install mongosh"
        exit 1
    fi
    
    # Check connection
    if ! mongosh "$db_name" --eval "db.version()" --quiet > /dev/null 2>&1; then
        print_error "Cannot connect to database: $db_name"
        exit 1
    fi
    
    print_success "Connected to database: $db_name"
    echo
    
    # Get menu item count
    local item_count=$(mongosh "$db_name" --eval "db.${COLLECTION_NAME}.countDocuments()" --quiet 2>/dev/null || echo "0")
    print_info "Total Menu Items: $item_count"
    
    # Get category count
    local category_count=$(mongosh "$db_name" --eval "db.menuCategories.countDocuments()" --quiet 2>/dev/null || echo "0")
    print_info "Total Categories: $category_count"
    
    echo
}

################################################################################
# Port Check
################################################################################

check_port() {
    # Check if port is already in use
    if lsof -Pi :${SERVICE_PORT} -sTCP:LISTEN -t >/dev/null 2>&1; then
        print_warning "Port ${SERVICE_PORT} is already in use"
        
        read -p "Do you want to kill the process and continue? (y/N): " -n 1 -r
        echo
        
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            local pid=$(lsof -ti:${SERVICE_PORT})
            print_info "Killing process $pid..."
            kill -9 "$pid" 2>/dev/null || true
            sleep 2
            print_success "Port ${SERVICE_PORT} is now available"
        else
            print_info "Please stop the process using port ${SERVICE_PORT} and try again"
            exit 1
        fi
    fi
}

################################################################################
# Maven Commands
################################################################################

run_maven_command() {
    local command=$1
    local profile=$2
    
    case $command in
        run)
            print_step "Starting application in $profile mode..."
            check_port
            mvn spring-boot:run -Dspring-boot.run.profiles="$profile"
            ;;
            
        test)
            print_step "Running tests..."
            mvn test -Dspring.profiles.active=test
            ;;
            
        build)
            print_step "Building application for $profile profile..."
            mvn clean package -DskipTests -Dspring.profiles.active="$profile"
            ;;
            
        clean)
            print_step "Cleaning build artifacts..."
            mvn clean
            ;;
            
        install)
            print_step "Clean installing application..."
            if [ "$FORCE_CLEAN" = true ]; then
                mvn clean install
            else
                mvn clean install -DskipTests
            fi
            ;;
            
        compile)
            print_step "Compiling application..."
            mvn compile
            ;;
            
        db-status)
            show_database_status
            ;;
            
        *)
            print_error "Unknown command: $command"
            print_info "Available commands: run, test, build, clean, install, compile, db-status"
            exit 1
            ;;
    esac
}

################################################################################
# Main Execution
################################################################################

main() {
    print_header "Menu Service - Maven Wrapper"
    
    print_info "Command: $COMMAND"
    print_info "Profile: $PROFILE"
    echo
    
    # Step 1: Setup Java
    setup_java
    echo
    
    # Step 2: Check MongoDB (skip for clean/compile commands)
    if [ "$COMMAND" != "clean" ] && [ "$COMMAND" != "compile" ]; then
        check_mongodb
        echo
    fi
    
    # Step 3: Run Maven command
    run_maven_command "$COMMAND" "$PROFILE"
}

# Run main function
main

################################################################################
# Exit Handler
################################################################################

trap 'echo -e "\n${YELLOW}Script interrupted${NC}"' INT TERM
