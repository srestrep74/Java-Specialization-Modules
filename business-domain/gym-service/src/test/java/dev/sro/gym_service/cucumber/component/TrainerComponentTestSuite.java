package dev.sro.gym_service.cucumber.component;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/component/trainer")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/trainer-component-report.html, json:target/cucumber-reports/trainer-component-report.json")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "dev.sro.gym_service.cucumber.component,dev.sro.gym_service.cucumber.component.hooks")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @ignore")
public class TrainerComponentTestSuite {
} 