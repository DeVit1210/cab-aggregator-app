Feature: passenger-service component test

  Scenario: Get all drivers
    Given 3 drivers in the database
    When The business logic to get all drivers is invoked
    Then The response should contain 3 drivers

  Scenario: Get existing driver by id
    Given Passenger exists in database
    When The business logic to get passenger by id is invoked
    Then The response should be present and contain found driver

  Scenario: Create passenger
    Given Valid passenger request send to the service
    When The business logic to create passenger is invoked
    Then The response should be present and contain created driver

  Scenario: Update passenger
    Given Valid passenger update request and id send to the service
    When The business logic to update passenger by id is invoked
    Then The response should be present and contain updated driver

  Scenario: Delete passenger
    Given Passenger exists in database
    When The business logic to delete passenger by id is invoked
    Then The passenger should be deleted from database