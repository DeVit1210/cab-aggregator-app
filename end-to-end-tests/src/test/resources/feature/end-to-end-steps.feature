Feature: end-to-end tests

  Scenario: Passenger calculating cost of a ride
    Given Valid request to calculate ride cost
    And Valid promocode is entered for passenger
    When Passenger calculates ride cost
    Then Total ride cost should be lower than base cost

  Scenario: Passenger requesting a car for a ride
    Given Valid request to create a ride
    When Passenger creates ride
    Then Ride should be created and have a status of "WITHOUT_DRIVER"
    And In range of 3 seconds ride status should change to the "WAITING_FOR_DRIVER_CONFIRMATION" status
    And Status of driver assigned to the ride should be "HAS_UNCONFIRMED_RIDE"

  Scenario: Driver dismissing the ride
    Given Available ride for driver with id 1
    When Driver dismisses the ride
    Then Ride status should change to the "WITHOUT_DRIVER" status
    And Previous driver status should be set to "AVAILABLE"
    And In range of 3 seconds ride status should change to the "WAITING_FOR_DRIVER_CONFIRMATION" status
    And Another driver should be assigned to the ride
    And Status of driver assigned to the ride should be "HAS_UNCONFIRMED_RIDE"

  Scenario: Driver accepting the ride
    Given Available ride for driver with id 2
    When Driver accepts the ride
    Then Ride status should change to the "PENDING" status
    And Status of driver assigned to the ride should be "ON_WAY_TO_PASSENGER"
    And Passenger should have confirmed ride

  Scenario: Driver starting the ride
    Given Confirmed ride for passenger with id 1
    When Driver starts the ride
    Then Ride status should change to the "ACTIVE" status
    And Status of driver assigned to the ride should be "ON_TRIP"

  Scenario: Driver finishing the ride
    Given Confirmed ride for passenger with id 1
    And Valid request to finish the ride
    And Valid promocode is entered for passenger
    When Driver finishes the ride
    Then Ride status should change to the "FINISHED" status
    And Ride should be paid
    And Promocode application should be confirmed
    And Balance on driver's account should increase
    And Status of driver assigned to the ride should be "AVAILABLE"

  Scenario: Passenger rates the rid
    Given Finished ride for passenger with id 1
    And Valid request from passenger to rate driver
    When Passenger rates driver after ride
    Then Rating should be created and driver rating count should increase
