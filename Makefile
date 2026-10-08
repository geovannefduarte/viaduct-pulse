.DEFAULT_GOAL := help

GRADLE := ./gradlew

ASDF_JAVA_HOME := $(shell command -v asdf >/dev/null 2>&1 && asdf where java 2>/dev/null)
ifneq ($(ASDF_JAVA_HOME),)
export JAVA_HOME := $(ASDF_JAVA_HOME)
endif

.PHONY: help setup run test build check format clean db-reset

help: ## List the targets
	@awk 'BEGIN {FS = ":.*## "} /^[a-z-]+:.*## / {printf "  %-10s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

setup: ## Install the JDK pinned in .tool-versions with asdf
	asdf plugin add java 2>/dev/null || true
	asdf install

run: ## Start the app on http://localhost:8080 with the local profile
	$(GRADLE) :app:bootRun

test: ## Run every test
	$(GRADLE) test

build: ## Compile, run every test and check formatting
	$(GRADLE) build

check: ## Run every test and check formatting, without building jars
	$(GRADLE) check

format: ## Format Kotlin sources and Gradle scripts
	$(GRADLE) spotlessApply

clean: ## Delete build outputs
	$(GRADLE) clean

db-reset: ## Delete the local database in .pulse/pgdata; stop the app first
	rm -rf .pulse/pgdata
