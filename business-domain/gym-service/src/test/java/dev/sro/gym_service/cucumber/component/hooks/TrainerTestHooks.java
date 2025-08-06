package dev.sro.gym_service.cucumber.component.hooks;

import dev.sro.gym_service.cucumber.component.context.TrainerTestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class TrainerTestHooks {

    @Autowired
    private TrainerTestContext testContext; 

    @Before
    public void setUp() {
        testContext.clear();
    }

    @After
    public void tearDown() {
        testContext.clear();
    }
}