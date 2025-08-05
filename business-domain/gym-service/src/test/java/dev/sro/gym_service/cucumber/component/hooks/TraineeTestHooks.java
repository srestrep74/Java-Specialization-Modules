package dev.sro.gym_service.cucumber.component.hooks;

import dev.sro.gym_service.cucumber.component.TraineeTestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.springframework.beans.factory.annotation.Autowired;

public class TraineeTestHooks {

    @Autowired
    private TraineeTestContext testContext;

    @Before
    public void setUp(Scenario scenario) {
        testContext.clear();

        System.out.println("Starting scenario: " + scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        testContext.clear();

        if (scenario.isFailed()) {
            System.out.println("Scenario failed: " + scenario.getName());
        } else {
            System.out.println("Scenario passed: " + scenario.getName());
        }
    }
}