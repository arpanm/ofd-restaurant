#!/bin/bash

# Script to run tests and generate reports for restaurant-service

set -e  # Exit on error

echo "=================================="
echo "Restaurant Service - Test Runner"
echo "=================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check if a service is running
check_service() {
    local service=$1
    if brew services list | grep "$service" | grep -q "started"; then
        echo -e "${GREEN}✓ $service is running${NC}"
        return 0
    else
        echo -e "${RED}✗ $service is not running${NC}"
        return 1
    fi
}

# Function to check Java version
check_java() {
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
        if [ "$JAVA_VERSION" = "21" ]; then
            echo -e "${GREEN}✓ Java 21 is configured${NC}"
            return 0
        else
            echo -e "${YELLOW}⚠ Java version is $JAVA_VERSION (Java 21 recommended)${NC}"
            echo -e "${YELLOW}  Run: export JAVA_HOME=\$(/usr/libexec/java_home -v 21)${NC}"
            return 1
        fi
    else
        echo -e "${RED}✗ Java is not installed${NC}"
        return 1
    fi
}

# Pre-flight checks
echo -e "${BLUE}Pre-flight checks...${NC}"
echo ""

CHECKS_PASSED=true

# Check Java version
check_java || CHECKS_PASSED=false

# Check MongoDB
check_service "mongodb-community@7.0" || CHECKS_PASSED=false

# Check Kafka
check_service "kafka" || CHECKS_PASSED=false

echo ""

if [ "$CHECKS_PASSED" = false ]; then
    echo -e "${YELLOW}⚠ Some services are not running!${NC}"
    echo ""
    echo "To start services, run:"
    echo "  ./scripts/manage-services.sh start"
    echo ""
    read -p "Continue anyway? (y/N) " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Aborted."
        exit 1
    fi
fi

echo ""
echo "=================================="
echo "Running Tests"
echo "=================================="
echo ""

# Clean previous builds
echo -e "${BLUE}[1/5] Cleaning previous builds...${NC}"
mvn clean -q

# Compile the project
echo -e "${BLUE}[2/5] Compiling project...${NC}"
mvn compile -q

# Run tests (skip coverage check as it's too strict for now)
echo -e "${BLUE}[3/5] Running tests...${NC}"
mvn test -Djacoco.skip=false

# Generate JaCoCo coverage report
echo -e "${BLUE}[4/5] Generating code coverage report...${NC}"
mvn jacoco:report -q

# Generate Surefire test report
echo -e "${BLUE}[5/5] Generating test report...${NC}"
mvn surefire-report:report -q

echo ""
echo -e "${GREEN}=================================="
echo "Test Execution Complete!"
echo "==================================${NC}"
echo ""
echo "📊 Reports Generated:"
echo "  • Test Results:      target/surefire-reports/"
echo "  • Test HTML Report:  target/site/surefire-report.html"
echo "  • Coverage Report:   target/site/jacoco/index.html"
echo ""
echo "📈 Test Summary:"
if [ -d "target/surefire-reports" ]; then
    TOTAL_TESTS=$(find target/surefire-reports -name "TEST-*.xml" -exec grep -h "tests=" {} \; | sed 's/.*tests="\([0-9]*\)".*/\1/' | awk '{s+=$1} END {print s}')
    FAILURES=$(find target/surefire-reports -name "TEST-*.xml" -exec grep -h "failures=" {} \; | sed 's/.*failures="\([0-9]*\)".*/\1/' | awk '{s+=$1} END {print s}')
    ERRORS=$(find target/surefire-reports -name "TEST-*.xml" -exec grep -h "errors=" {} \; | sed 's/.*errors="\([0-9]*\)".*/\1/' | awk '{s+=$1} END {print s}')
    SKIPPED=$(find target/surefire-reports -name "TEST-*.xml" -exec grep -h "skipped=" {} \; | sed 's/.*skipped="\([0-9]*\)".*/\1/' | awk '{s+=$1} END {print s}')
    
    PASSED=$((TOTAL_TESTS - FAILURES - ERRORS - SKIPPED))
    
    echo "  Total:   $TOTAL_TESTS"
    echo -e "  ${GREEN}Passed:  $PASSED${NC}"
    if [ "$FAILURES" -gt 0 ]; then
        echo -e "  ${RED}Failed:  $FAILURES${NC}"
    fi
    if [ "$ERRORS" -gt 0 ]; then
        echo -e "  ${RED}Errors:  $ERRORS${NC}"
    fi
    if [ "$SKIPPED" -gt 0 ]; then
        echo -e "  ${YELLOW}Skipped: $SKIPPED${NC}"
    fi
fi
echo ""
echo "To view reports:"
echo "  open target/site/jacoco/index.html        # Coverage Report"
echo "  open target/site/surefire-report.html     # Test Report"
echo ""


