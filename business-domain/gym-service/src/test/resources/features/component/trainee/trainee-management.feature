Feature: Trainee Management
  As a gym system user
  I want to manage trainee profiles
  So that I can maintain accurate trainee information

  Background:
    Given the gym service is running
    And I have a valid authentication token

  @positive @create
  Scenario: Successfully register a new trainee
    Given I want to register a new trainee with the following details:
      | firstName | lastName | dateOfBirth | address           |
      | John      | Doe      | 1990-05-15  | 123 Main St, NY   |
    When I send a POST request to "/api/v1/trainees"
    Then the response status should be 201
    And the response should contain a valid trainee registration
    And the response should include generated username and password

  @positive @read
  Scenario: Successfully retrieve trainee profile
    Given a trainee exists with username "john.doe"
    When I send a GET request to "/api/v1/trainees/john.doe"
    Then the response status should be 200
    And the response should contain the trainee profile
    And the profile should have the correct personal information

  @positive @update
  Scenario: Successfully update trainee profile
    Given a trainee exists with username "john.doe"
    And I want to update the trainee with the following details:
      | firstName    | lastName     | dateOfBirth | address              |
      | John Updated | Doe Updated  | 1991-06-16  | 456 Updated St, NY  |
    When I send a PUT request to "/api/v1/trainees/john.doe"
    Then the response status should be 200
    And the response should contain the updated trainee profile
    And the profile should reflect the updated information

  @negative @delete @authorization
  Scenario: Fail to delete trainee profile without admin role
    Given a trainee exists with username "john.doe"
    When I send a DELETE request to "/api/v1/trainees/john.doe"
    Then the response status should be 403
    And the response should contain an authorization error

  @negative @create
  Scenario: Fail to register trainee with invalid data
    Given I want to register a new trainee with invalid details:
      | firstName | lastName | dateOfBirth | address |
      |           |          |             |         |
    When I send a POST request to "/api/v1/trainees"
    Then the response status should be 400
    And the response should contain validation errors

  @negative @read
  Scenario: Fail to retrieve non-existent trainee profile
    Given no trainee exists with username "nonexistent.user"
    When I send a GET request to "/api/v1/trainees/nonexistent.user"
    Then the response status should be 404
    And the response should contain a not found error

  @negative @update
  Scenario: Fail to update non-existent trainee profile
    Given no trainee exists with username "nonexistent.user"
    And I want to update the trainee with valid details
    When I send a PUT request to "/api/v1/trainees/nonexistent.user"
    Then the response status should be 404
    And the response should contain a not found error

  @negative @delete
  Scenario: Fail to delete non-existent trainee profile
    Given no trainee exists with username "nonexistent.user"
    When I send a DELETE request to "/api/v1/trainees/nonexistent.user"
    Then the response status should be 403
    And the response should contain an authorization error

  @negative @delete @authorization
  Scenario: Fail to delete trainee profile without admin role
    Given a trainee exists with username "john.doe"
    When I send a DELETE request to "/api/v1/trainees/john.doe"
    Then the response status should be 403
    And the response should contain an authorization error

  @negative @authentication
  Scenario: Fail to access trainee profile without authentication
    Given I don't have a valid authentication token
    When I send a GET request to "/api/v1/trainees/john.doe"
    Then the response status should be 401
    And the response should contain an authentication error 