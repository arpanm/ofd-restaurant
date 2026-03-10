# FoodAI Platform - Quality Report

**Generated:** Mon Dec 15 18:49:05 IST 2025
**Mode:** service
**Target:** order-service

## Summary

| Check | Status |
|-------|--------|
| [order-service] Compilation: PASSED | ✅ |
| [order-service] Code Formatting: PASSED | ✅ |
| [order-service] Tests: PASSED - [INFO] Tests run: 160, Failures: 0, Errors: 0, Skipped: 0 | ✅ |
| [order-service] SpotBugs: PASSED | ✅ |
| [order-service] PMD: PASSED | ✅ |
| [order-service] RESULT: PASSED | ✅ |

## Quality Thresholds

| Metric | Required | Enforcement |
|--------|----------|-------------|
| Line Coverage | ≥ 80% | Blocking |
| Branch Coverage | ≥ 75% | Blocking |
| All Tests Pass | 100% | Blocking |
| SpotBugs Issues | 0 | Advisory |
| Checkstyle | 0 errors | Advisory |

## Detailed Report

See: `/Users/arpan1.mukherjee/code/agentic-plate/backend/target/quality-reports/quality-report-20251215-184829.txt`

## Commands

```bash
# Re-run validation
./mvnw.sh service order-service

# Fix formatting issues
mvn spotless:apply

# View coverage report
open target/site/jacoco/index.html
```
