Feature: Ride operations component tests

  Scenario: Accept ride
    Given Valid current ride status of "WAITING_FOR_DRIVER_CONFIRMATION"
    When Driver accepts the ride
    Then Ride response should be present and contain a ride with status "PENDING"
    And Request to change driver status to "ON_WAY_TO_PASSENGER" is sent

  Scenario: Dismiss ride
    Given Valid current ride status of "WAITING_FOR_DRIVER_CONFIRMATION"
    When Driver dismisses the ride
    Then Ride response should be present and contain a ride with status "WITHOUT_DRIVER"
    And Request to change driver status to "AVAILABLE" is sent
    And Request to find another driver for a ride was sent

  Scenario: Notifying passenger about driver waiting
    Given Valid current ride status of "PENDING"
    When Driver notifies passenger about waiting
    Then Ride response should be present and contain a ride with status "PENDING"
    And Request to change driver status to "WAITING_FOR_PASSENGER" is sent

  Scenario: Cancel ride
    Given Valid current ride status one of
      | WITHOUT_DRIVER                  |
      | WAITING_FOR_DRIVER_CONFIRMATION |
      | PENDING                         |
    When Passenger cancel the ride
    Then Ride response should be present and contain a ride with status "CANCELED"
    And Request to change driver status to "AVAILABLE" is sent

  Scenario: Start ride
    Given Valid current ride status of "PENDING"
    When Driver starts the ride
    Then Ride response should be present and contain a ride with status "ACTIVE"
    And Request to change driver status to "ON_TRIP" is sent

  Scenario: Finish ride
    Given Valid current ride status of "ACTIVE"
    And Valid request to finish ride
    When Driver finishes the ride
    Then Payment processed
    And Promocode appliance attempted
    And Ride response should be present and contain a ride with status "FINISHED"
    And Request to change driver status to "AVAILABLE" is sent