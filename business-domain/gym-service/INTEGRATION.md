# Gym Service - Workload Service Integration

## Overview

The Gym Service automatically notifies the Workload Service whenever training sessions are planned, updated, or cancelled. This integration ensures that trainer workload data is kept up-to-date across both services.

## Architecture

### Communication Flow

```
Gym Service ---> Feign Client ---> Workload Service
     |                                    |
     |            (HTTP REST)             |
     |                                    |
 Training CRUD  <-- Response <-- Workload Processing
```

### Components

1. **WorkloadServiceClient** - Feign Client interface for HTTP communication
2. **WorkloadNotificationService** - Service layer that handles workload notifications
3. **TrainingServiceImpl** - Modified to send notifications on training operations
4. **Circuit Breaker** - Resilience4j for fault tolerance

## Integration Points

### Training Creation
When a training session is created:
```java
@Override
public void save(CreateTrainingRequest request) {
    // ... save training logic ...
    workloadNotificationService.notifyTrainingCreated(savedTraining);
}
```

**Workload Service Call:**
- **Endpoint:** `POST /api/v1/trainers/workload`
- **Action:** `ADD`
- **Data:** Trainer info + training date + duration

### Training Update
When a training session is updated:
```java
@Override
public TrainingSummaryResponse update(UpdateTrainingRequest request) {
    // ... update training logic ...
    workloadNotificationService.notifyTrainingCreated(savedTraining);
}
```

**Note:** Updates are treated as new training sessions for workload calculation.

### Training Deletion
When a training session is deleted:
```java
@Override
public void deleteById(Long id) {
    Training training = trainingRepository.findById(id).orElseThrow(...);
    trainingRepository.deleteById(id);
    workloadNotificationService.notifyTrainingDeleted(training);
}
```

**Workload Service Call:**
- **Endpoint:** `POST /api/v1/trainers/workload`
- **Action:** `DELETE`
- **Data:** Trainer info + training date + duration

## Data Mapping

### TrainerWorkloadRequest
```json
{
  "trainerUsername": "john.doe",
  "trainerFirstName": "John",
  "trainerLastName": "Doe",
  "isActive": true,
  "trainingDate": "2024-01-15",
  "trainingDuration": 60,
  "actionType": "ADD"
}
```

### TrainerWorkloadResponse
```json
{
  "message": "Trainer workload processed successfully",
  "success": true
}
```

## Fault Tolerance

### Circuit Breaker Configuration
```yaml
resilience4j:
  circuitbreaker:
    instances:
      workload-service:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 5s
        failureRateThreshold: 50
```

### Fallback Strategy
When the Workload Service is unavailable:
1. **Circuit breaker opens** after failure threshold
2. **Fallback method executes** instead of making HTTP calls
3. **Gym Service continues** normal operation
4. **Workload updates are lost** but training operations succeed

### Error Handling
```java
try {
    TrainerWorkloadResponse response = workloadServiceClient.processTrainerWorkload(request);
    // Log success or warning based on response
} catch (Exception e) {
    log.error("Failed to notify workload service: {}", e.getMessage(), e);
    // Continue with gym service operation - don't fail the main transaction
}
```

## Configuration

### Gym Service Configuration
```yaml
# Local fallback configuration
workload-service:
  url: http://localhost:8081

feign:
  client:
    config:
      workload-service:
        connectTimeout: 5000
        readTimeout: 10000
        loggerLevel: basic
  circuitbreaker:
    enabled: true
```

### Config Server Configuration
```yaml
# Production configuration via config server
workload-service:
  url: http://workload-service:8081
```

## Monitoring

### Health Checks
- Circuit breaker status: `/actuator/health`
- Metrics: `/actuator/metrics`
- Custom health indicator for workload service connectivity

### Logging
- **INFO:** Successful workload notifications
- **WARN:** Workload service returns failure response
- **ERROR:** Communication failures with workload service

## Testing Integration

### Unit Tests
```java
@Test
void shouldNotifyWorkloadServiceOnTrainingCreation() {
    // Given
    CreateTrainingRequest request = createValidRequest();
    
    // When
    trainingService.save(request);
    
    // Then
    verify(workloadNotificationService).notifyTrainingCreated(any(Training.class));
}
```

### Integration Tests
```java
@Test
void shouldHandleWorkloadServiceFailureGracefully() {
    // Given workload service is down
    when(workloadServiceClient.processTrainerWorkload(any()))
        .thenThrow(new FeignException.ServiceUnavailable("Service down", null));
    
    // When
    assertDoesNotThrow(() -> trainingService.save(request));
    
    // Then training should still be saved
    assertThat(trainingRepository.findAll()).hasSize(1);
}
```

## Deployment Considerations

1. **Service Discovery:** Both services should be registered with Eureka
2. **Load Balancing:** Use service name instead of direct URL in production
3. **Security:** Add authentication headers if required
4. **Monitoring:** Set up alerts for circuit breaker state changes
5. **Data Consistency:** Consider implementing retry mechanism for critical workload updates

## Troubleshooting

### Common Issues

1. **Workload Service Not Available**
   - Check service health: `curl http://workload-service:8081/actuator/health`
   - Verify network connectivity
   - Check circuit breaker state

2. **Configuration Issues**
   - Verify `workload-service.url` property
   - Check Feign client configuration
   - Validate config server connectivity

3. **Authentication Failures**
   - Ensure proper service-to-service authentication
   - Check JWT token propagation if using security

### Debug Commands
```bash
# Check gym-service health
curl http://gym-service:8080/actuator/health

# Check workload-service connectivity
curl http://workload-service:8081/actuator/health

# View circuit breaker metrics
curl http://gym-service:8080/actuator/metrics/resilience4j.circuitbreaker.state
``` 