Feature: Ride operations component tests

  Scenario: Accept ride
    Given Valid current ride status of "WAITING_FOR_DRIVER_CONFIRMATION"
    When Business logic for accepting ride is invoked
    Then Ride response should be present and contain a ride with status "PENDING"
    And Request to change driver status to "ON_WAY_TO_PASSENGER" is sent

  Scenario: Dismiss ride
    Given Valid current ride status of "WAITING_FOR_DRIVER_CONFIRMATION"
    When Business logic for dismissing ride is invoked
    Then Ride response should be present and contain a ride with status "WITHOUT_DRIVER"
    And Request to change driver status to "AVAILABLE" is sent
    And Request to find another driver for a ride was sent

  Scenario: Notifying passenger about driver waiting
    Given Valid current ride status of "PENDING"
    When Business logic for notifying passenger about driver waiting is invoked
    Then Ride response should be present and contain a ride with status "PENDING"
    And Request to change driver status to "WAITING_FOR_PASSENGER" is sent

  Scenario: Cancel ride
    Given Valid current ride status one of
      | WITHOUT_DRIVER                  |
      | WAITING_FOR_DRIVER_CONFIRMATION |
      | PENDING                         |
    When Business logic for canceling ride is invoked
    Then Ride response should be present and contain a ride with status "CANCELED"
    And Request to change driver status to "AVAILABLE" is sent

  Scenario: Start ride
    Given Valid current ride status of "PENDING"
    When Business logic for ride starting is invoked
    Then Ride response should be present and contain a ride with status "ACTIVE"
    And Request to change driver status to "ON_TRIP" is sent

  Scenario: Finish ride
    Given Valid current ride status of "ACTIVE"
    And Valid request to finish ride
    When Business logic for ride finishing is invoked
    Then Payment processed
    And Promocode appliance attempted
    And Ride response should be present and contain a ride with status "FINISHED"
    And Request to change driver status to "AVAILABLE" is sent