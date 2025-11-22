#!/bin/bash

set -e

echo "Installing Docker..."
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
sudo systemctl enable docker
sudo systemctl start docker

echo "Installing Docker Compose..."
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

echo "Installing Git..."
sudo apt-get update
sudo apt-get install -y git

echo "Setting up application..."
sudo mkdir -p /opt/shop-app
sudo chown $(whoami):$(whoami) /opt/shop-app
git clone https://github.com/Foksen/DoCSA-2.git /opt/shop-app

echo "Starting services..."
cd /opt/shop-app/Pr8/deployment
docker-compose up -d

echo ""
echo "Setup completed!"
echo "Services started:"
docker-compose ps

