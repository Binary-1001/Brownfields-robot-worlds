#!/bin/bash

# 🤖 Robot Worlds Build Script
# Description: Comprehensive build and deployment script for Robot Worlds project
# Author: Generated for brownfields-robot-worlds-cjc-08
# Version: 1.0

set -e  # Exit on any error

# ===== CONFIGURATION =====
PROJECT_NAME="Robot Worlds"
PROJECT_VERSION="0.0.2"
JAR_NAME="robot-world-0.0.2.jar"
MAIN_CLASS="za.co.wethinkcode.robots.server.RobotWorldsServer"
DEFAULT_PORT=5000
SERVER_HOST="localhost"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# ===== FUNCTIONS =====
print_banner() {
    echo -e "${PURPLE}"
    echo "╔══════════════════════════════════════════════╗"
    echo "║           🤖 ROBOT WORLDS BUILDER           ║"
    echo "║                 Version 1.0                 ║"
    echo "╚══════════════════════════════════════════════╝"
    echo -e "${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_step() {
    echo -e "${CYAN}🔧 $1${NC}"
}

check_requirements() {
    print_step "Checking system requirements..."
    
    # Check Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
        print_success "Java found: $JAVA_VERSION"
    else
        print_error "Java is not installed or not in PATH"
        exit 1
    fi
    
    # Check Maven
    if command -v mvn &> /dev/null; then
        MAVEN_VERSION=$(mvn --version 2>&1 | head -n1)
        print_success "Maven found: $MAVEN_VERSION"
    else
        print_error "Maven is not installed or not in PATH"
        exit 1
    fi
    
    # Check project structure
    if [ ! -f "pom.xml" ]; then
        print_error "pom.xml not found. Are you in the project root?"
        exit 1
    fi
}

clean_project() {
    print_step "Cleaning project..."
    if mvn clean; then
        print_success "Project cleaned successfully"
    else
        print_error "Failed to clean project"
        exit 1
    fi
}

compile_project() {
    print_step "Compiling project..."
    if mvn compile; then
        print_success "Compilation successful"
    else
        print_error "Compilation failed"
        exit 1
    fi
}

run_tests() {
    local skip_tests=false
    if [ "$1" == "--skip" ]; then
        skip_tests=true
    fi
    
    if [ "$skip_tests" = true ]; then
        print_warning "Skipping tests as requested"
        return 0
    fi
    
    print_step "Running tests..."
    
    # Check if server is running for acceptance tests
    if is_server_running; then
        print_warning "Server is running - acceptance tests should work"
    else
        print_warning "Server not running - acceptance tests will fail"
    fi
    
    if mvn test; then
        print_success "All tests passed!"
    else
        print_warning "Some tests failed. Check target/surefire-reports/ for details"
        # Don't exit here - allow continuing for packaging
    fi
}

package_jar() {
    local skip_tests=false
    if [ "$1" == "--skip-tests" ]; then
        skip_tests=true
    fi
    
    print_step "Packaging application..."
    
    if [ "$skip_tests" = true ]; then
        if mvn clean package -DskipTests; then
            print_success "JAR packaged successfully (tests skipped)"
        else
            print_error "Packaging failed"
            exit 1
        fi
    else
        if mvn clean package; then
            print_success "JAR packaged successfully"
        else
            print_error "Packaging failed"
            exit 1
        fi
    fi
    
    # Verify JAR was created
    if [ -f "target/$JAR_NAME" ]; then
        local jar_size=$(ls -lh "target/$JAR_NAME" | awk '{print $5}')
        print_success "JAR created: target/$JAR_NAME ($jar_size)"
    else
        print_error "JAR file not found after packaging"
        exit 1
    fi
}

is_server_running() {
    if nc -z "$SERVER_HOST" "$DEFAULT_PORT" 2>/dev/null; then
        return 0  # Server is running
    else
        return 1  # Server is not running
    fi
}

start_server() {
    local port=$1
    if [ -z "$port" ]; then
        port=$DEFAULT_PORT
    fi
    
    print_step "Starting server on port $port..."
    
    if [ ! -f "target/$JAR_NAME" ]; then
        print_error "JAR file not found. Run './build.sh package' first."
        exit 1
    fi
    
    if is_server_running; then
        print_warning "Server is already running on port $DEFAULT_PORT"
        read -p "Do you want to stop it and start a new one? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            stop_server
        else
            print_info "Keeping existing server running"
            return 0
        fi
    fi
    
    print_info "Starting: java -cp target/$JAR_NAME $MAIN_CLASS $port"
    java -cp "target/$JAR_NAME" "$MAIN_CLASS" "$port" &
    SERVER_PID=$!
    
    # Wait a bit for server to start
    sleep 2
    
    if is_server_running; then
        print_success "Server started successfully (PID: $SERVER_PID) on port $port"
        echo "To stop the server, run: ./build.sh stop"
    else
        print_error "Server failed to start"
        exit 1
    fi
}

stop_server() {
    print_step "Stopping server..."
    
    # Find and kill the server process
    local pids=$(ps aux | grep "$MAIN_CLASS" | grep -v grep | awk '{print $2}')
    
    if [ -z "$pids" ]; then
        print_warning "No running server found"
        return 0
    fi
    
    for pid in $pids; do
        kill "$pid" 2>/dev/null && print_success "Stopped server (PID: $pid)" || print_error "Failed to stop server (PID: $pid)"
    done
    
    # Wait for port to be freed
    sleep 1
}

build_full() {
    print_banner
    print_step "Starting FULL build process..."
    
    check_requirements
    clean_project
    compile_project
    run_tests "$1"  # Pass --skip if provided
    package_jar "$1" # Pass --skip-tests if provided
    
    print_success "🎉 FULL BUILD COMPLETED SUCCESSFULLY!"
    echo ""
    print_info "Next steps:"
    echo "  ./build.sh run     - Start the server"
    echo "  ./build.sh status  - Check project status"
    echo "  ./build.sh clean   - Clean the project"
}

project_status() {
    print_banner
    print_step "Project Status Report"
    echo ""
    
    # Java info
    echo -e "${CYAN}Java Environment:${NC}"
    java -version 2>&1 | grep "version"
    echo ""
    
    # Maven info
    echo -e "${CYAN}Maven Environment:${NC}"
    mvn --version 2>&1 | head -n1
    echo ""
    
    # Project info
    echo -e "${CYAN}Project Structure:${NC}"
    if [ -f "pom.xml" ]; then
        print_success "pom.xml found"
    else
        print_error "pom.xml missing"
    fi
    
    if [ -f "target/$JAR_NAME" ]; then
        local jar_info=$(ls -lh "target/$JAR_NAME")
        print_success "JAR built: $jar_info"
    else
        print_warning "No JAR file built yet"
    fi
    echo ""
    
    # Server status
    echo -e "${CYAN}Server Status:${NC}"
    if is_server_running; then
        print_success "Server is RUNNING on port $DEFAULT_PORT"
    else
        print_warning "Server is NOT RUNNING"
    fi
    echo ""
    
    # Git status (if available)
    if command -v git &> /dev/null && [ -d ".git" ]; then
        echo -e "${CYAN}Git Status:${NC}"
        git branch --show-current 2>/dev/null | xargs echo "Current branch:"
        git status --short 2>/dev/null | head -10
    fi
}

show_help() {
    print_banner
    echo -e "${GREEN}Usage: ./build.sh [COMMAND] [OPTIONS]${NC}"
    echo ""
    echo -e "${CYAN}Available Commands:${NC}"
    echo "  ${YELLOW}help${NC}      - Show this help message"
    echo "  ${YELLOW}full${NC}      - Complete build (clean, compile, test, package)"
    echo "  ${YELLOW}compile${NC}   - Compile the project"
    echo "  ${YELLOW}test${NC}      - Run tests"
    echo "  ${YELLOW}package${NC}   - Create JAR package"
    echo "  ${YELLOW}run${NC}       - Start the server"
    echo "  ${YELLOW}stop${NC}      - Stop the server"
    echo "  ${YELLOW}status${NC}    - Show project status"
    echo "  ${YELLOW}clean${NC}     - Clean project"
    echo "  ${YELLOW}requirements${NC} - Check system requirements"
    echo ""
    echo -e "${CYAN}Options:${NC}"
    echo "  ${YELLOW}--skip${NC}    - Skip tests during build"
    echo ""
    echo -e "${CYAN}Examples:${NC}"
    echo "  ./build.sh full              # Complete build with tests"
    echo "  ./build.sh full --skip       # Complete build without tests"
    echo "  ./build.sh package           # Build JAR (run tests)"
    echo "  ./build.sh package --skip    # Build JAR (skip tests)"
    echo "  ./build.sh run               # Start server on default port (5000)"
    echo "  ./build.sh run 8080          # Start server on port 8080"
    echo "  ./build.sh status            # Show project status"
    echo ""
}

# ===== MAIN SCRIPT =====
case "$1" in
    "help"|"--help"|"-h")
        show_help
        ;;
    "full")
        build_full "$2"
        ;;
    "compile")
        print_banner
        check_requirements
        compile_project
        ;;
    "test")
        print_banner
        check_requirements
        run_tests "$2"
        ;;
    "package")
        print_banner
        check_requirements
        package_jar "$2"
        ;;
    "run")
        print_banner
        check_requirements
        start_server "$2"
        ;;
    "stop")
        print_banner
        stop_server
        ;;
    "status")
        project_status
        ;;
    "clean")
        print_banner
        clean_project
        ;;
    "requirements")
        print_banner
        check_requirements
        ;;
    "")
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac
