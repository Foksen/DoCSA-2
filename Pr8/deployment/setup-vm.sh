#!/bin/bash

set -e

if ! command -v docker &> /dev/null; then
    echo "Installing Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker $USER
    sudo systemctl enable docker
    sudo systemctl start docker
else
    echo "Docker already installed, skipping..."
fi

if ! command -v docker-compose &> /dev/null; then
    echo "Installing Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
else
    echo "Docker Compose already installed, skipping..."
fi

echo "Installing Git..."
sudo apt-get update
sudo apt-get install -y git

echo "Setting up application..."
if [ ! -d "/opt/shop-app" ]; then
    sudo mkdir -p /opt/shop-app
    sudo chown $(whoami):$(whoami) /opt/shop-app
    git clone https://github.com/Foksen/DoCSA-2.git /opt/shop-app
elif [ -d "/opt/shop-app/.git" ]; then
    echo "Repository exists, updating..."
    cd /opt/shop-app
    git fetch origin
    git reset --hard origin/master
else
    echo "Directory exists but not a git repo, removing and cloning..."
    sudo rm -rf /opt/shop-app
    sudo mkdir -p /opt/shop-app
    sudo chown $(whoami):$(whoami) /opt/shop-app
    git clone https://github.com/Foksen/DoCSA-2.git /opt/shop-app
fi

echo ""
echo "VM setup completed successfully"
echo "Virtual machine is configured for automated deployments via GitHub Actions"
echo ""
echo "Required post-installation actions:"
echo "1. Configure GitHub Container Registry package visibility settings"
echo "2. Execute GitHub Actions deployment workflow"
echo ""
echo "Note: Re-login or execute 'newgrp docker' to apply Docker group permissions"

