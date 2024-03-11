Feature: driver-service component tests

  Scenario: Get driver availability statistics
    Given Total driver count is 10 and available driver count is 5
    When Driver availability requested to calculate total ride cost
    Then The response should contain 5 available drivers and 10 total drivers

  Scenario: Handle request to change driver status from another service
    Given Valid request to change driver status to "AVAILABLE"
    When Driver changes his status from "ON_TRIP"
    Then The driver should be with the new status of "AVAILABLE"

  Scenario: handle request to find driver for a ride
    Given Available driver for a ride exists
    When Service searching for a driver
    Then The ride response should be produced with isAvailable is true
    And Methods needed to successfully handle request to find driver for a ride were called

  Scenario: handle request to find driver for a ride
    Given No available drivers for a ride
    When Service unsuccessfully searching for a driver
    Then The ride response should be produced with isAvailable is false
    And Methods needed to unsuccessfully handle request to find driver for a ride were called

  Scenario: Handle request to change driver status from api endpoint
    Given Valid request to change driver status to "OFFLINE"
    When Driver changes his status from "AVAILABLE"
    Then The driver should be with the new status of "OFFLINE"
    And Methods needed to change driver status from api endpoint were called
