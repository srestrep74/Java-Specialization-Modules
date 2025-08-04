# Cucumber Component Tests for Trainee Management

This directory contains Cucumber-based component tests for the Trainee Management functionality of the Gym Service.

## Structure

```
cucumber/component/
├── CucumberComponentTestConfig.java     # Spring Boot test configuration
├── TraineeTestContext.java              # Shared test context and data management
├── TraineeComponentTestSuite.java       # Test suite configuration
├── steps/
│   ├── TraineeManagementSteps.java      # Main step definitions
│   └── TraineeStepDefinitions.java      # Additional step definitions
└── README.md                            # This file
```

## Features

The tests cover the following scenarios:

### Positive Scenarios
- ✅ Register a new trainee with valid data
- ✅ Retrieve trainee profile
- ✅ Update trainee profile
- ✅ Delete trainee profile

### Negative Scenarios
- ❌ Register trainee with invalid data
- ❌ Retrieve non-existent trainee profile
- ❌ Update non-existent trainee profile
- ❌ Delete non-existent trainee profile
- ❌ Access without authentication

## Running the Tests

### Using Maven
```bash
# Run all component tests
mvn test -Dtest=TraineeComponentTestSuite

# Run specific tags
mvn test -Dcucumber.filter.tags="@positive"
mvn test -Dcucumber.filter.tags="@negative"
mvn test -Dcucumber.filter.tags="@create"

# Run with specific profile
mvn test -Dspring.profiles.active=test -Dtest=TraineeComponentTestSuite
```

### Using IDE
1. Right-click on `TraineeComponentTestSuite.java`
2. Select "Run as JUnit Test"

## Test Reports

After running the tests, reports will be generated in:
- HTML: `target/cucumber-reports/trainee-component.html`
- JSON: `target/cucumber-reports/trainee-component.json`

## Configuration

The tests use:
- Spring Boot Test with random port
- Test profile (`application-test.yml`)
- Mocked external dependencies
- In-memory H2 database for testing

## Best Practices Followed

1. **BDD Approach**: Tests are written in Gherkin syntax for better readability
2. **Separation of Concerns**: Step definitions are separated from test context
3. **Data Management**: Centralized test context for sharing data between steps
4. **Error Handling**: Comprehensive negative scenario testing
5. **Authentication**: Proper token management for authenticated endpoints
6. **Cleanup**: Automatic cleanup after each scenario
7. **Tagging**: Scenarios are tagged for selective execution

## Adding New Scenarios

1. Add new scenarios to `features/component/trainee/trainee-management.feature`
2. Implement corresponding step definitions in `TraineeManagementSteps.java`
3. Add any new test data creation methods to `TraineeTestContext.java`
4. Tag scenarios appropriately for organization

## Dependencies

The tests require the following Cucumber dependencies (already in pom.xml):
- `cucumber-java`
- `cucumber-junit-platform-engine`
- `cucumber-spring`
- `junit-platform-suite` 