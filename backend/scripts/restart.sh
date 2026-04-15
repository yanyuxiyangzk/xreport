#!/bin/bash
# XReport Restart Script
# Usage: ./restart.sh

set -e

echo "========================================="
echo "  XReport Restart Script"
echo "========================================="

echo "[1/4] Stopping services..."
docker-compose down

echo "[2/4] Removing old containers..."
docker-compose rm -f

echo "[3/4] Starting services..."
docker-compose up -d

echo "[4/4] Waiting for services to be ready..."
echo "Waiting for MySQL..."
for i in {1..30}; do
    if docker-compose exec mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
        echo "MySQL is ready!"
        break
    fi
    sleep 2
done

echo "Waiting for XReport..."
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health &>/dev/null || curl -s http://localhost:8080/api/auth/health &>/dev/null; then
        echo "XReport is ready!"
        break
    fi
    sleep 2
done

echo ""
echo "========================================="
echo "  Restart Complete!"
echo "========================================="
echo "View logs: docker-compose logs -f"
echo "========================================="