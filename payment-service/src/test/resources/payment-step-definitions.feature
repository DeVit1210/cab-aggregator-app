Feature: payment-service component tests

  Scenario: Create credit card for passenger
    Given Valid request for credit card creating for "PASSENGER"
    When Business logic for credit card creating is invoked
    Then Credit card response should be present and contain created credit card
    And Methods needed to create passenger's credit card were called

  Scenario: Create credit card for driver
    Given Valid request for credit card creating for "DRIVER"
    When Business logic for credit card creating is invoked
    Then Credit card response should be present and contain created credit card
    And Methods needed to create driver's credit card were called

  Scenario: Find all credit cards for person
    Given 3 credit cards exist for person
    When Business logic to retrieve credit cards for person is invoked
    Then Response should contain 3 credit cards
    And Methods needed to retrieve all credit cards for person were called

  Scenario: Find all credit cards for person
    Given No credit cards exist for person
    When Business logic to retrieve credit cards for person is invoked
    Then Response should contain 0 credit cards

  Scenario: Set new default card
    Given Current default card with id 1
    When Business logic to set new default card with id 2 is invoked
    Then Response should contain credit card with id 2
    And Methods needed to set new default card were called

  Scenario: Get default credit card
    Given Credit card exists for person
    When Business logic to retrieve default credit card is invoked
    Then Response should be present and contain default credit card
    And Methods needed to retrieve default credit card were called

  Scenario: Create payment
    Given Valid payment with amount of 10.00 request and ride has not been paid yet
    When Business logic to create payment is invoked
    Then Payment response should be present and contain created payment with amount of 10.00
    And Methods needed to create payment were called

  Scenario: Get payment for a ride
    Given Ride with id 100 has already been paid
    When Business logic to retrieve payment for a ride with id 100 is invoked
    Then Payment response should be present and contain payment for a ride with id 100
    And Methods needed to get payment for a ride were called

  Scenario: Get all payments for a passenger
    Given 3 payments exist for passenger
    When Business logic to retrieve all payments for a passenger is invoked
    Then Payment response should contain 3 payments
    And Methods needed to get all payments for a passenger were called