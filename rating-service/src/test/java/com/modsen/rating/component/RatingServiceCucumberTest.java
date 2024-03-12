package com.modsen.rating.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/rating-step-definitions.feature",
        glue = "com.modsen.rating.component",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class RatingServiceCucumberTest {
}