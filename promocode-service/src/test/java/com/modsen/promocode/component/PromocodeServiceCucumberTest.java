package com.modsen.promocode.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/promocodes-step-definitions.feature",
        glue = "com.modsen.promocode.component",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class PromocodeServiceCucumberTest {
}
