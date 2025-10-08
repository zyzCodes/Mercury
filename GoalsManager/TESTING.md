# Goals Management System - Testing Documentation

## Overview

Comprehensive test suite for the Goals Management System covering unit tests, repository tests, and integration tests.

## Test Configuration

### Test Database

Tests use an **H2 in-memory database** to avoid dependencies on PostgreSQL during testing.

### Dependencies

Added to `build.gradle`:
```gradle
testRuntimeOnly 'com.h2database:h2'
```
## Running Tests

### Run All Goal Tests

```bash
cd GoalsManager
./gradlew test --tests "*Goal*Test"
```

### Run Specific Test Classes

```bash
# Service tests only
./gradlew test --tests "com.example.goalsmanager.service.GoalServiceTest"

# Repository tests only
./gradlew test --tests "com.example.goalsmanager.repository.GoalRepositoryTest"

# Controller tests only
./gradlew test --tests "com.example.goalsmanager.controller.GoalControllerTest"
```

### Run All Tests

```bash
./gradlew test
```

### Generate Test Report

```bash
./gradlew test
# View report at: build/reports/tests/test/index.html
```
