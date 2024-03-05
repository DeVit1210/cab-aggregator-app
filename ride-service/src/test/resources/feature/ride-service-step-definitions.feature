Feature: ride-service component tests

  Scenario: Find all rides for person
    Given 3 finished rides exists for person
    When Business logic to retrieve all rides for "DRIVER" is invoked
    Then Ride list response should contain 3 rides
    And Methods needed to retrieve all rides for driver were called

  Scenario: Find all rides for person
    Given No finished rides for person
    When Business logic to retrieve all rides for "PASSENGER" is invoked
    Then Ride list response should contain 0 rides
    And Methods needed to retrieve all rides for passenger were called

  Scenario: Find available ride for driver
    Given Available ride for driver exists
    When Business logic to find available ride for driver is invoked
    Then Short ride response should be present and contain ride with status "WAITING_FOR_DRIVER_CONFIRMATION"

  Scenario: Find confirmed ride for passenger
    Given Confirmed ride for passenger exists
    When Business logic to find confirmed ride for passenger exists
    Then Ride response should be present and contain ride with status one of
      | PENDING |
      | ACTIVE  |

  Scenario: Create ride
    Given Valid request to create a ride
    When Business logic for ride creating is invoked
    Then Ride response should be present and contain ride with status "WITHOUT_DRIVER"
    And Methods needed to create ride were called

  Scenario: Handle request from another service for updating driver for a ride
    Given Valid request for driver updating and isAvailable is true
    When Business logic for updating driver for a ride is invoked
    Then Ride should have status "WAITING_FOR_DRIVER_CONFIRMATION"
    And Methods needed to handle request with existing driver were called

  Scenario: Handle request from another service for updating driver for a ride
    Given Valid request for driver updating and isAvailable is false
    When Business logic for updating driver for a ride is invoked
    Then Ride should have status "WITHOUT_DRIVER"
    And Another request to find a driver for a ride was sent