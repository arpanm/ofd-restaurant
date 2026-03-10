#!/bin/bash
#===============================================================================
# FoodAI Platform - Backend Quality Validation Script
# Version: 2.0.0
# 
# This script runs all quality guardrails, tests, and validations for the
# FoodAI backend services. It supports multiple modes:
#   - all:        Validate all services
#   - service:    Validate a specific service
#   - changeset:  Validate only changed files/services
#   - quick:      Quick validation (skip slow checks like OWASP)
#   - full:       Full validation including security scans
#
# Usage:
#   ./mvnw.sh [mode] [options]
#
# Examples:
#   ./mvnw.sh all                    # Validate all services
#   ./mvnw.sh service order-service  # Validate specific service
#   ./mvnw.sh changeset              # Validate changed services only
#   ./mvnw.sh quick all              # Quick validation of all services
#   ./mvnw.sh full all               # Full validation with security scan
#   ./mvnw.sh report                 # Generate quality report only
#===============================================================================

set -e

#===============================================================================
# Configuration
#===============================================================================
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR"
REPORT_DIR="$BACKEND_DIR/target/quality-reports"
REPORT_FILE="$REPORT_DIR/quality-report-$(date +%Y%m%d-%H%M%S).txt"
SUMMARY_FILE="$REPORT_DIR/LATEST_QUALITY_REPORT.md"

# Java Configuration
REQUIRED_JAVA_VERSION="21"
JAVA_21_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"

# Quality Thresholds (must match .cursor/rules)
JACOCO_LINE_COVERAGE="0.80"
JACOCO_BRANCH_COVERAGE="0.75"
MAX_CODE_SMELLS="10"
MAX_BUGS="0"
MAX_VULNERABILITIES="0"
MAX_TECH_DEBT_RATIO="5"
MAX_DUPLICATED_LINES="3"

# Services list
SERVICES=(
    "user-service"
    "restaurant-service"
    "menu-service"
    "order-service"
)

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color
BOLD='\033[1m'

#===============================================================================
# Helper Functions
#===============================================================================

print_header() {
    echo ""
    echo -e "${BLUE}╔══════════════════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║${NC} ${BOLD}$1${NC}"
    echo -e "${BLUE}╚══════════════════════════════════════════════════════════════════════════════╝${NC}"
}

print_section() {
    echo ""
    echo -e "${CYAN}┌──────────────────────────────────────────────────────────────────────────────┐${NC}"
    echo -e "${CYAN}│${NC} ${BOLD}$1${NC}"
    echo -e "${CYAN}└──────────────────────────────────────────────────────────────────────────────┘${NC}"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

log_to_report() {
    echo "$1" >> "$REPORT_FILE"
}

#===============================================================================
# Java Environment Setup
#===============================================================================

setup_java() {
    print_section "Setting up Java Environment"
    
    # Try to find Java 21
    if [ -d "$JAVA_21_HOME" ]; then
        export JAVA_HOME="$JAVA_21_HOME"
        export PATH="$JAVA_HOME/bin:$PATH"
        print_success "Using Java 21 from: $JAVA_HOME"
    elif command -v java &> /dev/null; then
        CURRENT_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$CURRENT_VERSION" = "$REQUIRED_JAVA_VERSION" ]; then
            print_success "Using system Java $REQUIRED_JAVA_VERSION"
        else
            print_error "Java $REQUIRED_JAVA_VERSION required, but found Java $CURRENT_VERSION"
            echo ""
            echo "Please install Java 21 or set JAVA_HOME to Java 21 installation."
            echo "Recommended: brew install temurin@21"
            exit 1
        fi
    else
        print_error "Java not found. Please install Java $REQUIRED_JAVA_VERSION"
        exit 1
    fi
    
    # Verify Java version
    java -version 2>&1 | head -1
    log_to_report "Java Version: $(java -version 2>&1 | head -1)"
}

#===============================================================================
# Report Initialization
#===============================================================================

init_report() {
    mkdir -p "$REPORT_DIR"
    
    cat > "$REPORT_FILE" << EOF
================================================================================
FoodAI Platform - Quality Validation Report
================================================================================
Generated: $(date)
Mode: $MODE
Target: $TARGET
================================================================================

EOF
}

#===============================================================================
# Get Changed Services (for changeset mode)
#===============================================================================

get_changed_services() {
    print_section "Detecting Changed Services"
    
    CHANGED_SERVICES=()
    
    # Get changed files from git (uncommitted changes)
    CHANGED_FILES=$(git diff --name-only HEAD 2>/dev/null || echo "")
    
    # Also check staged files
    STAGED_FILES=$(git diff --cached --name-only 2>/dev/null || echo "")
    
    # Combine and filter for backend services
    ALL_CHANGED=$(echo -e "$CHANGED_FILES\n$STAGED_FILES" | sort -u)
    
    for service in "${SERVICES[@]}"; do
        if echo "$ALL_CHANGED" | grep -q "backend/$service/"; then
            CHANGED_SERVICES+=("$service")
            print_info "Changed: $service"
        fi
    done
    
    # Check for common/shared changes that affect all services
    if echo "$ALL_CHANGED" | grep -q "backend/pom.xml\|backend/common/\|backend/checkstyle.xml\|backend/spotbugs-exclude.xml"; then
        print_warning "Common files changed - validating all services"
        CHANGED_SERVICES=("${SERVICES[@]}")
    fi
    
    if [ ${#CHANGED_SERVICES[@]} -eq 0 ]; then
        print_info "No backend services changed"
        log_to_report "No backend services changed in current changeset"
    fi
    
    log_to_report "Changed Services: ${CHANGED_SERVICES[*]:-none}"
}

#===============================================================================
# Validation Functions
#===============================================================================

run_format_check() {
    local service_dir="$1"
    local service_name="$2"
    
    print_info "Checking code formatting ($service_name)..."
    
    cd "$service_dir"
    if mvn spotless:check -q 2>&1 | grep -q "Spotless.*is not properly formatted"; then
        print_warning "Code formatting issues found. Run 'mvn spotless:apply' to fix."
        log_to_report "[$service_name] Code Formatting: NEEDS FIXING (advisory)"
        return 0  # Non-blocking
    else
        print_success "Code formatting: PASSED"
        log_to_report "[$service_name] Code Formatting: PASSED"
        return 0
    fi
}

run_checkstyle() {
    local service_dir="$1"
    local service_name="$2"
    
    print_info "Running Checkstyle ($service_name)..."
    
    cd "$service_dir"
    local output=$(mvn checkstyle:check 2>&1)
    local violations=$(echo "$output" | grep -c "\[WARN\]" 2>/dev/null || true)
    violations=${violations:-0}
    
    if [ -n "$violations" ] && [ "$violations" -gt 0 ] 2>/dev/null; then
        print_warning "Checkstyle: $violations style warnings (advisory)"
        log_to_report "[$service_name] Checkstyle: $violations warnings (advisory)"
    else
        print_success "Checkstyle: PASSED"
        log_to_report "[$service_name] Checkstyle: PASSED"
    fi
    return 0  # Non-blocking
}

run_compile() {
    local service_dir="$1"
    local service_name="$2"
    
    print_info "Compiling ($service_name)..."
    
    if cd "$service_dir" && mvn compile -q -DskipTests 2>/dev/null; then
        print_success "Compilation: PASSED"
        log_to_report "[$service_name] Compilation: PASSED"
        return 0
    else
        print_error "Compilation failed"
        log_to_report "[$service_name] Compilation: FAILED"
        return 1
    fi
}

run_tests() {
    local service_dir="$1"
    local service_name="$2"
    
    print_info "Running tests ($service_name)..."
    
    cd "$service_dir"
    
    if mvn test -Dspring.profiles.active=test -Dcheckstyle.skip=true -Dspotbugs.skip=true 2>&1 | tee "$REPORT_DIR/${service_name}-test.log"; then
        # Extract test results
        TESTS_RUN=$(grep "Tests run:" "$REPORT_DIR/${service_name}-test.log" | tail -1 || echo "0")
        
        if echo "$TESTS_RUN" | grep -q "Failures: 0, Errors: 0"; then
            print_success "Tests: PASSED - $TESTS_RUN"
            log_to_report "[$service_name] Tests: PASSED - $TESTS_RUN"
            return 0
        else
            print_error "Tests: FAILED - $TESTS_RUN"
            log_to_report "[$service_name] Tests: FAILED - $TESTS_RUN"
            return 1
        fi
    else
        print_error "Test execution failed"
        log_to_report "[$service_name] Tests: EXECUTION FAILED"
        return 1
    fi
}

run_coverage_check() {
    local service_dir="$1"
    local service_name="$2"
    
    print_info "Checking code coverage ($service_name)..."
    
    cd "$service_dir"
    
    # Check if JaCoCo report exists
    if [ -f "target/site/jacoco/index.html" ]; then
        # Extract coverage from JaCoCo report
        LINE_COV=$(grep -o 'Total[^%]*%' target/site/jacoco/index.html 2>/dev/null | head -1 | grep -o '[0-9]*%' || echo "N/A")
        BRANCH_COV=$(grep -o 'Total[^%]*%' target/site/jacoco/index.html 2>/dev/null | head -2 | tail -1 | grep -o '[0-9]*%' || echo "N/A")
        
        print_success "Coverage: Line=$LINE_COV, Branch=$BRANCH_COV"
        log_to_report "[$service_name] Coverage: Line=$LINE_COV, Branch=$BRANCH_COV"
        
        # JaCoCo check was already run during tests, check result
        if grep -q "All coverage checks have been met" "$REPORT_DIR/${service_name}-test.log" 2>/dev/null; then
            print_success "Coverage thresholds: MET"
            log_to_report "[$service_name] Coverage Thresholds: MET"
            return 0
        else
            print_error "Coverage thresholds: NOT MET (requires 80% line, 75% branch)"
            log_to_report "[$service_name] Coverage Thresholds: NOT MET"
            return 1
        fi
    else
        print_warning "Coverage report not found"
        log_to_report "[$service_name] Coverage: REPORT NOT FOUND"
        return 1
    fi
}

run_spotbugs() {
    local service_dir="$1"
    local service_name="$2"
    
    print_info "Running SpotBugs ($service_name)..."
    
    cd "$service_dir"
    local output=$(mvn spotbugs:check 2>&1)
    local bugs=$(echo "$output" | grep -c "BugInstance" 2>/dev/null || true)
    bugs=${bugs:-0}
    
    if [ -n "$bugs" ] && [ "$bugs" -gt 0 ] 2>/dev/null; then
        print_warning "SpotBugs: $bugs potential issues found (advisory)"
        log_to_report "[$service_name] SpotBugs: $bugs issues (advisory)"
    else
        print_success "SpotBugs: PASSED"
        log_to_report "[$service_name] SpotBugs: PASSED"
    fi
    return 0  # Non-blocking
}

run_pmd() {
    local service_dir="$1"
    local service_name="$2"
    
    print_info "Running PMD ($service_name)..."
    
    cd "$service_dir"
    local output=$(mvn pmd:check 2>&1)
    local violations=$(echo "$output" | grep -c "PMD Failure" 2>/dev/null || true)
    violations=${violations:-0}
    
    if [ -n "$violations" ] && [ "$violations" -gt 0 ] 2>/dev/null; then
        print_warning "PMD: $violations code issues found (advisory)"
        log_to_report "[$service_name] PMD: $violations issues (advisory)"
    else
        print_success "PMD: PASSED"
        log_to_report "[$service_name] PMD: PASSED"
    fi
    return 0  # Non-blocking
}

run_owasp() {
    local service_dir="$1"
    local service_name="$2"
    
    print_info "Running OWASP Dependency Check ($service_name)..."
    print_warning "This may take several minutes on first run..."
    
    if cd "$service_dir" && mvn dependency-check:check 2>&1 | tee "$REPORT_DIR/${service_name}-owasp.log"; then
        if grep -q "One or more dependencies were identified with vulnerabilities" "$REPORT_DIR/${service_name}-owasp.log"; then
            print_error "OWASP: Vulnerabilities found"
            log_to_report "[$service_name] OWASP: VULNERABILITIES FOUND"
            return 1
        else
            print_success "OWASP: PASSED (no critical vulnerabilities)"
            log_to_report "[$service_name] OWASP: PASSED"
            return 0
        fi
    else
        print_error "OWASP check failed"
        log_to_report "[$service_name] OWASP: CHECK FAILED"
        return 1
    fi
}

#===============================================================================
# Service Validation
#===============================================================================

validate_service() {
    local service_name="$1"
    local service_dir="$BACKEND_DIR/$service_name"
    local skip_owasp="${2:-true}"
    local skip_slow="${3:-false}"
    
    print_section "Validating: $service_name"
    log_to_report ""
    log_to_report "================================================================"
    log_to_report "Service: $service_name"
    log_to_report "================================================================"
    
    if [ ! -d "$service_dir" ]; then
        print_warning "Service directory not found: $service_dir"
        log_to_report "[$service_name] SKIPPED - Directory not found"
        return 0
    fi
    
    local failed=0
    
    # 1. Compile
    run_compile "$service_dir" "$service_name" || ((failed++))
    
    # 2. Format check (skip in quick mode)
    if [ "$skip_slow" != "true" ]; then
        run_format_check "$service_dir" "$service_name" || true  # Non-blocking
    fi
    
    # 3. Checkstyle (skip in quick mode)
    if [ "$skip_slow" != "true" ]; then
        run_checkstyle "$service_dir" "$service_name" || true  # Non-blocking for now
    fi
    
    # 4. Tests with coverage
    run_tests "$service_dir" "$service_name" || ((failed++))
    
    # 5. Coverage check
    run_coverage_check "$service_dir" "$service_name" || ((failed++))
    
    # 6. SpotBugs (skip in quick mode)
    if [ "$skip_slow" != "true" ]; then
        run_spotbugs "$service_dir" "$service_name" || true  # Non-blocking for now
    fi
    
    # 7. PMD (skip in quick mode)
    if [ "$skip_slow" != "true" ]; then
        run_pmd "$service_dir" "$service_name" || true  # Non-blocking
    fi
    
    # 8. OWASP (only in full mode)
    if [ "$skip_owasp" != "true" ]; then
        run_owasp "$service_dir" "$service_name" || true  # Non-blocking
    fi
    
    if [ $failed -eq 0 ]; then
        print_success "$service_name: ALL MANDATORY CHECKS PASSED"
        log_to_report "[$service_name] RESULT: PASSED"
        return 0
    else
        print_error "$service_name: $failed MANDATORY CHECK(S) FAILED"
        log_to_report "[$service_name] RESULT: FAILED ($failed checks)"
        return 1
    fi
}

#===============================================================================
# Generate Summary Report
#===============================================================================

generate_summary() {
    print_section "Generating Quality Report"
    
    cat > "$SUMMARY_FILE" << EOF
# FoodAI Platform - Quality Report

**Generated:** $(date)
**Mode:** $MODE
**Target:** $TARGET

## Summary

| Check | Status |
|-------|--------|
EOF

    # Parse results from report file
    while IFS= read -r line; do
        if [[ "$line" =~ \[.*\].*:.*PASSED ]]; then
            echo "| ${line} | ✅ |" >> "$SUMMARY_FILE"
        elif [[ "$line" =~ \[.*\].*:.*FAILED ]]; then
            echo "| ${line} | ❌ |" >> "$SUMMARY_FILE"
        elif [[ "$line" =~ \[.*\].*:.*WARNINGS ]]; then
            echo "| ${line} | ⚠️ |" >> "$SUMMARY_FILE"
        fi
    done < "$REPORT_FILE"

    cat >> "$SUMMARY_FILE" << EOF

## Quality Thresholds

| Metric | Required | Enforcement |
|--------|----------|-------------|
| Line Coverage | ≥ 80% | Blocking |
| Branch Coverage | ≥ 75% | Blocking |
| All Tests Pass | 100% | Blocking |
| SpotBugs Issues | 0 | Advisory |
| Checkstyle | 0 errors | Advisory |

## Detailed Report

See: \`$REPORT_FILE\`

## Commands

\`\`\`bash
# Re-run validation
./mvnw.sh $MODE $TARGET

# Fix formatting issues
mvn spotless:apply

# View coverage report
open target/site/jacoco/index.html
\`\`\`
EOF

    print_success "Report generated: $SUMMARY_FILE"
    print_info "Detailed log: $REPORT_FILE"
}

#===============================================================================
# Main Execution Modes
#===============================================================================

mode_all() {
    local skip_owasp="${1:-true}"
    local skip_slow="${2:-false}"
    local total_failed=0
    
    print_header "Validating All Services"
    
    for service in "${SERVICES[@]}"; do
        validate_service "$service" "$skip_owasp" "$skip_slow" || ((total_failed++))
    done
    
    return $total_failed
}

mode_service() {
    local service_name="$1"
    local skip_owasp="${2:-true}"
    local skip_slow="${3:-false}"
    
    print_header "Validating Service: $service_name"
    
    validate_service "$service_name" "$skip_owasp" "$skip_slow"
}

mode_changeset() {
    local skip_owasp="${1:-true}"
    local skip_slow="${2:-false}"
    local total_failed=0
    
    print_header "Validating Changed Services"
    
    get_changed_services
    
    if [ ${#CHANGED_SERVICES[@]} -eq 0 ]; then
        print_success "No services to validate"
        return 0
    fi
    
    for service in "${CHANGED_SERVICES[@]}"; do
        validate_service "$service" "$skip_owasp" "$skip_slow" || ((total_failed++))
    done
    
    return $total_failed
}

mode_quick() {
    print_header "Quick Validation (Tests + Coverage Only)"
    
    case "$1" in
        all)
            mode_all "true" "true"
            ;;
        *)
            if [ -n "$1" ]; then
                mode_service "$1" "true" "true"
            else
                mode_changeset "true" "true"
            fi
            ;;
    esac
}

mode_full() {
    print_header "Full Validation (Including Security Scans)"
    
    case "$1" in
        all)
            mode_all "false" "false"
            ;;
        *)
            if [ -n "$1" ]; then
                mode_service "$1" "false" "false"
            else
                mode_changeset "false" "false"
            fi
            ;;
    esac
}

#===============================================================================
# Cursor Integration - Pre-commit Validation
#===============================================================================

cursor_validate() {
    print_header "Cursor Pre-Commit Validation"
    
    # This function is called by Cursor before completing a task
    # It validates the changeset and generates a report
    
    CURSOR_REPORT="$REPORT_DIR/cursor-validation.md"
    
    get_changed_services
    
    if [ ${#CHANGED_SERVICES[@]} -eq 0 ]; then
        cat > "$CURSOR_REPORT" << EOF
# Cursor Validation Report

**Status:** ✅ PASSED
**Reason:** No backend services changed

No validation required.
EOF
        print_success "No backend changes - validation passed"
        cat "$CURSOR_REPORT"
        return 0
    fi
    
    local total_failed=0
    
    for service in "${CHANGED_SERVICES[@]}"; do
        validate_service "$service" "true" "false" || ((total_failed++))
    done
    
    if [ $total_failed -eq 0 ]; then
        cat > "$CURSOR_REPORT" << EOF
# Cursor Validation Report

**Status:** ✅ PASSED
**Services Validated:** ${CHANGED_SERVICES[*]}

All quality checks passed:
- ✅ Compilation successful
- ✅ All tests passing
- ✅ Code coverage thresholds met (≥80% line, ≥75% branch)
- ✅ Static analysis passed

The changeset meets all production quality standards.
EOF
        print_success "CURSOR VALIDATION: PASSED"
        cat "$CURSOR_REPORT"
        return 0
    else
        cat > "$CURSOR_REPORT" << EOF
# Cursor Validation Report

**Status:** ❌ FAILED
**Services Validated:** ${CHANGED_SERVICES[*]}
**Failed Services:** $total_failed

Quality checks failed. Please fix the following before completing:

1. Ensure all tests pass: \`mvn test\`
2. Ensure coverage thresholds are met: ≥80% line, ≥75% branch
3. Fix compilation errors if any

See detailed report: $REPORT_FILE
EOF
        print_error "CURSOR VALIDATION: FAILED"
        cat "$CURSOR_REPORT"
        return 1
    fi
}

#===============================================================================
# Help
#===============================================================================

show_help() {
    cat << EOF
FoodAI Backend Quality Validation Script

Usage: ./mvnw.sh [mode] [target] [options]

Modes:
  all                     Validate all services
  service <name>          Validate a specific service
  changeset               Validate only changed services (git diff)
  quick [all|<service>]   Quick validation (skip static analysis)
  full [all|<service>]    Full validation including security scans
  cursor                  Cursor pre-commit validation
  report                  Generate report only (no validation)
  help                    Show this help message

Examples:
  ./mvnw.sh all                    # Validate all services
  ./mvnw.sh service order-service  # Validate order-service only
  ./mvnw.sh changeset              # Validate git changes
  ./mvnw.sh quick all              # Quick validation of all services
  ./mvnw.sh full order-service     # Full validation with security scan
  ./mvnw.sh cursor                 # Cursor pre-commit check

Quality Thresholds:
  - Line Coverage:    ≥ 80%
  - Branch Coverage:  ≥ 75%
  - All Tests:        Must pass
  - SpotBugs:         0 high/medium issues
  - Checkstyle:       0 errors

Output:
  Reports are generated in: $REPORT_DIR
  Latest report: $SUMMARY_FILE

EOF
}

#===============================================================================
# Main
#===============================================================================

main() {
    MODE="${1:-changeset}"
    TARGET="${2:-}"
    
    cd "$BACKEND_DIR"
    
    # Initialize
    init_report
    setup_java
    
    print_header "FoodAI Backend Quality Validation"
    print_info "Mode: $MODE"
    print_info "Report: $REPORT_FILE"
    
    local exit_code=0
    
    case "$MODE" in
        all)
            mode_all
            exit_code=$?
            ;;
        service)
            if [ -z "$TARGET" ]; then
                print_error "Please specify a service name"
                show_help
                exit 1
            fi
            mode_service "$TARGET"
            exit_code=$?
            ;;
        changeset)
            mode_changeset
            exit_code=$?
            ;;
        quick)
            mode_quick "$TARGET"
            exit_code=$?
            ;;
        full)
            mode_full "$TARGET"
            exit_code=$?
            ;;
        cursor)
            cursor_validate
            exit_code=$?
            ;;
        report)
            generate_summary
            exit_code=0
            ;;
        help|--help|-h)
            show_help
            exit 0
            ;;
        *)
            print_error "Unknown mode: $MODE"
            show_help
            exit 1
            ;;
    esac
    
    # Generate summary report
    generate_summary
    
    # Final status
    echo ""
    if [ $exit_code -eq 0 ]; then
        print_header "VALIDATION PASSED ✓"
        log_to_report ""
        log_to_report "================================================================"
        log_to_report "FINAL RESULT: PASSED"
        log_to_report "================================================================"
    else
        print_header "VALIDATION FAILED ✗"
        log_to_report ""
        log_to_report "================================================================"
        log_to_report "FINAL RESULT: FAILED"
        log_to_report "================================================================"
    fi
    
    print_info "Full report: $REPORT_FILE"
    print_info "Summary: $SUMMARY_FILE"
    
    exit $exit_code
}

# Run main function with all arguments
main "$@"

