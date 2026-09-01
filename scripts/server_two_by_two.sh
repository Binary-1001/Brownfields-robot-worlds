#!/bin/bash
cd "$(dirname "$0")/.."

TEST_DIR="src/test/java/za/co/wethinkcode/robots/AcceptanceTests/iteration2"
TYPE_TEST=$1

# Collect iteration2 tests only
TEST_CLASSES=$(find "$TEST_DIR" -type f -name "*Test.java" \
  | sed 's|src/test/java/||; s|/|.|g; s|.java$||')

echo "===== Running Iteration 2 Tests (NO external server) ====="

for CLASS in $TEST_CLASSES; do
    echo "=========================================="
    echo "▶ Running: $CLASS"
    echo "=========================================="
    mvn test -Dtest="$CLASS" || true
done


