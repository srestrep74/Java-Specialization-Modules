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
- **Workload Integration**: Asynchronous communication with Workload Service via ActiveMQ for trainer workload tracking

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

### Message Broker
- **ActiveMQ**: Asynchronous messaging infrastructure
  - Broker URL: `tcp://activemq:61616`
  - Web Console: `http://localhost:8161` (admin/admin)
  - Queue: `workload-queue` for trainer workload messages
  - JMS Configuration: Point-to-point messaging with Jackson serialization

## 🔄 Circuit Breaker Pattern Implementation

### Overview
The Circuit Breaker pattern is implemented using **Resilience4j** to provide fault tolerance when communicating with the ActiveMQ message broker. This pattern prevents cascade failures and provides graceful degradation when the messaging infrastructure is unavailable.

### How Circuit Breaker Works

The circuit breaker operates in three states:

1. **CLOSED** (Normal Operation)
   - All messages are sent to ActiveMQ broker successfully
   - Success/failure rates are monitored
   - If failure rate exceeds threshold, transitions to OPEN

2. **OPEN** (Failure State)
   - All message sending attempts are immediately rejected
   - Fallback method is executed (Outbox pattern activated)
   - After wait duration, transitions to HALF_OPEN

3. **HALF_OPEN** (Recovery Testing)
   - Limited number of test messages are allowed
   - If successful, transitions back to CLOSED
   - If failed, returns to OPEN state

### Circuit Breaker Configuration Details

The circuit breaker is configured with specific thresholds and timing parameters for ActiveMQ communication:
- **Sliding Window Size**: 10 message sending attempts for failure rate calculation
- **Minimum Calls**: 3 calls required before state evaluation
- **Failure Rate Threshold**: 60% failure rate triggers circuit opening
- **Wait Duration**: 30 seconds before attempting recovery
- **Half-Open Test Calls**: 2 permitted calls for testing recovery
- **Slow Call Threshold**: 80% slow calls with 5-second duration limit
- **Timeout Duration**: 15 seconds for individual message sending operations
- **Recorded Exceptions**: JMSException, JmsException, ConnectException, IOException

## 🌐 ActiveMQ Asynchronous Communication & Outbox Pattern

### Primary ActiveMQ Message Producer
**Spring JMS** provides asynchronous messaging communication with the Workload Service through the `WorkloadMessageProducer` component. This producer is configured with circuit breaker protection and integrates seamlessly with the outbox pattern for guaranteed message delivery.

### Outbox Pattern Implementation

The service implements the **Outbox Pattern** to ensure reliable communication with the Workload Service via ActiveMQ, guaranteeing that workload updates are eventually delivered even when the message broker is temporarily unavailable.

#### How the Communication Flow Works

1. **Normal Transaction Processing**: When a training session is created, updated, or deleted, the business logic performs the main database transaction (saving/updating the Training entity)

2. **Immediate Message Sending**: After the successful database transaction, the `WorkloadMessageProducer` immediately attempts to send a message to the `workload-queue` using JMS Template

3. **Circuit Breaker & Fallback Activation**: If the ActiveMQ broker is unavailable, times out, or the circuit breaker is OPEN, the `fallbackSendWorkloadMessage` method is automatically triggered

4. **Fallback Storage (Outbox Pattern)**: The fallback mechanism stores the workload data in the `PendingWorkload` table (outbox), ensuring no data is lost

5. **Scheduled Retry Processing**: A background process (`WorkloadRelayService`) runs every 2 minutes, scanning the outbox table and attempting to process all pending workload entries by resending messages to ActiveMQ

#### Detailed Communication Flow

**Scenario 1: Successful Communication**
- Training operation completes in database
- `WorkloadMessageProducer` sends message to `workload-queue` immediately
- ActiveMQ broker acknowledges message delivery
- No outbox entry is created
- Operation is complete

**Scenario 2: Failed Communication with Fallback**
- Training operation completes in database
- `WorkloadMessageProducer` attempts to send message to ActiveMQ
- Message sending fails (broker down, timeout, circuit breaker open)
- `fallbackSendWorkloadMessage` is automatically executed
- Fallback saves workload data to `PendingWorkload` table (outbox)
- User operation completes successfully (non-blocking)
- Background service will retry later

**Scenario 3: Recovery Processing**
- `WorkloadRelayService` runs every 2 minutes
- Scans `PendingWorkload` table for pending entries
- Attempts to resend each pending workload message to ActiveMQ
- Successfully processed entries are removed from outbox
- Failed attempts remain for next retry cycle

#### Key Components in the Flow

**WorkloadMessageProducer (Primary Producer)**
- Main JMS message producer for immediate workload notifications
- Configured with circuit breaker and fallback mechanism
- Uses JmsTemplate for message sending operations
- Directly integrated with the main transaction flow

**fallbackSendWorkloadMessage (Fallback Handler)**
- Automatically activated when primary message sending fails
- Stores workload data in the outbox table (PendingWorkload)
- Handles duplicate prevention through unique constraints
- Provides graceful degradation without blocking business operations
- Logs critical errors if outbox storage fails

**JmsTemplate (Message Sending Infrastructure)**
- Spring JMS template for ActiveMQ communication
- Configured with Jackson message converter for JSON serialization
- Handles connection management and message delivery
- Supports point-to-point messaging to workload-queue

**WorkloadRelayService (Background Processor)**
- Scheduled service running every 2 minutes
- Processes all pending workloads in the outbox
- Removes successfully processed entries
- Maintains retry persistence for failed attempts

#### PendingWorkload Entity (Outbox Table)

The outbox table stores all necessary information to reconstruct and replay the workload message:
- **Trainer Information**: Username, first name, last name, and active status
- **Training Details**: Training date and duration
- **Action Type**: Operation type (ADD, DELETE, UPDATE)
- **Metadata**: Creation timestamp for monitoring and audit purposes
- **Unique Constraints**: Prevents duplicate entries for the same trainer/date/action combination

#### Benefits of This Implementation

1. **Guaranteed Delivery**: Eventually consistent communication between services via ActiveMQ
2. **Fault Tolerance**: Handles temporary broker failures and network outages gracefully
3. **Data Consistency**: Maintains transactional integrity within the Gym Service domain
4. **Non-Blocking Operations**: Business operations continue even when message broker is down
5. **Monitoring Capability**: Provides visibility into failed message deliveries through the outbox table
6. **Performance Optimization**: Immediate message sending for fast communication, background retry for resilience
7. **Idempotency Protection**: Unique constraints prevent duplicate workload processing
8. **Audit Trail**: Maintains history of message sending attempts with timestamps
9. **Asynchronous Processing**: Decoupled communication allowing independent service scaling
10. **Message Persistence**: ActiveMQ provides message durability and delivery guarantees

#### Retry Strategy Details

- **Frequency**: Fixed rate execution every 2 minutes
- **Persistence**: Failed attempts remain in the outbox for continuous retry
- **Error Handling**: Individual failures don't stop processing of other pending workloads
- **Success Cleanup**: Automatically removes successfully processed workloads
- **Monitoring**: Tracks retry attempts and success rates through comprehensive logging

#### Integration with Circuit Breaker

The Outbox pattern works seamlessly with the Circuit Breaker to provide multiple layers of resilience:

1. **Circuit CLOSED State**: Normal ActiveMQ message sending with immediate success
2. **Circuit OPEN State**: All message sending attempts immediately trigger fallback and go to outbox storage
3. **Circuit HALF_OPEN State**: Some message sending attempts are allowed, failures trigger fallback
4. **Background Processing**: WorkloadRelayService continues processing regardless of circuit breaker state

This implementation ensures that business operations are never blocked by message broker availability, while guaranteeing eventual delivery of all workload updates through the outbox pattern and ActiveMQ.

## 📊 Monitoring & Observability

### Health Checks
- **Database Health**: PostgreSQL connection status and query performance
- **Redis Health**: Cache connectivity and response times
- **Eureka Health**: Service discovery registration status
- **Circuit Breaker Health**: ActiveMQ communication status and state transitions
- **Outbox Health**: Pending workloads count and processing status
- **ActiveMQ Health**: Message broker connectivity and queue status

### Metrics Collection
The service collects comprehensive metrics for business operations and technical performance:
- **Training Metrics**: Creation rates, duration distributions, and scheduling patterns
- **Authentication Metrics**: Login attempts, success rates, and token generation
- **Circuit Breaker Metrics**: State transitions, failure rates, and response times for ActiveMQ communication
- **Outbox Metrics**: Pending workload counts, processing success rates, and retry frequencies
- **Database Metrics**: Connection pool utilization, query performance, and transaction rates
- **JMS Metrics**: Message sending rates, queue depths, and broker connectivity

### Distributed Tracing
- **Zipkin Integration**: End-to-end request tracing across service boundaries
- **Correlation IDs**: Request tracking through synchronous and asynchronous processing
- **B3 Propagation**: Trace context propagation for distributed system visibility
- **Outbox Tracing**: Correlation between original requests and retry attempts
- **JMS Tracing**: Message flow tracking through ActiveMQ broker

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

### Component Testing
The service implements comprehensive **Component Testing** using **Cucumber BDD (Behavior Driven Development)** framework to ensure individual component functionality validation in an isolated environment.

#### Testing Architecture
- **Cucumber Framework**: BDD testing with Gherkin syntax for readable test scenarios
- **Spring Boot Test**: Full application context testing with embedded components
- **H2 Database**: In-memory PostgreSQL-compatible database for isolated test execution
- **WebTestClient**: Non-blocking HTTP client for API endpoint testing
- **Test Context Management**: Centralized test state and data management

#### Test Organization Structure
```
component/
├── config/                    # Test configuration classes
│   └── properties/            # Test properties management
├── context/                   # Test context classes
│   ├── TraineeTestContext.java # Trainee test state management
│   ├── TrainerTestContext.java # Trainer test state management
│   └── TrainingTestContext.java # Training test state management
├── hooks/                     # Cucumber lifecycle hooks
│   ├── TraineeTestHooks.java  # Trainee test setup and teardown
│   ├── TrainerTestHooks.java  # Trainer test setup and teardown
│   └── TrainingTestHooks.java # Training test setup and teardown
├── steps/                     # Step definitions
│   ├── CommonHttpSteps.java   # Shared HTTP operations
│   ├── TraineeManagementSteps.java # Trainee-specific steps
│   ├── TrainerManagementSteps.java # Trainer-specific steps
│   └── TrainingManagementSteps.java # Training-specific steps
├── TraineeComponentTestSuite.java # Trainee test suite
├── TrainerComponentTestSuite.java # Trainer test suite
└── TrainingComponentTestSuite.java # Training test suite
```

#### BDD Feature Scenarios
The component tests cover comprehensive domain management scenarios using **Gherkin syntax**:

**Trainee Management Scenarios**
- ✅ **Registration**: Successfully register new trainees with automatic username/password generation
- ✅ **Profile Management**: Retrieve, update, and manage trainee profiles
- ✅ **Authentication**: Validate trainee login and token management
- ✅ **Authorization**: Ensure proper role-based access control
- ✅ **Data Validation**: Handle invalid data and validation errors

**Trainer Management Scenarios**
- ✅ **Registration**: Successfully register new trainers with specializations
- ✅ **Profile Management**: Retrieve, update, and manage trainer profiles
- ✅ **Status Management**: Activate/deactivate trainer accounts
- ✅ **Training Retrieval**: Get trainer's training sessions and history
- ✅ **Authorization**: Validate trainer-specific permissions

**Training Management Scenarios**
- ✅ **Session Creation**: Successfully create training sessions between trainers and trainees
- ✅ **Session Updates**: Modify existing training sessions
- ✅ **Session Deletion**: Cancel and remove training sessions
- ✅ **Scheduling**: Handle training scheduling and conflicts
- ✅ **Data Validation**: Ensure training business rules are enforced

#### API Endpoint Coverage
- ✅ **Authentication Endpoints**: Login, logout, token refresh
- ✅ **Trainee Endpoints**: CRUD operations for trainee management
- ✅ **Trainer Endpoints**: CRUD operations for trainer management
- ✅ **Training Endpoints**: CRUD operations for training session management

#### Test Configuration Features
- **Externalized Configuration**: Test properties managed through `application-test.yml`
- **Token Management**: JWT authentication tokens configured via YAML properties
- **Database Cleanup**: Automatic H2 database cleanup between test executions
- **Mock Services**: Isolated testing environment with mocked external dependencies
- **Test Data Management**: Centralized test context for state management across scenarios

### Integration Testing
The service implements comprehensive **Integration Testing** using **Cucumber BDD** framework to validate cross-service communication and end-to-end workflows, particularly focusing on **Workload Service integration** through ActiveMQ messaging.

#### Testing Architecture
- **Cucumber Framework**: BDD testing with Gherkin syntax for integration scenarios
- **Spring Boot Test**: Full application context with embedded ActiveMQ broker
- **TestContainers**: Docker-based PostgreSQL and ActiveMQ for realistic integration testing
- **Circuit Breaker Testing**: Resilience4j circuit breaker behavior validation
- **Outbox Pattern Testing**: Message delivery reliability and retry mechanism validation

#### Test Organization Structure
```
integration/
├── config/                    # Integration test configuration
│   ├── ActiveMQTestcontainersConfig.java # ActiveMQ container management
│   ├── IntegrationTestConfig.java # Integration test setup
│   └── TestJmsConfig.java     # JMS configuration for testing
├── hooks/                     # Integration test lifecycle hooks
│   └── TestHooks.java         # Test setup and teardown
├── steps/                     # Integration step definitions
│   └── WorkloadIntegrationSteps.java # Workload integration steps
├── WorkloadIntegrationTestContext.java # Integration test state management
└── WorkloadIntegrationTestRunner.java # Integration test suite
```

#### BDD Integration Scenarios
The integration tests cover comprehensive cross-service communication scenarios:

**Workload Integration Scenarios**
- ✅ **Successful Communication**: Training operations trigger proper workload notifications
- ✅ **Message Content Validation**: Ensure workload messages contain correct trainer and training data
- ✅ **Database Consistency**: Verify training data is properly saved and synchronized
- ✅ **Error Handling**: Handle invalid trainer/trainee scenarios gracefully

**Circuit Breaker Scenarios**
- ✅ **Circuit Breaker Activation**: Validate circuit breaker opens after multiple ActiveMQ failures
- ✅ **Fallback Mechanism**: Ensure outbox pattern activates when messaging fails
- ✅ **Recovery Testing**: Verify circuit breaker recovery and message processing
- ✅ **System Resilience**: Confirm system continues functioning during messaging failures

**Outbox Pattern Scenarios**
- ✅ **Message Persistence**: Failed messages are stored in pending workload table
- ✅ **Retry Processing**: Background service processes pending workloads
- ✅ **Data Consistency**: Ensure no data loss during messaging failures
- ✅ **Monitoring**: Track retry attempts and success rates

#### Integration Test Features
- **ActiveMQ Testcontainers**: Real ActiveMQ broker in Docker container for realistic testing
- **Circuit Breaker Simulation**: Controlled failure injection for resilience testing
- **Outbox Pattern Validation**: Comprehensive testing of message delivery reliability
- **Cross-Service Communication**: End-to-end testing of Gym Service to Workload Service communication
- **Failure Scenario Testing**: Extensive testing of various failure modes and recovery mechanisms

#### Testing Benefits
- **BDD Approach**: Business-readable integration scenarios that serve as living documentation
- **Realistic Testing**: Uses actual ActiveMQ broker and PostgreSQL database
- **Comprehensive Coverage**: Tests all major integration points and failure scenarios
- **Resilience Validation**: Thorough testing of circuit breaker and outbox pattern implementations
- **End-to-End Validation**: Complete workflow testing from API to message delivery
- **Failure Mode Testing**: Comprehensive negative testing for robust error handling

### Test Coverage
- **Comprehensive Coverage**: High test coverage across all architectural layers
- **Pattern Validation**: Specific testing for implemented design patterns (Circuit Breaker, Outbox)
- **Error Scenarios**: Extensive testing of failure modes and recovery mechanisms
- **Performance Testing**: Load testing and performance characteristic validation
- **Integration Coverage**: Complete cross-service communication validation

### 🏃‍♂️ Test Execution

Tests can be run from the command line, with the ability to select a particular set of tests, either for a single endpoint or for all.

#### Running Component Tests

**Execute All Component Tests**
```bash
mvn test -Dtest=*ComponentTestSuite
```

**Execute Specific Domain Tests**
```bash
# Trainee Management Tests
mvn test -Dtest=TraineeComponentTestSuite

# Trainer Management Tests  
mvn test -Dtest=TrainerComponentTestSuite

# Training Management Tests
mvn test -Dtest=TrainingComponentTestSuite
```

**Execute Tests by Cucumber Tags**
```bash
# Run only positive scenarios
mvn test -Dcucumber.filter.tags="@positive"

# Run only negative scenarios
mvn test -Dcucumber.filter.tags="@negative"

# Run authentication-related tests
mvn test -Dcucumber.filter.tags="@authentication"

# Run authorization-related tests
mvn test -Dcucumber.filter.tags="@authorization"

# Run specific domain with specific scenario type
mvn test -Dtest=TraineeComponentTestSuite -Dcucumber.filter.tags="@positive"
```

#### Running Integration Tests

**Execute All Integration Tests**
```bash
mvn test -Dtest=WorkloadIntegrationTestRunner
```

**Execute Integration Tests by Tags**
```bash
# Run workload integration scenarios
mvn test -Dtest=WorkloadIntegrationTestRunner -Dcucumber.filter.tags="@workload"

# Run circuit breaker scenarios
mvn test -Dtest=WorkloadIntegrationTestRunner -Dcucumber.filter.tags="@circuit-breaker"

# Run outbox pattern scenarios
mvn test -Dtest=WorkloadIntegrationTestRunner -Dcucumber.filter.tags="@outbox"
```

#### Running Tests by Category

**Execute All Tests (Component + Integration)**
```bash
mvn test
```

**Execute Tests with Specific Profile**
```bash
# Run with test profile
mvn test -Dspring.profiles.active=test

# Run with integration-test profile
mvn test -Dspring.profiles.active=integration-test
```

**Execute Tests with Debug Information**
```bash
# Run with debug logging
mvn test -Dlogging.level.dev.sro.gym_service=DEBUG

# Run with ActiveMQ debug logging
mvn test -Dlogging.level.org.apache.activemq=DEBUG
```

#### Test Execution Examples

**Quick Component Test Run**
```bash
# Run only trainee registration tests
mvn test -Dtest=TraineeComponentTestSuite -Dcucumber.filter.tags="@registration"
```

**Integration Test with Specific Scenario**
```bash
# Run only successful workload notification tests
mvn test -Dtest=WorkloadIntegrationTestRunner -Dcucumber.filter.tags="@workload and @success"
```

**Comprehensive Test Run**
```bash
# Run all tests with full logging
mvn test -Dlogging.level.dev.sro.gym_service=DEBUG -Dlogging.level.org.springframework.jms=DEBUG
```

#### Test Execution Benefits
- **Selective Testing**: Run specific test suites or scenarios for focused development
- **Tag-Based Filtering**: Use Cucumber tags to execute related test scenarios
- **Profile-Based Configuration**: Different test configurations for different environments
- **Debug Capabilities**: Enhanced logging for troubleshooting test issues
- **CI/CD Integration**: Command-line execution suitable for automated pipelines

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
   - **Symptoms**: All ActiveMQ message sending operations failing
   - **Diagnosis**: Check ActiveMQ broker availability and network connectivity
   - **Solution**: Verify broker connectivity and adjust threshold parameters

2. **Accumulating Pending Workloads**
   - **Symptoms**: Growing number of entries in outbox table
   - **Diagnosis**: Check WorkloadRelayService execution logs and ActiveMQ broker status
   - **Solution**: Verify scheduled task execution and broker availability

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
- `/actuator/health/circuitBreakers` - Circuit breaker status and metrics for ActiveMQ communication

### Monitoring Queries
Database queries for operational monitoring:
- **Pending Workload Analysis**: Track retry patterns and success rates
- **Performance Metrics**: Monitor response times and throughput
- **Error Pattern Analysis**: Identify common failure scenarios
- **Capacity Planning**: Analyze usage trends and resource utilization

---

## 🏆 Key Features

- ✅ **Circuit Breaker Pattern** with Resilience4j for ActiveMQ fault tolerance
- ✅ **Outbox Pattern** for guaranteed message delivery via ActiveMQ
- ✅ **ActiveMQ Integration** for asynchronous inter-service communication
- ✅ **Scheduled Retry Mechanism** for reliable message delivery
- ✅ **Transactional Outbox** ensuring data consistency across operations
- ✅ **JWT Authentication** with Redis-based token management
- ✅ **Database per Service** pattern implementation
- ✅ **Comprehensive Health Checks** and operational monitoring
- ✅ **Distributed Tracing** with Zipkin integration
- ✅ **Business Metrics Collection** with Micrometer
- ✅ **Clean Architecture** with clear separation of concerns
- ✅ **Extensive Testing Strategy** covering all architectural layers 