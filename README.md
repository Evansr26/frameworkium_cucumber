To execute tests:

```mvn clean verify allure:serve -Dbrowser=chrome -Dheadless=false -Dcucumber.options="--tags @example" -Denv=test```

Environment parameters can be updated in:

``` src/test/resources/config.properties ```