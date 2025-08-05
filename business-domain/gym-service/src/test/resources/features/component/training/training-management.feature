Feature: Training Management
  As a gym system user
  I want to manage training sessions
  So that I can schedule and track workouts between trainees and trainers

  Background:
    Given the gym service is running for training tests
    And I have a valid authentication token for training

  @positive @create
  Scenario: Successfully create a new training session
    Given I want to create a new training with the following details:
      | traineeUsername | trainerUsername | trainingName | trainingDate | duration |
      | test.trainee    | test.trainer    | Zumba        | 2024-12-25   | 60       |
    When I send a POST request to create training at "/api/v1/trainings"
    Then the training response status should be 200
    And the response should contain a valid training creation

  @positive @update
  Scenario: Successfully update an existing training session
    Given a training exists with trainee "test.trainee" and trainer "test.trainer"
    And I want to update the training with the following details:
      | trainingName    | trainingDate | duration | trainerUsername | traineeUsername | trainingType |
      | Zumba Updated   | 2024-12-25   | 90       | test.trainer    | test.trainee    | Zumba        |
    When I send a PUT request to update training at "/api/v1/trainings"
    Then the training response status should be 200
    And the response should contain a valid training update

  @positive @delete
  Scenario: Successfully delete an existing training session
    Given a training exists with trainee "test.trainee" and trainer "test.trainer"
    And I want to delete the training with the following details:
      | traineeUsername | trainerUsername | trainingDate |
      | test.trainee    | test.trainer    | 2024-12-25   |
    When I send a DELETE request to delete training at "/api/v1/trainings"
    Then the training response status should be 200
    And the response should contain a valid training deletion

  @negative @create
  Scenario: Fail to create training with invalid data
    Given I want to create a new training with invalid details:
      | traineeUsername | trainerUsername | trainingName | trainingDate | duration |
      |                 |                 |             |              |          |
    When I send a POST request to create training at "/api/v1/trainings"
    Then the training response status should be 400
    And the response should contain training validation errors

  @negative @create
  Scenario: Fail to create training with non-existent users
    Given I want to create a new training with non-existent users:
      | traineeUsername      | trainerUsername      | trainingName | trainingDate | duration |
      | nonexistent.trainee  | nonexistent.trainer  | Invalid      | 2024-12-25   | 60       |
    When I send a POST request to create training at "/api/v1/trainings"
    Then the training response status should be 404
    And the response should contain a training not found error

  @negative @update
  Scenario: Fail to update non-existent training session
    Given I want to update the training with non-existent users:
      | trainingName    | trainingDate | duration | trainerUsername      | traineeUsername      | trainingType |
      | Zumba Updated   | 2024-12-25   | 90       | nonexistent.trainer  | nonexistent.trainee  | Zumba        |
    When I send a PUT request to update training at "/api/v1/trainings"
    Then the training response status should be 404
    And the response should contain a training not found error

  @negative @delete
  Scenario: Fail to delete non-existent training session
    Given I want to delete the training with non-existent users:
      | traineeUsername      | trainerUsername      | trainingDate |
      | nonexistent.trainee  | nonexistent.trainer  | 2024-12-25   |
    When I send a DELETE request to delete training at "/api/v1/trainings"
    Then the training response status should be 404
    And the response should contain a training not found error

  @negative @authentication
  Scenario: Fail to create training without authentication
    Given I don't have a valid authentication token for training
    And I want to create a new training with the following details:
      | traineeUsername | trainerUsername | trainingName | trainingDate | duration |
      | test.trainee    | test.trainer    | Zumba        | 2024-12-25   | 60       |
    When I send a POST request to create training at "/api/v1/trainings"
    Then the training response status should be 401
    And the response should contain a training authentication error

  @negative @authentication
  Scenario: Fail to update training without authentication
    Given I don't have a valid authentication token for training
    And I want to update the training with the following details:
      | trainingName    | trainingDate | duration | trainerUsername | traineeUsername | trainingType |
      | Zumba Updated   | 2024-12-25   | 90       | test.trainer    | test.trainee    | Zumba        |
    When I send a PUT request to update training at "/api/v1/trainings"
    Then the training response status should be 401
    And the response should contain a training authentication error

  @negative @authentication
  Scenario: Fail to delete training without authentication
    Given I don't have a valid authentication token for training
    And I want to delete the training with the following details:
      | traineeUsername | trainerUsername | trainingDate |
      | test.trainee    | test.trainer    | 2024-12-25   |
    When I send a DELETE request to delete training at "/api/v1/trainings"
    Then the training response status should be 401
    And the response should contain a training authentication error 