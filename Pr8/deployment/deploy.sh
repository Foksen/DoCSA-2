#!/bin/bash

set -e

APP_DIR="/opt/shop-app"

echo "Updating repository..."
cd $APP_DIR
git fetch origin 2>&1 | grep -E "^(From|->)" || true
git reset --hard origin/master | head -1

cd $APP_DIR/Pr8/deployment

echo "Stopping services..."
sudo docker-compose down > /dev/null 2>&1 || true

echo "Pulling latest images..."
sudo docker-compose pull --quiet

echo "Starting services..."
sudo docker-compose up -d --quiet-pull

echo "Waiting for services to initialize..."
sleep 15

echo "Deployment completed. Service status:"
sudo docker-compose ps

