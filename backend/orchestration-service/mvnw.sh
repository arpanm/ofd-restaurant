#!/bin/bash
#===============================================================================
# FoodAI Platform - Orchestration Service Maven Wrapper
#===============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Source Java 21 setup
source scripts/SET_JAVA_21.sh 2>/dev/null || true

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

print_help() {
    echo "Usage: ./mvnw.sh <command> [options]"
    echo ""
    echo "Commands:"
    echo "  run [profile]   - Run the application (default: dev)"
    echo "  test            - Run all tests"
    echo "  build           - Build the application"
    echo "  clean           - Clean build artifacts"
    echo "  verify          - Run tests with coverage"
    echo "  format          - Format code with Spotless"
    echo "  db-status       - Check MongoDB status"
    echo "  help, -h        - Show this help"
}

check_mongodb() {
    if command -v mongosh &> /dev/null; then
        mongosh --eval "db.adminCommand('ping')" --quiet 2>/dev/null && echo "MongoDB is running" || echo "MongoDB not available"
    else
        echo "MongoDB client not installed"
    fi
}

case "${1:-help}" in
    run)
        PROFILE="${2:-dev}"
        echo -e "${GREEN}Starting orchestration-service with profile: $PROFILE${NC}"
        mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE
        ;;
    test)
        echo -e "${GREEN}Running tests...${NC}"
        mvn clean test
        ;;
    build)
        echo -e "${GREEN}Building application...${NC}"
        mvn clean package -DskipTests
        ;;
    clean)
        echo -e "${GREEN}Cleaning build artifacts...${NC}"
        mvn clean
        ;;
    verify)
        echo -e "${GREEN}Running verification with coverage...${NC}"
        mvn clean verify
        ;;
    format)
        echo -e "${GREEN}Formatting code...${NC}"
        mvn spotless:apply
        ;;
    db-status)
        check_mongodb
        ;;
    help|-h|--help)
        print_help
        ;;
    *)
        echo -e "${RED}Unknown command: $1${NC}"
        print_help
        exit 1
        ;;
esac

