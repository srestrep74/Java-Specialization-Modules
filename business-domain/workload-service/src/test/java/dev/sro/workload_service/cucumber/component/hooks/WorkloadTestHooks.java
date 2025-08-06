package dev.sro.workload_service.cucumber.component.hooks;

import dev.sro.workload_service.cucumber.component.WorkloadTestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkloadTestHooks {

    @Autowired
    private WorkloadTestContext testContext;

    @Before
    public void setUp() {
        testContext.clear();
    }

    @After
    public void tearDown() {
        testContext.clear();
    }
} 