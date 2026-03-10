#!/bin/bash
# Maven wrapper script for promotion-service
# Ensures correct Java version and provides convenient build commands

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Source Java 21 setup
source scripts/SET_JAVA_21.sh

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

echo_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

echo_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if parent mvnw exists
MVNW="../mvnw"
if [ ! -f "$MVNW" ]; then
    MVNW="mvn"
fi

show_help() {
    echo "Usage: ./mvnw.sh [command]"
    echo ""
    echo "Commands:"
    echo "  build       - Clean and build the project"
    echo "  test        - Run all tests"
    echo "  verify      - Run full verification (compile, test, quality checks)"
    echo "  run         - Run the application"
    echo "  clean       - Clean the build directory"
    echo "  format      - Format code using Spotless"
    echo "  quality     - Run quality checks (Spotless, Checkstyle)"
    echo "  help        - Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./mvnw.sh build"
    echo "  ./mvnw.sh test"
    echo "  ./mvnw.sh run"
}

case "${1:-help}" in
    build)
        echo_info "Building promotion-service..."
        $MVNW clean compile -pl promotion-service -am
        echo_info "Build completed successfully"
        ;;
    test)
        echo_info "Running tests..."
        $MVNW test -pl promotion-service
        echo_info "Tests completed"
        ;;
    verify)
        echo_info "Running full verification..."
        $MVNW verify -pl promotion-service
        echo_info "Verification completed"
        ;;
    run)
        echo_info "Starting promotion-service..."
        $MVNW spring-boot:run -pl promotion-service
        ;;
    clean)
        echo_info "Cleaning build directory..."
        $MVNW clean -pl promotion-service
        echo_info "Clean completed"
        ;;
    format)
        echo_info "Formatting code..."
        $MVNW spotless:apply -pl promotion-service
        echo_info "Formatting completed"
        ;;
    quality)
        echo_info "Running quality checks..."
        $MVNW spotless:check checkstyle:check -pl promotion-service
        echo_info "Quality checks completed"
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo_error "Unknown command: $1"
        show_help
        exit 1
        ;;
esac

