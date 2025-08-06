Feature: Workload Notification Integration
  As a system administrator
  I want to ensure that training operations are properly notified to the workload service
  So that trainers' workload is accurately calculated and tracked

  Background:
    Given the gym-service is running with test configuration
    And the ActiveMQ broker is available and configured
    And the workload-service is ready to receive messages
    And the database is initialized with test data

  @happy-path
  Scenario: Successful notification when a new training is created
    Given a valid trainer with username "trainer1" exists in the system
    And a valid trainee with username "trainee1" exists in the system
    When I create a new training with the following details:
      | trainerUsername | traineeUsername | trainingName | trainingDate | trainingDuration |
      | trainer1        | trainee1        | Yoga Session | 2024-01-15   | 60               |
    Then a workload notification message should be sent to ActiveMQ
    And the message should contain the correct trainer information:
      | trainerUsername | trainerFirstName | trainerLastName | isActive | actionType |
      | trainer1        | John             | Doe             | true     | ADD        |
    And the message should contain the correct training information:
      | trainingDate | trainingDuration |
      | 2024-01-15   | 60               |
    And the training should be successfully saved in the database



  @delete-scenario
  Scenario: Successful notification when a training is deleted
    Given a valid trainer with username "trainer4" exists in the system
    And a valid trainee with username "trainee4" exists in the system
    And a training exists for trainer "trainer4" and trainee "trainee4"
    When I delete the training
    Then a workload notification message should be sent to ActiveMQ
    And the message action type should be "DELETE"
    And the training should be removed from the database

  @error-handling
  Scenario: Handle invalid trainer username gracefully
    Given a valid trainee with username "trainee5" exists in the system
    When I attempt to create a training with non-existent trainer "nonexistent_trainer"
    Then the operation should fail with a "Trainer not found" error
    And no workload notification should be sent

  @error-handling
  Scenario: Handle invalid trainee username gracefully
    Given a valid trainer with username "trainer6" exists in the system
    When I attempt to create a training with non-existent trainee "nonexistent_trainee"
    Then the operation should fail with a "Trainee not found" error
    And no workload notification should be sent

  @circuit-breaker
  Scenario: Circuit breaker activates after multiple ActiveMQ failures
    Given a valid trainer with username "trainer7" exists in the system
    And a valid trainee with username "trainee7" exists in the system
    And ActiveMQ broker is consistently failing
    When I create multiple trainings in rapid succession
    Then the circuit breaker should activate
    And all workload notifications should be saved to pending workload table
    And the system should continue to function normally