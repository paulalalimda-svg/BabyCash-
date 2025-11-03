#!/bin/bash
# BabyCash Backend Quick Start Script

echo "🚀 Starting BabyCash Backend..."

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if PostgreSQL is running
echo -e "${BLUE}Checking PostgreSQL...${NC}"
if docker ps | grep -q babycash-postgres; then
    echo -e "${GREEN}✅ PostgreSQL is running${NC}"
else
    echo -e "${RED}❌ PostgreSQL not running. Starting it...${NC}"
    docker run -d \
      --name babycash-postgres \
      -e POSTGRES_DB=babycash \
      -e POSTGRES_USER=postgres \
      -e POSTGRES_PASSWORD=postgres \
      -p 5432:5432 \
      -v $(pwd)/database_schema.sql:/docker-entrypoint-initdb.d/01-schema.sql \
      postgres:15-alpine
    
    echo "Waiting for PostgreSQL to initialize (10 seconds)..."
    sleep 10
    echo -e "${GREEN}✅ PostgreSQL started${NC}"
fi

# Run the application
echo -e "${BLUE}Starting Spring Boot application...${NC}"
./mvnw spring-boot:run

echo -e "${GREEN}🎉 BabyCash Backend is running!${NC}"
echo -e "${BLUE}📚 Swagger UI: http://localhost:8080/swagger-ui.html${NC}"
echo -e "${BLUE}🔍 API Docs: http://localhost:8080/api-docs${NC}"
echo -e "${BLUE}💚 Health Check: http://localhost:8080/api/health${NC}"
