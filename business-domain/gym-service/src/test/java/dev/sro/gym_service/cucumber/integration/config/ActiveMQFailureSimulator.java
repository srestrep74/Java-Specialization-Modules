package dev.sro.gym_service.cucumber.integration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ActiveMQFailureSimulator {

    @Autowired
    private ActiveMQTestcontainersConfig activeMQConfig;

    private boolean failureMode = false;

    public void simulateActiveMQFailure() {
        log.warn("Simulating ActiveMQ failure mode");
        this.failureMode = true;
        
        // Also try to stop the container as a backup
        try {
            if (activeMQConfig.isContainerRunning()) {
                activeMQConfig.stopContainer();
                log.info("ActiveMQ container stopped for failure simulation");
            }
        } catch (Exception e) {
            log.error("Error stopping ActiveMQ container: {}", e.getMessage());
        }
    }

    public void restoreActiveMQ() {
        log.info("Restoring ActiveMQ from failure mode");
        this.failureMode = false;
        
        // Also try to restart the container as a backup
        try {
            if (!activeMQConfig.isContainerRunning()) {
                activeMQConfig.startContainer();
                log.info("ActiveMQ container restarted successfully");
            }
        } catch (Exception e) {
            log.error("Error restarting ActiveMQ container: {}", e.getMessage());
        }
    }

    public boolean isActiveMQAvailable() {
        return !failureMode && activeMQConfig.isContainerRunning();
    }

    public boolean isInFailureMode() {
        return failureMode;
    }
} 