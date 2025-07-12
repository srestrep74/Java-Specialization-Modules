# Distributed Tracing with Zipkin

## Overview

This project implements distributed tracing using Micrometer Tracing (the successor to Spring Cloud Sleuth for Spring Boot 3.x) and Zipkin. This allows you to track requests as they flow through multiple microservices.

## Quick Start

### 1. Start Zipkin

```bash
# From the project root directory
docker-compose up -d
```

This will start Zipkin on port 9411.

### 2. Access Zipkin UI

Open your browser and navigate to: http://localhost:9411

### 3. Start Your Microservices

Start your services in this order:
1. Config Server
2. Eureka Server  
3. Gateway Server
4. Gym Service
5. Workload Service

### 4. Generate Some Traffic

Make some API calls through the gateway to generate traces:

```bash
# Example: Create a training
curl -X POST http://localhost:8080/gym-api/v1/trainings \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "traineeUsername": "john.doe",
    "trainerUsername": "trainer.smith",
    "trainingName": "Morning Workout",
    "trainingDate": "2024-01-15",
    "duration": 60
  }'
```

### 5. View Traces in Zipkin

1. Go to http://localhost:9411
2. Click on "Run Query" to see recent traces
3. Click on any trace to see the detailed breakdown

## Understanding the Traces

### Trace Components

- **Trace ID**: Unique identifier for the entire request flow (similar to our `transactionId`)
- **Span ID**: Unique identifier for each operation within the trace
- **Service Name**: The microservice that generated the span
- **Operation Name**: What the service was doing (e.g., `POST /api/v1/trainings`)
- **Duration**: How long the operation took
- **Tags**: Additional metadata (HTTP status, errors, etc.)

### Example Trace Flow

```
Gateway Server (POST /gym-api/v1/trainings) - 500ms
  └── Gym Service (POST /api/v1/trainings) - 450ms
      ├── Database Query (SELECT trainer) - 50ms
      ├── Database Query (SELECT trainee) - 45ms
      ├── Database Insert (training) - 100ms
      └── Feign Call to Workload Service - 200ms
          └── Workload Service (POST /api/v1/trainers/workload) - 180ms
              ├── Database Query - 40ms
              └── Database Update - 80ms
```

## Configuration

### Sampling Rate

The sampling rate determines what percentage of requests are traced. Currently set to 100% for development:

```yaml
management:
  tracing:
    sampling:
      probability: 1.0  # 100% sampling
```

For production, you might want to reduce this:

```yaml
management:
  tracing:
    sampling:
      probability: 0.1  # 10% sampling
```

### Propagation Type

We're using B3 propagation (Zipkin's native format):

```yaml
micrometer:
  tracing:
    propagation:
      type: B3
```

This ensures trace context is propagated via headers like:
- `X-B3-TraceId`
- `X-B3-SpanId`
- `X-B3-Sampled`

## Integration with Existing Logging

The trace ID from Zipkin is automatically added to your logs via MDC. You'll see it in your log patterns:

```
2024-01-15 10:30:00.123 [http-nio-8080-exec-1] INFO  c.e.service.MyService [3e4afa38] - Processing request
```

The `[3e4afa38]` is both your custom `requestId` and part of the Zipkin trace ID.

## Troubleshooting

### No traces showing in Zipkin

1. Check that Zipkin is running: `docker ps`
2. Verify the endpoint configuration in your services
3. Check service logs for connection errors to Zipkin
4. Ensure all services have the Micrometer Tracing dependencies

### Incomplete traces

1. Make sure all services in the call chain have tracing enabled
2. Check that Feign clients are properly configured for tracing
3. Verify that the propagation type is consistent across all services

### Performance Impact

Tracing adds minimal overhead, but in high-traffic scenarios:
1. Reduce sampling rate
2. Use asynchronous span reporting
3. Consider using a production-grade storage backend for Zipkin (Elasticsearch, Cassandra)

## Advanced Features

### Custom Spans

You can create custom spans in your code:

```java
@Autowired
private Tracer tracer;

public void myMethod() {
    Span span = tracer.nextSpan().name("custom-operation");
    try (Tracer.SpanInScope ws = tracer.withSpanInScope(span.start())) {
        // Your custom operation
        span.tag("custom.tag", "value");
    } finally {
        span.end();
    }
}
```

### Baggage Propagation

You can propagate custom data across services:

```java
@Autowired
private Tracer tracer;

// Set baggage
tracer.createBaggage("user.id", "12345");

// Read baggage in another service
String userId = tracer.getBaggage("user.id");
```

## Production Considerations

1. **Storage**: Use persistent storage (MySQL, Elasticsearch) instead of in-memory
2. **Retention**: Configure data retention policies
3. **Security**: Secure Zipkin UI access
4. **Monitoring**: Monitor Zipkin's health and performance
5. **Sampling**: Adjust sampling rates based on traffic volume 