#!/bin/bash

#set -e

# Go to project root
cd "$(dirname "$0")/.."

TEST_DIR="src/test/java/za/co/wethinkcode/robots/AcceptanceTests"
TYPE_TEST=$1

# Collect test classes excluding iteration2 folder
TEST_CLASSES=$(find "$TEST_DIR" -type f -name "*Test.java" \
  ! -path "*/iteration2/*" \
  | sed 's|src/test/java/||; s|/|.|g; s|.java$||')

# Function to stop anything on port 5000
kill_port_5000() {
    echo "Checking for processes on port 5000..."
    PIDS=$(lsof -ti tcp:5000)

    if [ ! -z "$PIDS" ]; then
        echo "Killing processes: $PIDS"
        kill -9 $PIDS 2>/dev/null || true
        sleep 1
    fi

    # Double check using fuser (some systems need this)
    if command -v fuser >/dev/null 2>&1; then
        echo "Force killing with fuser..."
        fuser -k 5000/tcp 2>/dev/null || true
        sleep 1
    fi
}

# Function to start reference server
start_reference_server() {
    echo "Starting reference server..."
    java -jar libs/reference-server-0.1.0.jar >/dev/null 2>&1 &
    SERVER_PID=$!
    trap "kill -9 $SERVER_PID 2>/dev/null" EXIT
    sleep 2
}

# Function to start local server
start_local_server() {
    echo "Starting local server..."
    mvn exec:java -Dexec.mainClass=za.co.wethinkcode.robots.server.Server >/dev/null 2>&1 &
    SERVER_PID=$!
    trap "kill -9 $SERVER_PID 2>/dev/null" EXIT
    sleep 2
}

# RUN AGAINST REFERENCE SERVER
if [ "$TYPE_TEST" = "ref" ]; then
    echo "===== Testing Against Reference Server ====="
    for CLASS in $TEST_CLASSES; do
        echo "▶ Running: $CLASS"
        kill_port_5000
        start_reference_server
        mvn test -Dtest="$CLASS" || true
        kill_port_5000
    done

# RUN AGAINST LOCAL SERVER
elif [ "$TYPE_TEST" = "own" ]; then
    echo "===== Testing Against Local Server ====="
    for CLASS in $TEST_CLASSES; do
        echo "▶ Running: $CLASS"
        kill_port_5000
        start_local_server
        mvn test -Dtest="$CLASS" || true
        kill_port_5000
    done

else
    echo "This command: $TYPE_TEST does not exist"
    echo "Use: ./scripts/server_one_by_one.sh ref"
    echo "     ./scripts/server_one_by_one.sh own"
    exit 1
fi
