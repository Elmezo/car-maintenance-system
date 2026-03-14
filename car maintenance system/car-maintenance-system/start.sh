#!/bin/bash

# ==========================================
# Smart Car Maintenance Analytics System
# Quick Start Script
# ==========================================

echo "🚗 Starting Smart Car Maintenance Analytics System..."

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check if MySQL is running
echo -e "${BLUE}Checking MySQL...${NC}"
if ! command -v mysql &> /dev/null; then
    echo "MySQL not found. Please install MySQL first."
    exit 1
fi

# Setup Database
echo -e "${GREEN}Setting up database...${NC}"
mysql -u root -p < database/schema.sql

# Start Backend
echo -e "${GREEN}Starting Spring Boot Backend...${NC}"
cd backend
mvn spring-boot:run &
BACKEND_PID=$!
cd ..

# Wait for backend to start
sleep 10

# Start Analytics Service
echo -e "${GREEN}Starting Python Analytics Service...${NC}"
cd analytics
source venv/bin/activate 2>/dev/null || python -m venv venv && source venv/bin/activate
pip install -r requirements.txt
python app.py &
ANALYTICS_PID=$!
cd ..

echo ""
echo -e "${GREEN}✅ System started successfully!${NC}"
echo ""
echo "📊 Dashboard: http://localhost:3000"
echo "🔧 Backend API: http://localhost:8080/api"
echo "📖 API Docs: http://localhost:8080/api/swagger-ui.html"
echo "🐍 Analytics: http://localhost:5000"
echo ""
echo "Press Ctrl+C to stop all services..."

# Wait for interrupt
trap "kill $BACKEND_PID $ANALYTICS_PID 2>/dev/null; exit 0" INT TERM
wait
