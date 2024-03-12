package com.modsen.e2e.config;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/feature/end-to-end-steps.feature",
        glue = "com.modsen.e2e.config",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class EndToEndTest {
}
