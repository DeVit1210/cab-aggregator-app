package com.modsen.payment.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/payment-step-definitions.feature",
        glue = "com.modsen.payment.component",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class PaymentServiceCucumberTest {
}