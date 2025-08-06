package dev.sro.gym_service.cucumber.integration.config;

public class ActiveMQFailureSimulator {

    private final ActiveMQTestcontainersConfig activeMQConfig;

    public ActiveMQFailureSimulator(ActiveMQTestcontainersConfig activeMQConfig) {
        this.activeMQConfig = activeMQConfig;
    }

    private boolean failureMode = false;

    public void simulateActiveMQFailure() {
        this.failureMode = true;

        try {
            if (activeMQConfig.isContainerRunning()) {
                activeMQConfig.stopContainer();
            }
        } catch (Exception e) {
        }
    }

    public void restoreActiveMQ() {
        this.failureMode = false;

        try {
            if (!activeMQConfig.isContainerRunning()) {
                activeMQConfig.startContainer();
            }
        } catch (Exception e) {
        }
    }

    public boolean isActiveMQAvailable() {
        return !failureMode && activeMQConfig.isContainerRunning();
    }

    public boolean isInFailureMode() {
        return failureMode;
    }
}