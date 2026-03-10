#!/bin/bash

################################################################################
# Maven Wrapper Script for Restaurant Service
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

# Script directory
readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Check for help flag first
if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    show_help() {
        cat << EOF
$(echo -e "${BOLD}")Restaurant Service - Maven Wrapper$(echo -e "${NC}")

$(echo -e "${BOLD}")USAGE:$(echo -e "${NC}")
    ./mvnw.sh [command] [profile] [options]

$(echo -e "${BOLD}")COMMANDS:$(echo -e "${NC}")
    run          Run the application (default)
    test         Run tests
    build        Build the application
    clean        Clean build artifacts
    install      Clean install
    compile      Compile source code
    db-clean     Clean MongoDB database
    db-status    Show database status
    db-mock      Load mock data into MongoDB

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
    $(echo -e "${GREEN}")./mvnw.sh db-mock dev$(echo -e "${NC}")              # Load mock data
    $(echo -e "${GREEN}")./mvnw.sh db-clean dev$(echo -e "${NC}")             # Clean database
    $(echo -e "${GREEN}")./mvnw.sh install --force-clean$(echo -e "${NC}")    # Clean install with tests
    $(echo -e "${GREEN}")./mvnw.sh run dev --skip-data-check$(echo -e "${NC}") # Run without checking mock data

$(echo -e "${BOLD}")PREREQUISITES:$(echo -e "${NC}")
    - Java 21 installed
    - MongoDB installed and running
    - Maven installed

For more information, see MVNW_GUIDE.md
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
        echo "mongodb://localhost:27017/foodai_restaurant_dev"
        return
    fi
    
    # Parse MongoDB URI from YAML
    # First try to get full URI
    local mongo_uri=$(grep -A 5 "mongodb:" "$config_file" | grep "uri:" | awk '{print $2}' | tr -d '"' | head -n 1)
    
    if [ -n "$mongo_uri" ]; then
        echo "$mongo_uri"
    else
        # Build URI from host/port/database
        local host=$(grep -A 5 "mongodb:" "$config_file" | grep "host:" | awk '{print $2}' | tr -d '"' | head -n 1 || echo "localhost")
        local port=$(grep -A 5 "mongodb:" "$config_file" | grep "port:" | awk '{print $2}' | tr -d '"' | head -n 1 || echo "27017")
        local database=$(grep -A 5 "mongodb:" "$config_file" | grep "database:" | awk '{print $2}' | tr -d '"' | head -n 1 || echo "foodai_restaurant_${profile}")
        
        echo "mongodb://${host}:${port}/${database}"
    fi
}

get_database_name() {
    local mongo_uri=$1
    # Extract database name from URI
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
# Mock Data Check and Loading
################################################################################

check_and_load_mock_data() {
    if [ "$SKIP_DATA_CHECK" = true ]; then
        print_warning "Skipping data check (--skip-data-check flag)"
        return 0
    fi
    
    local mongo_uri=$(get_mongodb_config "$PROFILE")
    local db_name=$(get_database_name "$mongo_uri")
    
    print_step "Checking data in database: $db_name"
    
    if ! command -v mongosh &> /dev/null; then
        print_warning "mongosh not found, skipping data check"
        return 0
    fi
    
    # Check if restaurants collection has data
    local count=$(mongosh "$db_name" --eval "db.restaurants.countDocuments()" --quiet 2>/dev/null || echo "0")
    
    if [ "$count" -eq 0 ]; then
        print_warning "No restaurant data found in database: $db_name"
        
        # Only load mock data in dev/test profiles
        if [ "$PROFILE" = "dev" ] || [ "$PROFILE" = "test" ]; then
            print_info "Loading mock data via MongoDB..."
            
            # Use MongoDB script for pre-application data loading
            if [ -f "./scripts/insert-mock-data.js" ]; then
                mongosh "$db_name" --quiet < ./scripts/insert-mock-data.js > /dev/null 2>&1 && {
                    print_success "Mock data loaded successfully"
                } || {
                    print_warning "Failed to load mock data automatically"
                    print_info "You can load it manually after app starts:"
                    print_info "  ./scripts/load-mock-data.sh"
                }
            else
                print_warning "Mock data script not found: ./scripts/insert-mock-data.js"
                print_info "You can load it manually after app starts:"
                print_info "  ./scripts/load-mock-data.sh"
            fi
        else
            print_info "Production profile detected - mock data not loaded automatically"
            print_info "Please ensure your production database is properly configured"
        fi
    else
        print_success "Found $count restaurant(s) in database"
    fi
}

################################################################################
# Database Management
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
    
    # Get restaurant count
    local count=$(mongosh "$db_name" --eval "db.restaurants.countDocuments()" --quiet 2>/dev/null || echo "0")
    print_info "Total Restaurants: $count"
    
    if [ "$count" -gt 0 ]; then
        # Get status breakdown
        echo
        print_info "Status Breakdown:"
        mongosh "$db_name" --quiet --eval "
            db.restaurants.aggregate([
                { \$group: { _id: '\$status', count: { \$sum: 1 } } },
                { \$sort: { _id: 1 } }
            ]).forEach(function(doc) {
                print('  ' + doc._id + ': ' + doc.count);
            });
        " 2>/dev/null || print_warning "Could not get status breakdown"
        
        # Get cuisine types
        echo
        print_info "Sample Restaurants:"
        mongosh "$db_name" --quiet --eval "
            db.restaurants.find({}, {name: 1, cuisineTypes: 1, status: 1, _id: 0})
                .limit(5)
                .forEach(function(doc) {
                    print('  • ' + doc.name + ' (' + doc.cuisineTypes.join(', ') + ') - ' + doc.status);
                });
        " 2>/dev/null || print_warning "Could not list restaurants"
    else
        echo
        print_warning "Database is empty"
        print_info "Load mock data with: ./mvnw.sh db-mock $PROFILE"
    fi
    
    echo
}

load_mock_data_db() {
    local mongo_uri=$(get_mongodb_config "$PROFILE")
    local db_name=$(get_database_name "$mongo_uri")
    
    print_step "Loading mock data into: $db_name"
    
    if ! command -v mongosh &> /dev/null; then
        print_error "mongosh not found!"
        print_info "Install: brew install mongosh"
        exit 1
    fi
    
    # Check current count
    local count=$(mongosh "$db_name" --eval "db.restaurants.countDocuments()" --quiet 2>/dev/null || echo "0")
    
    if [ "$count" -gt 0 ]; then
        print_warning "Database already has $count restaurant(s)"
        read -p "$(echo -e "${YELLOW}Do you want to clear and reload? (y/N): ${NC}")" -n 1 -r
        echo
        
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            print_info "Clearing existing data..."
            mongosh "$db_name" --eval "db.restaurants.deleteMany({})" --quiet > /dev/null 2>&1
        else
            print_info "Operation cancelled"
            return 0
        fi
    fi
    
    # Load mock data
    if [ -f "./scripts/insert-mock-data.js" ]; then
        print_info "Loading mock data via MongoDB script..."
        
        if mongosh "$db_name" --quiet < ./scripts/insert-mock-data.js; then
            echo
            print_success "Mock data loaded successfully!"
            
            # Show summary
            local new_count=$(mongosh "$db_name" --eval "db.restaurants.countDocuments()" --quiet 2>/dev/null || echo "0")
            print_info "Total restaurants: $new_count"
        else
            print_error "Failed to load mock data"
            exit 1
        fi
    else
        print_error "Mock data script not found: ./scripts/insert-mock-data.js"
        print_info "Alternative: Load via API after starting the app:"
        print_info "  1. ./mvnw.sh run $PROFILE"
        print_info "  2. ./scripts/load-mock-data.sh (in new terminal)"
        exit 1
    fi
}

clean_database() {
    local mongo_uri=$(get_mongodb_config "$PROFILE")
    local db_name=$(get_database_name "$mongo_uri")
    
    print_step "Cleaning database: $db_name"
    
    if ! command -v mongosh &> /dev/null; then
        print_error "mongosh not found!"
        print_info "Install: brew install mongosh"
        exit 1
    fi
    
    # Check current count
    local count=$(mongosh "$db_name" --eval "db.restaurants.countDocuments()" --quiet 2>/dev/null || echo "0")
    
    if [ "$count" -eq 0 ]; then
        print_info "Database is already empty (0 documents)"
        return 0
    fi
    
    print_warning "Found $count restaurant(s) in database"
    
    # Confirm deletion
    read -p "$(echo -e "${YELLOW}Are you sure you want to delete all data? (y/N): ${NC}")" -n 1 -r
    echo
    
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Operation cancelled"
        return 0
    fi
    
    # Delete all restaurants
    mongosh "$db_name" --eval "db.restaurants.deleteMany({})" --quiet > /dev/null 2>&1
    
    # Verify deletion
    local new_count=$(mongosh "$db_name" --eval "db.restaurants.countDocuments()" --quiet 2>/dev/null || echo "0")
    
    if [ "$new_count" -eq 0 ]; then
        print_success "Successfully deleted $count restaurant(s)"
        
        # Ask if user wants to reload mock data
        read -p "$(echo -e "${CYAN}Load mock data now? (y/N): ${NC}")" -n 1 -r
        echo
        
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            if [ -f "./scripts/insert-mock-data.js" ]; then
                print_info "Loading mock data via MongoDB..."
                mongosh "$db_name" --quiet < ./scripts/insert-mock-data.js && {
                    print_success "Mock data loaded successfully"
                } || {
                    print_error "Failed to load mock data"
                    return 1
                }
            else
                print_error "Mock data script not found: ./scripts/insert-mock-data.js"
                print_info "You can load it via API after starting the app:"
                print_info "  ./scripts/load-mock-data.sh"
                return 1
            fi
        fi
    else
        print_error "Failed to delete data (still has $new_count documents)"
        return 1
    fi
}

################################################################################
# Port Check
################################################################################

check_port() {
    local port=8081
    
    # Check if port is already in use
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        print_warning "Port $port is already in use"
        
        read -p "Do you want to kill the process and continue? (y/N): " -n 1 -r
        echo
        
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            local pid=$(lsof -ti:$port)
            print_info "Killing process $pid..."
            kill -9 "$pid" 2>/dev/null || true
            sleep 2
            print_success "Port $port is now available"
        else
            print_info "Please stop the process using port $port and try again"
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
            
        db-clean)
            clean_database
            ;;
            
        db-status)
            show_database_status
            ;;
            
        db-mock)
            load_mock_data_db
            ;;
            
        *)
            print_error "Unknown command: $command"
            print_info "Available commands: run, test, build, clean, install, compile, db-clean, db-status, db-mock"
            exit 1
            ;;
    esac
}

################################################################################
# Main Execution
################################################################################

main() {
    print_header "Restaurant Service - Maven Wrapper"
    
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
    elif [ "$COMMAND" = "db-clean" ] || [ "$COMMAND" = "db-status" ] || [ "$COMMAND" = "db-mock" ]; then
        check_mongodb
        echo
    fi
    
    # Step 3: Check and load mock data (only for run command)
    if [ "$COMMAND" = "run" ]; then
        check_and_load_mock_data
        echo
    fi
    
    # Step 4: Run Maven command
    run_maven_command "$COMMAND" "$PROFILE"
}

# Run main function
main

################################################################################
# Exit Handler
################################################################################

trap 'echo -e "\n${YELLOW}Script interrupted${NC}"' INT TERM

