# === Robot Worlds Makefile (Full Detailed Reporting - Fixed Counts + Line Info) ===

APP_NAME = robot-world
VERSION  = 0.1.0
JAR_FILE = target/$(APP_NAME)-$(VERSION).jar

# === Colors ===
GREEN  = \033[0;32m
YELLOW = \033[1;33m
RED    = \033[0;31m
BLUE   = \033[1;34m
RESET  = \033[0m

.PHONY: all compile acceptance-tests  clean  test-both launch-client launch-server launch-both

# === Default target ===
all: test-both

compile:
	@echo "$(BLUE)🔧 Compiling project...$(RESET)"
	mvn compile

# === Run local unit tests only (skip acceptance tests) ===
test: compile
	@echo "$(YELLOW)🧪 Running Local Unit Tests (excluding AcceptanceTests)...$(RESET)"
	@mvn -Dsurefire.printSummary=true test -DfailIfNoTests=false -Dtest='*Test,!*AcceptanceTest' | tee test_local.log
	@echo "$(GREEN)✅ Unit tests completed. See test_local.log for details.$(RESET)"


# === Package the server (build JAR but skip tests) ===
package-server: compile
	@echo "$(BLUE)📦 Packaging the Robot World Server (skipping tests)...$(RESET)"
	@mvn package -DskipTests
	@echo "$(GREEN)✅ Server packaged successfully at target/$(APP_NAME)-$(VERSION).jar$(RESET)"

# === Build final release JAR (clean + skip tests) ===
release:
	@echo "$(BLUE)🚀 Building release version of Robot Worlds (skipping tests)...$(RESET)"
	@mvn clean package -DskipTests
	@echo "$(GREEN)🎉 Release JAR created: target/$(APP_NAME)-$(VERSION).jar$(RESET)"

# === Run tests against both servers and compare results ===
test-both: compile
	@echo "$(BLUE)==================================================$(RESET)"
	@echo "$(YELLOW)🧪 RUNNING LEGACY ACCEPTANCE TESTS (1x1 world)$(RESET)"
	@echo "$(BLUE)==================================================$(RESET)"
	./scripts/server_one_by_one.sh ref
	./scripts/server_one_by_one.sh own

	@echo ""
	@echo "$(BLUE)==================================================$(RESET)"
	@echo "$(YELLOW)🧪 RUNNING ITERATION 2 ACCEPTANCE TESTS (2x2 world)$(RESET)"
	@echo "$(BLUE)==================================================$(RESET)"
	./scripts/server_two_by_two.sh

	@echo ""
	@echo "$(GREEN)🎉 All Acceptance Test Suites Completed$(RESET)"

# === Original acceptance-tests target (for backward compatibility) ===
acceptance-tests: compile
	@echo "$(YELLOW)🧪 Running Acceptance Tests (Legacy + Iteration 2)...$(RESET)"
	./scripts/server_one_by_one.sh own
	./scripts/server_two_by_two.sh own

clean:
	@echo "$(RED)🧹 Cleaning project...$(RESET)"
	mvn clean

# === Launch Targets ===
launch-client:
	@echo "$(GREEN)🚀 Launching Robot Worlds Client...$(RESET)"
	scripts/launch_client.sh

launch-server:
	@echo "$(GREEN)🚀 Launching Robot Worlds Server...$(RESET)"
	scripts/launch_server.sh

launch-both:
	@echo "$(GREEN)🚀 Launching Both Server and Client...$(RESET)"
	scripts/launch_both.sh

# === Development Quick Start ===
dev: compile launch-both
	@echo "$(GREEN)🎯 Development environment ready!$(RESET)"