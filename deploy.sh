# Deployment Scripts
# deploy.sh
#!/bin/bash

# Deployment script for B2B Workflow Platform

set -e

echo "Starting deployment of B2B Workflow Platform..."

# Configuration
ENVIRONMENT=${1:-development}
DOCKER_COMPOSE_FILE="docker-compose.yml"

if [ "$ENVIRONMENT" = "production" ]; then
    DOCKER_COMPOSE_FILE="docker-compose.prod.yml"
fi

echo "Deploying to environment: $ENVIRONMENT"

# Build applications
echo "Building backend application..."
cd backend
mvn clean package -DskipTests
cd ..

echo "Building frontend application..."
cd frontend
npm ci
npm run build
cd ..

# Stop existing containers
echo "Stopping existing containers..."
docker-compose -f $DOCKER_COMPOSE_FILE down

# Start new containers
echo "Starting new containers..."
docker-compose -f $DOCKER_COMPOSE_FILE up -d

# Wait for services to be ready
echo "Waiting for services to be ready..."
sleep 30

# Health check
echo "Performing health checks..."
curl -f http://localhost:8081/actuator/health || exit 1
curl -f http://localhost:3000 || exit 1

echo "Deployment completed successfully!"

# Show running services
docker-compose -f $DOCKER_COMPOSE_FILE ps