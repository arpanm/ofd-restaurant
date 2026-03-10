#!/bin/bash

# Script to load mock restaurant data via REST API
# Usage: ./scripts/load-mock-data.sh

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Restaurant Service - Mock Data Loader${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Check if application is running
echo -e "${BLUE}[1/2] Checking if application is running...${NC}"
if ! curl -s "http://localhost:8081/api/v1/restaurants?page=0&size=1" > /dev/null 2>&1; then
    echo -e "${RED}ERROR: Application is not running on port 8081${NC}"
    echo -e "${YELLOW}Please start the application first:${NC}"
    echo -e "  ${YELLOW}./mvnw.sh run dev${NC}"
    echo -e "  ${YELLOW}OR${NC}"
    echo -e "  ${YELLOW}mvn spring-boot:run -Dspring-boot.run.profiles=dev${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Application is running${NC}"
echo ""

# Load mock data via API
echo -e "${BLUE}[2/2] Loading mock data via REST API...${NC}"
echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
"$SCRIPT_DIR/load-via-api-v2.sh"

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Mock Data Loaded Successfully!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${BLUE}You can now:${NC}"
echo -e "  1. Test GET API: ${YELLOW}curl 'http://localhost:8081/api/v1/restaurants?page=0&size=10'${NC}"
echo -e "  2. View Swagger UI: ${YELLOW}http://localhost:8081/swagger-ui.html${NC}"
echo -e "  3. Test specific restaurant: ${YELLOW}curl 'http://localhost:8081/api/v1/restaurants/{id}'${NC}"
echo ""
echo -e "${BLUE}Available Restaurants:${NC}"
echo -e "  • Bella Italia (Italian, Mediterranean)"
echo -e "  • Sattvik Bhavan (Indian, North Indian, South Indian)"
echo -e "  • Dragon Wok (Chinese, Asian, Thai)"
echo ""

