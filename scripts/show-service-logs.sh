#!/usr/bin/env bash
# Show logs for failing services (run after: docker compose up -d)
# Usage: ./scripts/show-service-logs.sh [service-name]
# Example: ./scripts/show-service-logs.sh order-service

set -e
SERVICE="${1:-}"
if [ -z "$SERVICE" ]; then
  echo "Usage: $0 <service-name>"
  echo "Example: $0 order-service"
  echo "Example: $0 promotion-service"
  echo ""
  echo "Run after: docker compose up -d"
  exit 1
fi
docker compose logs --tail=200 "$SERVICE"
