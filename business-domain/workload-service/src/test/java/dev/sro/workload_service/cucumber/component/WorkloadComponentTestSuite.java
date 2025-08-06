package dev.sro.workload_service.cucumber.component;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/component/workload")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "dev.sro.workload_service.cucumber.component")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/workload-component-tests.html, json:target/cucumber-reports/workload-component-tests.json")
public class WorkloadComponentTestSuite {
} 