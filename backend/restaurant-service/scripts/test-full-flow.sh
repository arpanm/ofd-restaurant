#!/bin/bash

# Complete Test Flow: Clean DB, Start Server, Load Data via API, Test

set -e

echo "========================================="
echo "Restaurant Service - Complete Test Flow"
echo "========================================="
echo ""

# Step 1: Clean MongoDB
echo "Step 1: Cleaning MongoDB..."
mongosh foodai_restaurant_dev --eval "db.restaurants.deleteMany({}); print('✅ Cleaned: ' + db.restaurants.countDocuments() + ' documents remaining')" --quiet
echo ""

# Step 2: Kill existing processes
echo "Step 2: Killing existing processes..."
pkill -9 -f "spring-boot:run" 2>/dev/null || true
pkill -9 java 2>/dev/null || true
sleep 2
echo "✅ Processes killed"
echo ""

# Step 3: Start the application
echo "Step 3: Starting application..."
cd /Users/arpan1.mukherjee/code/agentic-plate/backend/restaurant-service
nohup mvn spring-boot:run -Dspring-boot.run.profiles=dev > /tmp/restaurant-test.log 2>&1 &
APP_PID=$!
echo "   Application starting (PID: $APP_PID)"
echo "   Logs: /tmp/restaurant-test.log"
echo ""

# Step 4: Wait for application to be ready
echo "Step 4: Waiting for application to start (this takes ~45 seconds)..."
MAX_WAIT=60
WAITED=0
while [ $WAITED -lt $MAX_WAIT ]; do
  if curl -s "http://localhost:8081/api/v1/restaurants?page=0&size=1" > /dev/null 2>&1; then
    echo "✅ Application is ready! (waited ${WAITED}s)"
    break
  fi
  sleep 5
  WAITED=$((WAITED + 5))
  echo "   Still waiting... (${WAITED}s/${MAX_WAIT}s)"
done

if [ $WAITED -ge $MAX_WAIT ]; then
  echo "❌ Application failed to start within ${MAX_WAIT} seconds"
  echo "   Check logs: tail -100 /tmp/restaurant-test.log"
  exit 1
fi
echo ""

# Step 5: Load mock data via API
echo "Step 5: Loading mock data via API..."
./scripts/load-via-api-v2.sh
echo ""

# Step 6: Final test
echo "Step 6: Testing with curl..."
curl -s 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | jq -r 'if .content then "✅ SUCCESS! API is working. Found " + (.totalElements | tostring) + " restaurants:\n" + (.content | map("  • " + .name) | join("\n")) else "❌ Error: " + (.message // .error // "Unknown error") end'
echo ""

echo "========================================="
echo "✅ Complete Test Flow Finished!"
echo "========================================="
echo ""
echo "Application is running on http://localhost:8081"
echo "Swagger UI: http://localhost:8081/swagger-ui.html"
echo "Logs: tail -f /tmp/restaurant-test.log"
echo ""

