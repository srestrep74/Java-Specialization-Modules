# SigNoz Observability Setup Guide

## 🚀 Quick Start

### 1. Start SigNoz Stack
```bash
# Start SigNoz and all observability components
docker-compose -f docker-compose-observability.yml up -d

# Wait for all services to be healthy (about 1-2 minutes)
docker-compose -f docker-compose-observability.yml ps
```

### 2. Access SigNoz UI
Open your browser and navigate to: **http://localhost:3301**

### 3. Start Your Microservices
Make sure your microservices are running with the updated configurations from the config-server.

## 📊 What You'll See in SigNoz

### **Traces Tab**
- Distributed traces showing the complete request flow
- Each trace shows the journey through: Gateway → Gym Service → Workload Service
- Click on any trace to see detailed timing and the `requestId`
- Filter traces by service, operation, or custom attributes

### **Metrics Tab**
- JVM metrics (memory, GC, threads)
- HTTP request metrics (latency, throughput, error rate)
- Custom business metrics
- System metrics (CPU, memory usage)

### **Logs Tab**
- Centralized logs from all services
- **Filter by `requestId`** to see all logs for a specific transaction
- Search logs by service name, log level, or content
- Correlate logs with traces

## 🔍 Key Features to Try

### 1. **Trace a Request End-to-End**
1. Make a request to create a training through the gateway
2. Go to Traces tab in SigNoz
3. Find your request and click on it
4. You'll see:
   - Total request duration
   - Time spent in each service
   - Database queries
   - Any errors or slow operations

### 2. **Filter Logs by Request ID**
1. Copy a `requestId` from your logs (e.g., `9d24f8c0`)
2. Go to Logs tab
3. Add filter: `requestId = "9d24f8c0"`
4. See all logs across all services for that specific request

### 3. **Monitor Service Health**
1. Go to Metrics tab
2. Select a service (e.g., `gym-service`)
3. View:
   - Request rate
   - Error rate
   - P99 latency
   - JVM memory usage

## 🛠️ Troubleshooting

### If services don't appear in SigNoz:
1. Check that the OpenTelemetry Collector is running:
   ```bash
   docker logs signoz-otel-collector
   ```

2. Verify your services can reach the collector:
   ```bash
   curl http://localhost:4318/v1/traces
   ```

3. Check service logs for any OTLP export errors

### If traces are incomplete:
1. Ensure all services have the same `X-Request-ID` propagation
2. Check that Feign clients are configured with the interceptor
3. Verify sampling is set to 1.0 (100%)

## 📝 Adding Custom Attributes

To add custom attributes to your traces, use:

```java
import io.opentelemetry.api.trace.Span;

Span currentSpan = Span.current();
currentSpan.setAttribute("user.id", userId);
currentSpan.setAttribute("order.amount", orderAmount);
```

These attributes will be searchable in SigNoz.

## 🎯 Best Practices

1. **Use structured logging** with your `requestId`:
   ```java
   log.info("Processing order | orderId: {} | userId: {}", orderId, userId);
   ```

2. **Add meaningful span names** for better trace readability

3. **Set appropriate log levels** to avoid noise:
   - ERROR: For actual errors
   - WARN: For potential issues
   - INFO: For important business events
   - DEBUG: For detailed troubleshooting

## 🛑 Stopping SigNoz

```bash
# Stop all containers
docker-compose -f docker-compose-observability.yml down

# Stop and remove all data
docker-compose -f docker-compose-observability.yml down -v
```

## 📚 Additional Resources

- [SigNoz Documentation](https://signoz.io/docs/)
- [OpenTelemetry Spring Boot](https://opentelemetry.io/docs/instrumentation/java/automatic/spring-boot/)
- [SigNoz Troubleshooting Guide](https://signoz.io/docs/troubleshooting/) 