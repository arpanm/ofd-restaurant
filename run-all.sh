#!/usr/bin/env bash
# Top-level script to run all OFD Restaurant services + frontend.
# Usage:
#   ./run-all.sh docker     - Run everything in Docker (default)
#   ./run-all.sh docker-bg  - Run in Docker, detached
#   ./run-all.sh local      - Run infra in Docker, backend + frontend locally (Maven + npm)
#   ./run-all.sh stop       - Stop Docker stack (and local processes if run with 'local')

set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

run_docker() {
  echo "Starting full stack with Docker Compose..."
  docker compose up "$@"
}

run_docker_bg() {
  echo "Starting full stack in background..."
  docker compose up -d
  echo "Stack is up. Frontend: http://localhost:3000  Gateway: http://localhost:8085"
  echo "Run './run-all.sh stop' to stop."
}

run_local() {
  echo "Starting infrastructure (MongoDB, Postgres, Redis, Zookeeper, Kafka)..."
  docker compose up -d mongodb postgres redis zookeeper kafka
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
  docker compose down
  echo "Done."
}

case "${1:-docker}" in
  docker)    run_docker ;;
  docker-bg) run_docker_bg ;;
  local)     run_local ;;
  stop)      stop_all ;;
  *)
    echo "Usage: $0 {docker|docker-bg|local|stop}"
    echo "  docker     - Run full stack in Docker (foreground)"
    echo "  docker-bg  - Run full stack in Docker (detached)"
    echo "  local      - Infra in Docker; backend (Maven) + frontend (npm) locally"
    echo "  stop       - Stop Docker stack"
    exit 1
    ;;
esac
