package com.upc.aventurape.platform.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.upc.aventurape.platform.bdd",
        plugin = {"pretty", "html:target/cucumber-report.html"},
        tags = "not @ignore"
)
public class CucumberRunner {
    // Esta clase actúa como punto de entrada para ejecutar pruebas de Cucumber
} 