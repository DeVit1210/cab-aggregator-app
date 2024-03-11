Feature: ride-service component tests

  Scenario: Find all rides for person
    Given 3 finished rides exists for person
    When "DRIVER" search for his ride history
    Then Ride list response should contain 3 rides
    And Methods needed to retrieve all rides for driver were called

  Scenario: Find all rides for person
    Given No finished rides for person
    When "PASSENGER" search for his ride history
    Then Ride list response should contain 0 rides
    And Methods needed to retrieve all rides for passenger were called

  Scenario: Find available ride for driver
    Given Available ride for driver exists
    When Driver search for any available ride
    Then Short ride response should be present and contain ride with status "WAITING_FOR_DRIVER_CONFIRMATION"

  Scenario: Find confirmed ride for passenger
    Given Confirmed ride for passenger exists
    When Passenger search for the confirmed ride
    Then Ride response should be present and contain ride with status one of
      | PENDING |
      | ACTIVE  |

  Scenario: Create ride
    Given Valid request to create a ride
    When Passenger creates ride
    Then Ride response should be present and contain ride with status "WITHOUT_DRIVER"
    And Methods needed to create ride were called

  Scenario: Handle request from another service for updating driver for a ride
    Given Valid request for driver updating and isAvailable is true
    When Service updating driver for a ride
    Then Ride should have status "WAITING_FOR_DRIVER_CONFIRMATION"
    And Methods needed to handle request with existing driver were called

  Scenario: Handle request from another service for updating driver for a ride
    Given Valid request for driver updating and isAvailable is false
    When Service updating driver for a ride
    Then Ride should have status "WITHOUT_DRIVER"
    And Another request to find a driver for a ride was sent