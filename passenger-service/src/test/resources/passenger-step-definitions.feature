Feature: passenger-service component test

  Scenario: Get all passengers
    Given 3 passengers in the database
    When The business logic to get all passengers is invoked
    Then The response should contain 3 passengers

  Scenario: Get existing passenger by id
    Given Passenger exists in database
    When The business logic to get passenger by id is invoked
    Then The response should be present and contain found passenger

  Scenario: Create passenger
    Given Valid passenger request send to the service
    When The business logic to create passenger is invoked
    Then The response should be present and contain created passenger

  Scenario: Update passenger
    Given Valid passenger update request and id send to the service
    When The business logic to update passenger by id is invoked
    Then The response should be present and contain updated passenger

  Scenario: Delete passenger
    Given Passenger exists in database
    When The business logic to delete passenger by id is invoked
    Then The passenger should be deleted from database