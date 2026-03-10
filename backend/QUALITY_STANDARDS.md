# FoodAI Platform - Quality Standards & Enforcement

## Overview

This document describes the quality gates, tools, and enforcement mechanisms for the FoodAI platform backend. All code MUST pass these quality gates before being considered production-ready.

## Quality Gates Summary

| Gate | Tool | Threshold | Enforcement |
|------|------|-----------|-------------|
| Code Coverage (Line) | JaCoCo | ≥ 80% | Build fails |
| Code Coverage (Branch) | JaCoCo | ≥ 75% | Build fails |
| All Tests Pass | Surefire/Failsafe | 100% | Build fails |
| Static Bug Detection | SpotBugs | 0 High/Medium | Build fails |
| Security Vulnerabilities | OWASP | 0 (CVSS ≥ 7) | Build fails |
| Code Style | Checkstyle | 0 errors | Build fails |
| Code Formatting | Spotless | Enforced | Build fails |
| Code Smells | SonarQube | < 10 | Pipeline fails |
| Technical Debt | SonarQube | < 5% | Pipeline fails |
| Duplicated Lines | SonarQube | < 3% | Pipeline fails |

## Tools & Configuration

### 1. JaCoCo - Code Coverage

**Purpose:** Measure test coverage to ensure adequate testing.

**Configuration:** `pom.xml`
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.13</version>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.75</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

**Commands:**
```bash
# Run tests with coverage
mvn clean test

# Generate coverage report
mvn jacoco:report

# View report at: target/site/jacoco/index.html
```

### 2. SpotBugs - Static Bug Detection

**Purpose:** Find potential bugs, security issues, and bad practices.

**Configuration:** 
- Plugin: `spotbugs-maven-plugin` v4.8.3.0
- Exclusions: `spotbugs-exclude.xml`
- Security Plugin: FindSecBugs

**Commands:**
```bash
# Run SpotBugs analysis
mvn spotbugs:check

# Generate report
mvn spotbugs:spotbugs

# View report at: target/spotbugsXml.xml
```

### 3. PMD - Code Analysis

**Purpose:** Detect common programming flaws.

**Configuration:** `pom.xml`
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <configuration>
        <rulesets>
            <ruleset>/category/java/bestpractices.xml</ruleset>
            <ruleset>/category/java/errorprone.xml</ruleset>
            <ruleset>/category/java/security.xml</ruleset>
        </rulesets>
    </configuration>
</plugin>
```

**Commands:**
```bash
# Run PMD analysis
mvn pmd:check

# View report at: target/pmd.xml
```

### 4. OWASP Dependency Check

**Purpose:** Identify known vulnerabilities in dependencies.

**Configuration:**
- Plugin: `dependency-check-maven` v9.0.9
- Fail threshold: CVSS ≥ 7
- Suppressions: `owasp-suppressions.xml`

**Commands:**
```bash
# Run vulnerability scan (takes ~10 minutes first time)
mvn dependency-check:check

# View report at: target/dependency-check-report.html
```

### 5. Checkstyle - Code Style

**Purpose:** Enforce consistent code style.

**Configuration:** `checkstyle.xml` (based on Google Java Style with customizations)

**Key Rules:**
- Max line length: 120 characters
- Max file length: 2000 lines
- Cyclomatic complexity: < 15 per method
- No star imports
- Proper naming conventions

**Commands:**
```bash
# Check code style
mvn checkstyle:check

# View report at: target/checkstyle-result.xml
```

### 6. Spotless - Code Formatting

**Purpose:** Ensure consistent code formatting.

**Configuration:** Google Java Format style

**Commands:**
```bash
# Check formatting
mvn spotless:check

# Auto-fix formatting
mvn spotless:apply
```

### 7. SonarQube - Continuous Inspection

**Purpose:** Comprehensive code quality analysis.

**Quality Profile Settings:**
- Code Smells: < 10
- Bugs: 0
- Vulnerabilities: 0 Critical/High
- Technical Debt: < 5%
- Duplicated Lines: < 3%
- Coverage: ≥ 80%

**Commands:**
```bash
# Run SonarQube analysis (requires SonarQube server)
mvn sonar:sonar \
    -Dsonar.projectKey=foodai-{service} \
    -Dsonar.host.url=http://localhost:9000 \
    -Dsonar.login=your-token
```

### 8. ArchUnit - Architecture Tests

**Purpose:** Enforce architectural constraints.

**Tests Include:**
- Layer dependencies (controller → service → domain → repository)
- Naming conventions
- Package structure
- Annotation usage

**Example Test:**
```java
@ArchTest
static final ArchRule controllers_should_not_access_repositories =
    noClasses()
        .that().resideInAPackage("..controller..")
        .should().accessClassesThat()
        .resideInAPackage("..repository..");
```

## Running All Quality Checks

### Quick Check (Development)
```bash
# Format code first
mvn spotless:apply

# Run tests with coverage
mvn clean verify
```

### Full Check (Pre-commit)
```bash
# All checks including static analysis
mvn clean verify spotbugs:check pmd:check
```

### Complete Scan (CI/CD)
```bash
# Include OWASP scan (slow)
mvn clean verify spotbugs:check pmd:check dependency-check:check
```

## Per-Service Coverage Requirements

| Layer | Line Coverage | Branch Coverage |
|-------|---------------|-----------------|
| Domain Model | ≥ 90% | ≥ 85% |
| Services | ≥ 85% | ≥ 80% |
| Controllers | ≥ 80% | ≥ 75% |
| Mappers | ≥ 80% | ≥ 75% |
| Repository | ≥ 70% | ≥ 65% |

## Suppressing False Positives

### SpotBugs
Use `spotbugs-exclude.xml` for pattern-based exclusions:
```xml
<Match>
    <Bug pattern="EI_EXPOSE_REP"/>
    <Class name="~.*Response"/>
</Match>
```

### OWASP
Use `owasp-suppressions.xml` for CVE suppressions:
```xml
<suppress>
    <notes>Test dependency - not in production</notes>
    <packageUrl regex="true">^pkg:maven/com\.h2database/h2@.*$</packageUrl>
    <vulnerabilityName regex="true">.*</vulnerabilityName>
</suppress>
```

### Checkstyle
Use `@SuppressWarnings("checkstyle:...")` annotation:
```java
@SuppressWarnings("checkstyle:MagicNumber")
public void calculate() { ... }
```

### PMD
Use `@SuppressWarnings("PMD.MethodName")` annotation.

## CI/CD Integration

### GitHub Actions / Jenkins Pipeline Stages:

1. **validate** - Checkstyle, Spotless check
2. **compile** - Maven compile
3. **unit-test** - Unit tests with coverage
4. **static-analysis** - SpotBugs, PMD
5. **security-scan** - OWASP Dependency Check
6. **integration-test** - Integration tests
7. **sonar** - SonarQube analysis
8. **package** - Build JAR/Docker image

### Branch Protection Rules:
- Require 2 PR approvals
- All status checks must pass
- No force push to main/develop

## Temporary Threshold Reduction

During initial development, services may temporarily reduce thresholds. This MUST be tracked:

1. Add TODO comment in pom.xml:
```xml
<!-- TODO: Increase to 0.80/0.75 - JIRA-123 -->
<minimum>0.50</minimum>
```

2. Create tracking ticket
3. Plan sprint to reach target coverage
4. Maximum temporary period: 2 sprints

## Monitoring & Reporting

- JaCoCo reports: `target/site/jacoco/index.html`
- SpotBugs reports: `target/spotbugsXml.xml`
- PMD reports: `target/pmd.xml`
- OWASP reports: `target/dependency-check-report.html`
- Surefire reports: `target/surefire-reports/`

## Contact

For questions about quality standards, contact:
- Technical Lead: [TBD]
- Quality Engineering: [TBD]

