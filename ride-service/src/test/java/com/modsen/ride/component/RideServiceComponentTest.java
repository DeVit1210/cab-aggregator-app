package com.modsen.ride.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/feature",
        glue = "com.modsen.ride.component",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class RideServiceComponentTest {
}