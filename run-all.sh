#!/usr/bin/env bash
# Top-level script to run all OFD Restaurant services + frontend.
# Usage:
#   ./run-all.sh docker       - Run full stack in Docker (built images, dev profile)
#   ./run-all.sh docker-bg    - Same, detached
#   ./run-all.sh docker-dev   - Docker with hot-reload: volume-mounted source, frontend HMR
#   ./run-all.sh docker-prod  - Docker with prod profile (set CORS_ALLOWED_ORIGINS etc. for prod)
#   ./run-all.sh local        - Infra in Docker; backend + frontend on host (Maven + npm)
#   ./run-all.sh stop         - Stop Docker stack

set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

COMPOSE_BASE="docker compose -f docker-compose.yml"
COMPOSE_DEV="docker compose -f docker-compose.yml -f docker-compose.dev.yml"
COMPOSE_PROD="docker compose -f docker-compose.yml -f docker-compose.prod.yml"

run_docker() {
  echo "Starting full stack with Docker Compose (dev profile)..."
  $COMPOSE_BASE up "$@"
}

run_docker_bg() {
  echo "Starting full stack in background..."
  $COMPOSE_BASE up -d
  echo "Stack is up. Frontend: http://localhost:3000  Gateway: http://localhost:8085"
  echo "Run './run-all.sh stop' to stop."
}

run_docker_dev() {
  echo "Starting stack in dev mode (volume mounts, frontend HMR, backend restarts pick up changes)..."
  $COMPOSE_DEV up --build "$@"
}

run_docker_dev_bg() {
  echo "Starting stack in dev mode (background)..."
  $COMPOSE_DEV up -d --build
  echo "Dev stack is up. Frontend (Vite): http://localhost:3001  Gateway: http://localhost:8085"
  echo "Restart a service to pick up backend changes: docker compose -f docker-compose.yml -f docker-compose.dev.yml restart <service>"
  echo "Run './run-all.sh stop' to stop."
}

run_docker_prod() {
  echo "Starting stack with prod profile..."
  $COMPOSE_PROD up "$@"
}

run_local() {
  echo "Starting infrastructure (MongoDB, Postgres, Redis, Zookeeper, Kafka)..."
  $COMPOSE_BASE up -d mongodb postgres redis zookeeper kafka
  echo "Waiting for infra to be ready..."
  sleep 15

  echo "Starting backend services with Maven (in background)..."
  cd backend
  mvn -f user-service/pom.xml spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev" &
  PID_USER=$!
  mvn -f restaurant-service/pom.xml spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev" &
  PID_REST=$!
  mvn -f menu-service/pom.xml spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev" &
  PID_MENU=$!
  mvn -f order-service/pom.xml spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev" &
  PID_ORDER=$!
  mvn -f promotion-service/pom.xml spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev" &
  PID_PROMO=$!
  echo "Waiting for backend services to start..."
  sleep 45
  mvn -f orchestration-service/pom.xml spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev" &
  PID_ORCH=$!
  cd ..
  echo "Backend PIDs: user=$PID_USER restaurant=$PID_REST menu=$PID_MENU order=$PID_ORDER promotion=$PID_PROMO orchestration=$PID_ORCH"
  echo "Starting frontend (Ctrl+C to stop all)..."
  trap "kill $PID_USER $PID_REST $PID_MENU $PID_ORDER $PID_PROMO $PID_ORCH 2>/dev/null; exit" INT TERM
  cd ofd-restaurant-app && npm run dev
}

stop_all() {
  echo "Stopping Docker stack..."
  $COMPOSE_BASE down
  echo "Done."
}

case "${1:-docker}" in
  docker)        run_docker ;;
  docker-bg)     run_docker_bg ;;
  docker-dev)    run_docker_dev ;;
  docker-dev-bg) run_docker_dev_bg ;;
  docker-prod)   run_docker_prod ;;
  local)         run_local ;;
  stop)          stop_all ;;
  *)
    echo "Usage: $0 {docker|docker-bg|docker-dev|docker-dev-bg|docker-prod|local|stop}"
    echo "  docker        - Run full stack in Docker (foreground, dev profile)"
    echo "  docker-bg     - Run full stack in Docker (detached)"
    echo "  docker-dev    - Docker with hot-reload: volume mounts, frontend HMR (restart backend containers for code changes)"
    echo "  docker-dev-bg - Same as docker-dev, detached"
    echo "  docker-prod   - Docker with prod profile (application-prod.yml, set CORS_ALLOWED_ORIGINS for gateway)"
    echo "  local         - Infra in Docker; backend (Maven) + frontend (npm) on host"
    echo "  stop          - Stop Docker stack"
    exit 1
    ;;
esac
