package com.modsen.driver.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/driver-step-definitions.feature",
        glue = "com.modsen.driver.component",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class DriverServiceCucumberTest {
}