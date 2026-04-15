#!/bin/bash
# XReport Docker Deploy Script
# Usage: ./deploy.sh

set -e

echo "========================================="
echo "  XReport Deployment Script"
echo "========================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "ERROR: docker-compose is not installed. Please install docker-compose first."
    exit 1
fi

echo "[1/5] Building Docker images..."
docker-compose build

echo "[2/5] Starting services..."
docker-compose up -d

echo "[3/5] Waiting for MySQL to be healthy..."
for i in {1..30}; do
    if docker-compose exec mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
        echo "MySQL is ready!"
        break
    fi
    echo "Waiting for MySQL... ($i/30)"
    sleep 2
done

echo "[4/5] Waiting for XReport to start..."
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health &>/dev/null || curl -s http://localhost:8080/api/auth/health &>/dev/null; then
        echo "XReport is ready!"
        break
    fi
    echo "Waiting for XReport... ($i/30)"
    sleep 2
done

echo "[5/5] Checking service status..."
docker-compose ps

echo ""
echo "========================================="
echo "  Deployment Complete!"
echo "========================================="
echo "XReport: http://localhost:8080"
echo "MySQL:   localhost:3306"
echo ""
echo "View logs: docker-compose logs -f"
echo "Stop services: docker-compose down"
echo "========================================="