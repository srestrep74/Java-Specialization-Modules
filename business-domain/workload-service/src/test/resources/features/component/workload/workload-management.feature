Feature: Trainer Workload Management
  As a workload service user
  I want to manage trainer workloads and retrieve summaries
  So that I can track and analyze trainer performance

  Background:
    Given the workload service is running for workload tests
    And I have a valid authentication token for workload

  @positive @process
  Scenario: Successfully process a trainer workload
    Given I want to process a trainer workload with the following details:
      | trainerUsername | firstName | lastName | isActive | trainingDate | duration | actionType |
      | test.trainer    | Test      | Trainer  | true     | 2024-12-25   | 60       | ADD        |
    When I send a POST request to process trainer workload at "/api/v1/workloads"
    Then the workload response status should be 200
    And the response should contain a valid workload processing

  @positive @process
  Scenario: Successfully process a trainer workload update
    Given I want to process a trainer workload with the following details:
      | trainerUsername | firstName | lastName | isActive | trainingDate | duration | actionType |
      | test.trainer    | Test      | Trainer  | true     | 2024-12-25   | 90       | UPDATE     |
    When I send a POST request to process trainer workload at "/api/v1/workloads"
    Then the workload response status should be 200
    And the response should contain a valid workload processing

  @positive @process
  Scenario: Successfully process a trainer workload deletion
    Given I want to process a trainer workload with the following details:
      | trainerUsername | firstName | lastName | isActive | trainingDate | duration | actionType |
      | test.trainer    | Test      | Trainer  | true     | 2024-12-25   | 60       | DELETE     |
    When I send a POST request to process trainer workload at "/api/v1/workloads"
    Then the workload response status should be 200
    And the response should contain a valid workload processing

  @positive @summary
  Scenario: Successfully retrieve trainer monthly summary
    Given a trainer workload exists for username "test.trainer"
    When I send a GET request to retrieve trainer monthly summary at "/api/v1/workloads/trainers/{username}/monthly-summary"
    Then the workload response status should be 200
    And the response should contain a valid monthly summary

  @positive @summary
  Scenario: Successfully retrieve trainer monthly summary by year
    Given a trainer workload exists for username "test.trainer"
    When I send a GET request to retrieve trainer monthly summary by year at "/api/v1/workloads/trainers/{username}/monthly-summary/{year}"
    Then the workload response status should be 200
    And the response should contain a valid monthly summary

  @positive @summary
  Scenario: Successfully retrieve trainer monthly summary by month
    Given a trainer workload exists for username "test.trainer"
    When I send a GET request to retrieve trainer monthly summary by month at "/api/v1/workloads/trainers/{username}/monthly-summary/{year}/{month}"
    Then the workload response status should be 200
    And the response should contain a valid monthly summary

  @negative @process
  Scenario: Fail to process trainer workload with invalid data
    Given I want to process a trainer workload with invalid details:
      | trainerUsername | firstName | lastName | isActive | trainingDate | duration | actionType |
      |                 |           |          |          |              |          |            |
    When I send a POST request to process trainer workload at "/api/v1/workloads"
    Then the workload response status should be 400
    And the response should contain workload validation errors

  @negative @summary
  Scenario: Fail to retrieve monthly summary for non-existent trainer
    When I send a GET request to retrieve trainer monthly summary at "/api/v1/workloads/trainers/nonexistent.trainer/monthly-summary"
    Then the workload response status should be 404
    And the response should contain a workload not found error

  @negative @authentication
  Scenario: Fail to process trainer workload without authentication
    Given I don't have a valid authentication token for workload
    And I want to process a trainer workload with the following details:
      | trainerUsername | firstName | lastName | isActive | trainingDate | duration | actionType |
      | test.trainer    | Test      | Trainer  | true     | 2024-12-25   | 60       | ADD        |
    When I send a POST request to process trainer workload at "/api/v1/workloads"
    Then the workload response status should be 401
    And the response should contain a workload authentication error

  @negative @authentication
  Scenario: Fail to retrieve monthly summary without authentication
    Given I don't have a valid authentication token for workload
    When I send a GET request to retrieve trainer monthly summary at "/api/v1/workloads/trainers/test.trainer/monthly-summary"
    Then the workload response status should be 401
    And the response should contain a workload authentication error 