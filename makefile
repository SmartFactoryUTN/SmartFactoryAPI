# Define default target (can be 'build', 'test', etc.)
.DEFAULT_GOAL := help

# Variables
GRADLEW := ./gradlew
COMPOSE=docker-compose

# Gradle tasks
.PHONY: build
build:
	$(GRADLEW) build

.PHONY: clean
clean:
	$(GRADLEW) clean

.PHONY: test
test:
	$(GRADLEW) test

.PHONY: coverage
coverage:
	$(GRADLEW) jacocoTestReport


.PHONY: run
run:
	$(GRADLEW) bootRun

.PHONY: dependencies
dependencies:
	$(GRADLEW) dependencies

.PHONY: help
help:
	$(GRADLEW) tasks

# Example: Create a task to run with specific JVM options
.PHONY: runWithOpts
runWithOpts:
	$(GRADLEW) bootRun -Dorg.gradle.jvmargs="-Xmx1024m"

# Targets
.PHONY: up down

# Start containers with build and detached mode
up:
	$(COMPOSE) up --build -d

# Stop and remove containers
down:
	$(COMPOSE) down
