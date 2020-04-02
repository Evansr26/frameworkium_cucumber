
@test
Feature: Navigation examples using BDD and Frameworkium

 @example
  Scenario Outline: Navigate to <link> and verify title is <title>
    Given I am on the Ten10 homepage
    When I click <link>
    Then The title matches <title>

    Examples:
      | link     | title                                 |
      | JOIN     | The Application Process - Apply Today |
      | SERVICES | Software Testing Services             |
      | SERVICES | The Application Process - Apply Today |