Feature: Trainer Management
  As a gym system user
  I want to manage trainer profiles
  So that I can maintain accurate trainer information

  Background:
    Given the gym service is running for trainer tests
    And I have a valid authentication token for trainer

  @positive @create
  Scenario: Successfully register a new trainer
    Given I want to register a new trainer with the following details:
      | firstName | lastName | trainingTypeId |
      | John      | Trainer  | 1              |
    When I send a POST request to register trainer at "/api/v1/trainers"
    Then the trainer response status should be 201
    And the response should contain a valid trainer registration
    And the response should include generated trainer username and password

  @positive @read
  Scenario: Successfully retrieve trainer profile
    Given a trainer exists with username "john.trainer"
    When I send a GET request to retrieve trainer profile at "/api/v1/trainers/john.trainer"
    Then the trainer response status should be 200
    And the response should contain the trainer profile
    And the trainer profile should have the correct personal information

  @positive @update
  Scenario: Successfully update trainer profile
    Given a trainer exists with username "john.trainer"
    And I want to update the trainer with the following details:
      | firstName    | lastName     | trainingTypeId | active |
      | John Updated | Trainer Updated | 2              | true   |
    When I send a PUT request to update trainer profile at "/api/v1/trainers/john.trainer"
    Then the trainer response status should be 200
    And the response should contain the updated trainer profile
    And the trainer profile should reflect the updated information

  @positive @activation
  Scenario: Successfully update trainer activation status
    Given a trainer exists with username "john.trainer"
    And I want to deactivate the trainer
    When I send a PATCH request to update trainer activation at "/api/v1/trainers/john.trainer/activation"
    Then the trainer response status should be 200
    And the response should contain a location header for trainer

  @positive @trainings
  Scenario: Successfully retrieve trainer trainings
    Given a trainer exists with username "john.trainer"
    When I send a GET request to retrieve trainer trainings at "/api/v1/trainers/john.trainer/trainings"
    Then the trainer response status should be 200
    And the response should contain a list of trainer trainings

  @negative @create
  Scenario: Fail to register trainer with invalid data
    Given I want to register a new trainer with invalid details:
      | firstName | lastName | trainingTypeId |
      |           |          |                |
    When I send a POST request to register trainer at "/api/v1/trainers"
    Then the trainer response status should be 400
    And the response should contain trainer validation errors

  @negative @read
  Scenario: Fail to retrieve non-existent trainer profile
    Given no trainer exists with username "nonexistent.trainer"
    When I send a GET request to retrieve trainer profile at "/api/v1/trainers/nonexistent.trainer"
    Then the trainer response status should be 404
    And the response should contain a trainer not found error

  @negative @update
  Scenario: Fail to update non-existent trainer profile
    Given no trainer exists with username "nonexistent.trainer"
    And I want to update the trainer with valid details
    When I send a PUT request to update trainer profile at "/api/v1/trainers/nonexistent.trainer"
    Then the trainer response status should be 404
    And the response should contain a trainer not found error

  @negative @authorization
  Scenario: Fail to access trainer profile without trainer role
    Given a trainer exists with username "john.trainer"
    And I have a valid authentication token without trainer role
    When I send a GET request to retrieve trainer profile at "/api/v1/trainers/john.trainer"
    Then the trainer response status should be 403
    And the response should contain a trainer authorization error

  @negative @authentication
  Scenario: Fail to access trainer profile without authentication
    Given I don't have a valid authentication token for trainer
    When I send a GET request to retrieve trainer profile at "/api/v1/trainers/john.trainer"
    Then the trainer response status should be 401
    And the response should contain a trainer authentication error 