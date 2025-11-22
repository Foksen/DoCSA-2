#!/bin/bash

set -e

APP_DIR="/opt/shop-app"

echo "Updating application..."

cd $APP_DIR
git fetch origin
git reset --hard origin/master

cd $APP_DIR/Pr8/deployment

echo "Stopping services..."
docker-compose down || true

echo "Building images..."
docker-compose build --no-cache

echo "Starting services..."
docker-compose up -d

echo "Waiting for services to start..."
sleep 10

echo "Checking service status..."
docker-compose ps

echo "Deployment completed!"

