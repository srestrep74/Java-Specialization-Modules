# Workload Service Component Tests

This directory contains Cucumber component tests for the `TrainerWorkloadController` in the workload service.

## Overview

The workload service component tests verify the behavior of the trainer workload management endpoints, including:

- Processing trainer workloads (ADD, UPDATE, DELETE operations)
- Retrieving trainer monthly summaries
- Authentication and authorization
- Error handling and validation

## Test Structure

### Feature Files

- `workload-management.feature` - Main feature file containing all scenarios

### Test Scenarios

#### Positive Scenarios
- **Process Workload**: Successfully process trainer workload with ADD action
- **Update Workload**: Successfully process trainer workload with UPDATE action  
- **Delete Workload**: Successfully process trainer workload with DELETE action
- **Retrieve Summary**: Successfully retrieve trainer monthly summary
- **Retrieve Summary by Year**: Successfully retrieve trainer monthly summary for specific year
- **Retrieve Summary by Month**: Successfully retrieve trainer monthly summary for specific month

#### Negative Scenarios
- **Invalid Data**: Fail to process trainer workload with invalid data
- **Non-existent Trainer**: Fail to retrieve monthly summary for non-existent trainer
- **No Authentication**: Fail to process workload without authentication
- **No Authentication for Summary**: Fail to retrieve summary without authentication

## Running Tests

### Run All Component Tests
```bash
mvn test -Dtest=WorkloadComponentTestSuite
```

### Run Specific Tags
```bash
# Run only positive scenarios
mvn test -Dtest=WorkloadComponentTestSuite -Dcucumber.filter.tags="@positive"

# Run only process scenarios
mvn test -Dtest=WorkloadComponentTestSuite -Dcucumber.filter.tags="@process"

# Run only summary scenarios
mvn test -Dtest=WorkloadComponentTestSuite -Dcucumber.filter.tags="@summary"

# Run only negative scenarios
mvn test -Dtest=WorkloadComponentTestSuite -Dcucumber.filter.tags="@negative"
```

## Test Architecture

### Components

1. **WorkloadTestContext**: Manages test state and data
2. **CommonHttpSteps**: Provides shared HTTP request methods
3. **WorkloadManagementSteps**: Implements step definitions for workload scenarios
4. **WorkloadTestHooks**: Handles test setup and teardown
5. **CucumberComponentTestConfig**: Spring Boot test configuration

### Key Features

- **Token-based Authentication**: Simulates authentication tokens from the gym service
- **Mock Data**: Uses mock data for testing without external dependencies
- **Comprehensive Coverage**: Tests all major endpoints and error scenarios
- **BDD Approach**: Uses Gherkin syntax for readable test scenarios

## Notes

- The workload service receives authentication tokens from the gym service
- Tests use mock tokens to simulate the authentication flow
- All endpoints require TRAINER or ADMIN role authorization
- MongoDB is used for data persistence in the workload service 