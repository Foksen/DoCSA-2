#!/bin/bash

CONTAINER_NAME="postgres-dev"
POSTGRES_USER="postgres"
POSTGRES_PASSWORD="postgres"
POSTGRES_PORT="5432"

case "$1" in
  start)
    if [ "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
      echo "PostgreSQL already running"
      exit 0
    fi

    if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
      echo "Starting existing container..."
      docker start $CONTAINER_NAME
      echo "PostgreSQL started"
      exit 0
    fi

    echo "Creating new PostgreSQL container..."
    docker run -d \
      --name $CONTAINER_NAME \
      -e POSTGRES_USER=$POSTGRES_USER \
      -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
      -p $POSTGRES_PORT:5432 \
      postgres:15-alpine

    echo "Waiting for PostgreSQL to start..."
    sleep 5

    echo "Creating databases..."
    docker exec $CONTAINER_NAME psql -U $POSTGRES_USER -c "CREATE DATABASE auth_db;" 2>/dev/null
    docker exec $CONTAINER_NAME psql -U $POSTGRES_USER -c "CREATE DATABASE product_db;" 2>/dev/null
    docker exec $CONTAINER_NAME psql -U $POSTGRES_USER -c "CREATE DATABASE customer_db;" 2>/dev/null
    docker exec $CONTAINER_NAME psql -U $POSTGRES_USER -c "CREATE DATABASE order_db;" 2>/dev/null

    echo "PostgreSQL ready at localhost:$POSTGRES_PORT"
    echo "Databases: auth_db, product_db, customer_db, order_db"
    ;;

  stop)
    if [ "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
      docker stop $CONTAINER_NAME
      echo "PostgreSQL stopped"
    else
      echo "PostgreSQL not running"
    fi
    ;;

  remove)
    if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
      docker stop $CONTAINER_NAME 2>/dev/null
      docker rm $CONTAINER_NAME
      echo "PostgreSQL container removed"
    else
      echo "Container not found"
    fi
    ;;

  *)
    echo "Usage: $0 {start|stop|remove}"
    exit 1
    ;;
esac

