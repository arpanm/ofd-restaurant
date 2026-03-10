#!/bin/bash

# Service Management Script for Restaurant Service Tests
# Usage: ./scripts/manage-services.sh [start|stop|status|restart]

set -e

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

function print_status() {
    echo -e "${GREEN}=== Service Status ===${NC}"
    brew services list | grep -E "mongodb|kafka" || echo "No services found"
}

function start_services() {
    echo -e "${GREEN}Starting MongoDB...${NC}"
    brew services start mongodb-community@7.0
    
    echo -e "${GREEN}Starting Kafka...${NC}"
    brew services start kafka
    
    echo -e "${GREEN}Waiting for services to start...${NC}"
    sleep 3
    
    print_status
    echo -e "${GREEN}✓ All services started!${NC}"
}

function stop_services() {
    echo -e "${YELLOW}Stopping MongoDB...${NC}"
    brew services stop mongodb-community@7.0
    
    echo -e "${YELLOW}Stopping Kafka...${NC}"
    brew services stop kafka
    
    print_status
    echo -e "${GREEN}✓ All services stopped!${NC}"
}

function restart_services() {
    echo -e "${YELLOW}Restarting services...${NC}"
    stop_services
    sleep 2
    start_services
}

function check_java() {
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" != "21" ]; then
        echo -e "${RED}Warning: Java version is $JAVA_VERSION, but Java 21 is required.${NC}"
        echo -e "${YELLOW}Run: export JAVA_HOME=\$(/usr/libexec/java_home -v 21)${NC}"
    else
        echo -e "${GREEN}✓ Java 21 is configured${NC}"
    fi
}

# Main script
case "$1" in
    start)
        start_services
        check_java
        ;;
    stop)
        stop_services
        ;;
    status)
        print_status
        check_java
        ;;
    restart)
        restart_services
        check_java
        ;;
    *)
        echo "Usage: $0 {start|stop|status|restart}"
        echo ""
        echo "Commands:"
        echo "  start   - Start MongoDB and Kafka services"
        echo "  stop    - Stop MongoDB and Kafka services"
        echo "  status  - Check status of services"
        echo "  restart - Restart all services"
        echo ""
        print_status
        exit 1
        ;;
esac

exit 0

