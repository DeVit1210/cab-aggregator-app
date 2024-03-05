Feature: promocode-service component tests

  Scenario: apply promocode
    Given Valid apply promocode request
    When Business logic for promocode appliance is invoked
    Then AppliedPromocodeResponse should be present and have the "NOT_CONFIRMED" status
    And Methods needed to apply promocode were called

  Scenario: find not confirmed promocode
    Given Valid passenger id
    When Business logic for not confirmed promocode retrieval is invoked
    Then AppliedPromocodeResponse should be present and have the "NOT_CONFIRMED" status
    And Methods needed to find not confirmed promocode were invoked

  Scenario: confirm promocode appliance
    Given Valid promocode id
    When Business logic for promocode appliance confirming is invoked
    Then AppliedPromocodeResponse should be present and have the "CONFIRMED" status
    And Methods needed to confirm promocode were invoked