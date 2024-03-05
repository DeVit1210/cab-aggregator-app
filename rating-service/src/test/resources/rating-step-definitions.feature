Feature: rating-service component tests

  Scenario: Find rating by id
    Given Rating exists in the database
    When Business logic for retrieving rating from database is invoked
    Then Rating response should be present and contain rating
    And Methods needed to find rating by id should be called

  Scenario: Get all ratings for person
    Given 3 ratings for person exists in the database
    When Business logic for retrieving ratings for person is invoked
    Then Rating list should has the size of 3
    And Methods needed to get all ratings for person should be called

  Scenario: Get all ratings for person
    Given No ratings for person exists in the database
    When Business logic for retrieving ratings for person is invoked
    Then Empty rating list should be returned

  Scenario: Get average rating for person
    Given Rating with values for person exist into database
      | THREE |
      | FOUR  |
      | FIVE  |
    When Business logic for retrieving average rating is invoked
    Then The average rating with rating quantity of 3 and value of 4.00 should be returned

  Scenario: Get average rating for person
    Given No ratings for person exists in the database
    When Business logic for retrieving average rating is invoked
    Then The average rating with rating quantity of 0 and value of 5.00 should be returned

  Scenario: Create rating for driver
    Given No rating for ride for "DRIVER" exist
    When Business logic for creating a rating for "DRIVER" with rating value of "FOUR" is invoked
    Then Rating response should contain created rating for "DRIVER" with rating value of 4
    And Methods needed to create rating should be called

  Scenario: Create rating for passenger
    Given No rating for ride for "PASSENGER" exist
    When Business logic for creating a rating for "PASSENGER" with rating value of "THREE" is invoked
    Then Rating response should contain created rating for "PASSENGER" with rating value of 3
    And Methods needed to create rating should be called

  Scenario: Update rating for person
    Given Rating for ride with rating value of "THREE" exists into database
    When Business logic for updating rating with new rating value of "FIVE" is invoked
    Then Rating response should contain updated rating with rating value of 5
    And Methods needed to update rating value should be called