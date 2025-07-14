# Gym Service Microservice

A comprehensive gym management microservice built with Spring Boot, implementing enterprise-level patterns for managing trainers, trainees, training sessions, and user authentication in a distributed system architecture.

## 🏗️ Service Overview

The **Gym Service** is the core business domain microservice responsible for:

- **User Management**: Registration and authentication of trainers and trainees
- **Trainer Management**: CRUD operations, profile management, and availability tracking
- **Trainee Management**: Profile management, trainer assignments, and training history
- **Training Sessions**: Scheduling, management, and tracking of training sessions
- **Training Types**: Management of available training specializations
- **Authentication & Authorization**: JWT-based security with role-based access control
- **Workload Integration**: Synchronous communication with Workload Service for trainer workload tracking

## 🎯 Business Domain

### Core Entities
- **User**: Base entity for authentication (username, password, roles)
- **Trainer**: Professional gym instructors with specializations and availability
- **Trainee**: Gym members with assigned trainers and training goals
- **Training**: Individual training sessions between trainers and trainees
- **TrainingType**: Available training specializations (e.g., Fitness, Yoga, Cardio)
- **PendingWorkload**: Outbox pattern entity for reliable workload communication

### Key Business Rules
- Each trainee can have multiple trainers
- Trainers have specific specializations (training types)
- Training sessions are scheduled between active trainers and trainees
- Workload tracking is automatically updated when training sessions are created/modified
- Failed workload communications are stored and retried automatically
- User accounts can be activated/deactivated
- Password generation follows specific patterns for new users

## 🛠️ Infrastructure & Dependencies

### Database Layer
- **Primary Database**: PostgreSQL
  - Port: `5432`
  - Database: `jpa_epam`
  - Connection Pool: HikariCP
  - JPA/Hibernate for ORM
- **Administration**: pgAdmin (port `5050`)

### Caching Layer
- **Redis**: JWT token storage and session management
  - Port: `6379`
  - Purpose: Blacklisted tokens, session storage
  - Administration: RedisInsight (port `5540`)

### Service Discovery
- **Eureka Client**: Registers with Eureka Server
  - Server URL: `http://localhost:8761/eureka/`
  - Instance ID: Dynamic with random values
  - Health checks and heartbeat configuration

### Configuration Management
- **Spring Cloud Config Client**: External configuration
  - Config Server: `http://localhost:8888`
  - Profile-based configurations (dev, prod, local)

## 🔄 Circuit Breaker Pattern Implementation

### Overview
The Circuit Breaker pattern is implemented using **Resilience4j** to provide fault tolerance when communicating with the Workload Service. This pattern prevents cascade failures and provides graceful degradation.

### How Circuit Breaker Works

The circuit breaker operates in three states:

1. **CLOSED** (Normal Operation)
   - All requests pass through to the Workload Service
   - Success/failure rates are monitored
   - If failure rate exceeds threshold, transitions to OPEN

2. **OPEN** (Failure State)
   - All requests are immediately rejected
   - Fallback method is executed (Outbox pattern activated)
   - After wait duration, transitions to HALF_OPEN

3. **HALF_OPEN** (Recovery Testing)
   - Limited number of test requests are allowed
   - If successful, transitions back to CLOSED
   - If failed, returns to OPEN state

### Circuit Breaker Configuration Details

The circuit breaker is configured with specific thresholds and timing parameters:
- **Sliding Window Size**: 10 requests for failure rate calculation
- **Minimum Calls**: 5 calls required before state evaluation
- **Failure Rate Threshold**: 50% failure rate triggers circuit opening
- **Wait Duration**: 10 seconds before attempting recovery
- **Half-Open Test Calls**: 3 permitted calls for testing recovery
- **Slow Call Threshold**: 80% slow calls with 5-second duration limit
- **Timeout Duration**: 15 seconds for individual requests

## 🌐 Feign Client Communication & Outbox Pattern

### Primary Feign Client Communication
**OpenFeign** provides declarative HTTP client communication with the Workload Service through the `WorkloadServiceClient` interface. This client is configured with specific timeouts, connection settings, and integrates seamlessly with the circuit breaker pattern.

### Outbox Pattern Implementation

The service implements the **Outbox Pattern** to ensure reliable communication with the Workload Service, guaranteeing that workload updates are eventually delivered even when the target service is temporarily unavailable.

#### How the Communication Flow Works

1. **Normal Transaction Processing**: When a training session is created, updated, or deleted, the business logic performs the main database transaction (saving/updating the Training entity)

2. **Immediate Feign Client Call**: After the successful database transaction, the `WorkloadNotificationService` immediately attempts to notify the Workload Service using the `WorkloadServiceClient` (primary Feign client)

3. **Circuit Breaker & Fallback Activation**: If the Workload Service is unavailable, times out, or the circuit breaker is OPEN, the `WorkloadServiceClientFallback` is automatically triggered

4. **Fallback Storage (Outbox Pattern)**: The fallback mechanism stores the workload data in the `PendingWorkload` table (outbox), ensuring no data is lost

5. **Scheduled Retry Processing**: A background process (`WorkloadRelayService`) runs every 2 minutes, scanning the outbox table and attempting to process all pending workload entries using a separate Feign client (`WorkloadRelayClient`)

#### Detailed Communication Flow

**Scenario 1: Successful Communication**
- Training operation completes in database
- `WorkloadNotificationService` calls `WorkloadServiceClient` immediately
- Workload Service responds successfully
- No outbox entry is created
- Operation is complete

**Scenario 2: Failed Communication with Fallback**
- Training operation completes in database
- `WorkloadNotificationService` calls `WorkloadServiceClient` immediately
- Call fails (service down, timeout, circuit breaker open)
- `WorkloadServiceClientFallback` is automatically executed
- Fallback saves workload data to `PendingWorkload` table (outbox)
- User operation completes successfully (non-blocking)
- Background service will retry later

**Scenario 3: Recovery Processing**
- `WorkloadRelayService` runs every 2 minutes
- Scans `PendingWorkload` table for pending entries
- Uses `WorkloadRelayClient` to attempt sending each pending workload
- Successfully processed entries are removed from outbox
- Failed attempts remain for next retry cycle

#### Key Components in the Flow

**WorkloadServiceClient (Primary Client)**
- Main Feign client for immediate workload notifications
- Configured with circuit breaker and fallback mechanism
- Used during normal business operations
- Directly integrated with the main transaction flow

**WorkloadServiceClientFallback (Fallback Handler)**
- Automatically activated when primary client fails
- Stores workload data in the outbox table (PendingWorkload)
- Handles duplicate prevention through unique constraints
- Provides graceful degradation without blocking business operations
- Logs critical errors if outbox storage fails

**WorkloadNotificationService (Orchestrator)**
- Coordinates workload notifications for training operations
- Handles different action types (ADD, UPDATE, DELETE)
- Manages complex update scenarios (date/duration changes)
- Provides abstraction layer for workload communications

**WorkloadRelayClient (Retry Client)**
- Separate Feign client used exclusively for retry operations
- Processes pending workloads from the outbox table
- Operates independently of the main business flow
- Used by the scheduled background service

**WorkloadRelayService (Background Processor)**
- Scheduled service running every 2 minutes
- Processes all pending workloads in the outbox
- Removes successfully processed entries
- Maintains retry persistence for failed attempts

#### PendingWorkload Entity (Outbox Table)

The outbox table stores all necessary information to reconstruct and replay the workload request:
- **Trainer Information**: Username, first name, last name, and active status
- **Training Details**: Training date and duration
- **Action Type**: Operation type (ADD, DELETE, UPDATE)
- **Metadata**: Creation timestamp for monitoring and audit purposes
- **Unique Constraints**: Prevents duplicate entries for the same trainer/date/action combination

#### Benefits of This Implementation

1. **Guaranteed Delivery**: Eventually consistent communication between services
2. **Fault Tolerance**: Handles temporary network failures and service outages gracefully
3. **Data Consistency**: Maintains transactional integrity within the Gym Service domain
4. **Non-Blocking Operations**: Business operations continue even when external service is down
5. **Monitoring Capability**: Provides visibility into failed communications through the outbox table
6. **Performance Optimization**: Immediate attempts for fast communication, background retry for resilience
7. **Idempotency Protection**: Unique constraints prevent duplicate workload processing
8. **Audit Trail**: Maintains history of communication attempts with timestamps

#### Retry Strategy Details

- **Frequency**: Fixed rate execution every 2 minutes
- **Persistence**: Failed attempts remain in the outbox for continuous retry
- **Error Handling**: Individual failures don't stop processing of other pending workloads
- **Success Cleanup**: Automatically removes successfully processed workloads
- **Monitoring**: Tracks retry attempts and success rates through comprehensive logging

#### Integration with Circuit Breaker

The Outbox pattern works seamlessly with the Circuit Breaker to provide multiple layers of resilience:

1. **Circuit CLOSED State**: Normal Feign client communication with immediate success
2. **Circuit OPEN State**: All requests immediately trigger fallback and go to outbox storage
3. **Circuit HALF_OPEN State**: Some requests attempt direct communication, failures trigger fallback
4. **Background Processing**: WorkloadRelayService continues processing regardless of circuit breaker state

This implementation ensures that business operations are never blocked by external service availability, while guaranteeing eventual delivery of all workload updates through the outbox pattern.

## 📊 Monitoring & Observability

### Health Checks
- **Database Health**: PostgreSQL connection status and query performance
- **Redis Health**: Cache connectivity and response times
- **Eureka Health**: Service discovery registration status
- **Circuit Breaker Health**: Workload service communication status and state transitions
- **Outbox Health**: Pending workloads count and processing status

### Metrics Collection
The service collects comprehensive metrics for business operations and technical performance:
- **Training Metrics**: Creation rates, duration distributions, and scheduling patterns
- **Authentication Metrics**: Login attempts, success rates, and token generation
- **Circuit Breaker Metrics**: State transitions, failure rates, and response times
- **Outbox Metrics**: Pending workload counts, processing success rates, and retry frequencies
- **Database Metrics**: Connection pool utilization, query performance, and transaction rates

### Distributed Tracing
- **Zipkin Integration**: End-to-end request tracing across service boundaries
- **Correlation IDs**: Request tracking through synchronous and asynchronous processing
- **B3 Propagation**: Trace context propagation for distributed system visibility
- **Outbox Tracing**: Correlation between original requests and retry attempts

## 🏛️ Architecture Patterns

### Repository Pattern
Data access is abstracted through repository interfaces that provide:
- **Standard CRUD Operations**: Basic entity management functionality
- **Custom Query Methods**: Business-specific data retrieval operations
- **Specification Support**: Dynamic query building for complex filtering
- **Transaction Management**: Automatic transaction handling for data consistency

### Specification Pattern
Dynamic query building is implemented for flexible data filtering:
- **Composable Criteria**: Combination of multiple filter conditions
- **Type Safety**: Compile-time validation of query parameters
- **Reusable Components**: Shared specifications across different use cases
- **Performance Optimization**: Efficient query generation and execution

### DTO Pattern
Data transfer between layers is managed through dedicated transfer objects:
- **Request DTOs**: Input validation and data binding for API endpoints
- **Response DTOs**: Structured output formatting and data projection
- **Internal DTOs**: Service-to-service communication data structures
- **Mapping Strategy**: Automated conversion between entities and DTOs

### Service Layer Pattern
Business logic is encapsulated in service components that:
- **Coordinate Operations**: Orchestrate multiple repository and external service calls
- **Handle Transactions**: Manage database transaction boundaries
- **Implement Business Rules**: Enforce domain-specific constraints and validations
- **Provide Fault Tolerance**: Integrate circuit breaker and retry mechanisms

## 📋 API Documentation

### Swagger UI
- **URL**: `http://localhost:8081/swagger-ui.html`
- **API Docs**: `http://localhost:8081/api-docs`

### Key Endpoints

#### Authentication
- `POST /api/v1/auth/login` - User authentication with credentials
- `POST /api/v1/auth/refresh` - JWT token refresh mechanism
- `POST /api/v1/auth/logout` - User logout and token invalidation

#### Trainers
- `GET /api/v1/trainers` - Retrieve all trainers with filtering options
- `POST /api/v1/trainers` - Create new trainer profile
- `PUT /api/v1/trainers/{username}` - Update existing trainer information
- `PATCH /api/v1/trainers/{username}/toggle-status` - Activate/deactivate trainer

#### Trainees
- `GET /api/v1/trainees` - Retrieve all trainees with filtering capabilities
- `POST /api/v1/trainees` - Create new trainee profile
- `PUT /api/v1/trainees/{username}` - Update existing trainee information

#### Trainings
- `GET /api/v1/trainings` - List training sessions with advanced filtering
- `POST /api/v1/trainings` - Schedule new training session
- `DELETE /api/v1/trainings/{id}` - Cancel existing training session

## 🧪 Testing Strategy

### Unit Testing
- **Service Layer Testing**: Business logic validation with mocked dependencies
- **Repository Testing**: Data access layer validation with embedded databases
- **Controller Testing**: API endpoint testing with MockMvc framework
- **Pattern Testing**: Circuit breaker and outbox pattern behavior validation

### Integration Testing
- **Database Integration**: Full database interaction testing with TestContainers
- **Service Communication**: Inter-service communication testing with WireMock
- **End-to-End Scenarios**: Complete user journey testing across all layers
- **Resilience Testing**: Fault injection and recovery scenario validation

### Test Coverage
- **Comprehensive Coverage**: High test coverage across all architectural layers
- **Pattern Validation**: Specific testing for implemented design patterns
- **Error Scenarios**: Extensive testing of failure modes and recovery mechanisms
- **Performance Testing**: Load testing and performance characteristic validation

## 🔧 Configuration Management

### Environment-Specific Configuration
The service supports multiple configuration profiles for different deployment environments:
- **Local Development**: Simplified configuration for local development
- **Development Environment**: Extended logging and debugging capabilities
- **Production Environment**: Optimized performance and security settings
- **Testing Environment**: Isolated configuration for automated testing

### Key Configuration Areas
- **Database Connections**: Connection pooling and performance tuning
- **Service Discovery**: Eureka client registration and health check configuration
- **Circuit Breaker**: Resilience parameters and threshold configuration
- **Retry Mechanisms**: Outbox processing frequency and error handling
- **Security Settings**: JWT configuration and authentication parameters

## 🔍 Troubleshooting

### Common Issues

1. **Circuit Breaker Constantly Open**
   - **Symptoms**: All workload service communications failing
   - **Diagnosis**: Check Workload Service availability and network connectivity
   - **Solution**: Verify service discovery registration and adjust threshold parameters

2. **Accumulating Pending Workloads**
   - **Symptoms**: Growing number of entries in outbox table
   - **Diagnosis**: Check WorkloadRelayService execution logs and target service status
   - **Solution**: Verify scheduled task execution and target service availability

3. **Database Performance Issues**
   - **Symptoms**: Slow response times and connection pool exhaustion
   - **Diagnosis**: Monitor connection pool metrics and query performance
   - **Solution**: Optimize queries, adjust pool settings, and review transaction boundaries

4. **Authentication Failures**
   - **Symptoms**: JWT token validation errors and authentication rejections
   - **Diagnosis**: Check Redis connectivity and token expiration settings
   - **Solution**: Verify Redis configuration and JWT secret consistency

### Health Check Endpoints
- `/actuator/health` - Overall service health status
- `/actuator/health/db` - Database connectivity and performance
- `/actuator/health/redis` - Redis cache availability
- `/actuator/health/circuitBreakers` - Circuit breaker status and metrics

### Monitoring Queries
Database queries for operational monitoring:
- **Pending Workload Analysis**: Track retry patterns and success rates
- **Performance Metrics**: Monitor response times and throughput
- **Error Pattern Analysis**: Identify common failure scenarios
- **Capacity Planning**: Analyze usage trends and resource utilization

---

## 🏆 Key Features

- ✅ **Circuit Breaker Pattern** with Resilience4j for fault tolerance
- ✅ **Outbox Pattern** for guaranteed message delivery
- ✅ **Feign Client** for declarative service communication
- ✅ **Scheduled Retry Mechanism** for reliable inter-service communication
- ✅ **Transactional Outbox** ensuring data consistency across operations
- ✅ **JWT Authentication** with Redis-based token management
- ✅ **Database per Service** pattern implementation
- ✅ **Comprehensive Health Checks** and operational monitoring
- ✅ **Distributed Tracing** with Zipkin integration
- ✅ **Business Metrics Collection** with Micrometer
- ✅ **Clean Architecture** with clear separation of concerns
- ✅ **Extensive Testing Strategy** covering all architectural layers 