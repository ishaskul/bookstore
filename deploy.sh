#!/bin/bash

# Default stack names
stack_name="experimental"
autoscaler_stack_name="autoscaler"
compose_file="docker-compose.yml"

# Function to display script usage
usage() {
    echo "Usage: $0 [-n <name_of_stack>] [-c <compose_file_name>]" 1>&2
    exit 1
}

# Parse command-line options
while getopts ":n:c:" opt; do
    case ${opt} in
        n )
            stack_name=$OPTARG
            ;;
        c )
            compose_file=$OPTARG
            ;;
        \? )
            usage
            ;;
    esac
done
shift $((OPTIND -1))

# Step 0: Delete previous registry if it exists
docker service rm registry >/dev/null 2>&1

# Step 1: Run disposable registry
docker service create --name registry --publish published=5000,target=5000 registry:2

# Step 2: Create network
docker network create --driver overlay bookstore-app-network

# Step 3: Build and push docker images
docker compose build --push

# Step 4: Deploy main application stack
echo "Deploying $stack_name stack..."
docker stack deploy --with-registry-auth -c $compose_file $stack_name
