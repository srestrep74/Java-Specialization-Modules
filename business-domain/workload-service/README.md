# Workload Service Microservice

A specialized microservice built with Spring Boot for tracking and managing trainer workloads and monthly summaries in a distributed gym management system. This service focuses on workload analytics and reporting capabilities.

## 🏗️ Service Overview

The **Workload Service** is a dedicated analytics microservice responsible for:

- **Trainer Workload Tracking**: Real-time processing of trainer training sessions
- **Monthly Summaries**: Aggregated trainer performance and workload data
- **Workload Analytics**: Statistical analysis of trainer productivity
- **Training Session Processing**: Consuming workload updates from the Gym Service via ActiveMQ messaging
- **Reporting Services**: Providing comprehensive trainer workload reports
- **Data Aggregation**: Consolidating training data for business intelligence

## 🎯 Business Domain

### Core Entities

#### TrainerTrainingSummary (MongoDB Document)
- **Collection**: `trainer_training_summaries`
- **Purpose**: Main document storing trainer workload summaries with hierarchical year/month structure
- **Key Fields**:
  - `trainerUsername`: Unique identifier for the trainer
  - `trainerFirstName` & `trainerLastName`: Trainer identification (indexed)
  - `trainerStatus`: Boolean indicating if trainer is active
  - `years`: List of YearSummary objects for historical data
  - `createdAt` & `updatedAt`: Timestamps for audit trail
- **Indexes**: Compound index on `trainer_first_name` and `trainer_last_name`
- **Business Logic**: Contains methods for profile updates and year management

#### TrainingSession (MongoDB Document)
- **Collection**: `training_sessions`
- **Purpose**: Individual training session records with metadata
- **Key Fields**:
  - `trainerUsername`: Reference to the trainer (indexed)
  - `trainingDate`: Date of the training session
  - `trainingDuration`: Duration in minutes
  - `actionType`: Type of operation (ADD, DELETE, UPDATE)
  - `createdAt` & `updatedAt`: Timestamps for audit trail
- **Business Logic**: Helper methods to extract year and month from training date

#### YearSummary (Embedded Document)
- **Purpose**: Aggregated data for a specific year within a trainer's summary
- **Key Fields**:
  - `year`: The year being summarized
  - `months`: List of MonthSummary objects for monthly breakdown
- **Business Logic**: Methods to find or create month summaries

#### MonthSummary (Embedded Document)
- **Purpose**: Aggregated data for a specific month within a year
- **Key Fields**:
  - `month`: The month number (1-12)
  - `trainingsSummaryDuration`: Total duration in minutes for the month
- **Business Logic**: Methods to add or subtract duration with validation

#### ActionType (Enumeration)
- **Values**: `ADD`, `DELETE`, `UPDATE`
- **Purpose**: Defines the type of workload operation being processed

### Key Business Rules
- Each trainer has a single `TrainerTrainingSummary` document with hierarchical year/month structure
- Training sessions are stored as individual documents in the `training_sessions` collection
- Monthly summaries are automatically calculated and embedded within year summaries
- Workload data is aggregated by year and month for efficient reporting
- Training session durations contribute to monthly workload calculations
- Historical data is preserved in the hierarchical structure for trend analysis
- Data consistency is maintained through MongoDB's document model and embedded relationships
- Indexes optimize queries for trainer lookups and date-based filtering

## 🛠️ Infrastructure & Dependencies

### Database Layer
- **Primary Database**: MongoDB
  - Port: `27017`
  - Database: `workload_db`
  - User: `admin`
  - Password: `admin123`
  - Connection Pool: MongoDB driver connection pooling
  - Spring Data MongoDB for data access
- **Administration**: Mongo Express (port `8084`)

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
- **ActiveMQ**: Asynchronous messaging infrastructure for workload consumption
  - Broker URL: `tcp://activemq:61616`
  - Web Console: `http://localhost:8161` (admin/admin)
  - Queue: `workload-queue` for consuming trainer workload messages
  - DLQ: `ActiveMQ.DLQ` for failed message processing
  - JMS Configuration: Point-to-point messaging with Jackson serialization
  - Redelivery Policy: Exponential backoff with configurable retry attempts

## 📊 ActiveMQ Message Consumption Architecture

### Workload Message Flow

The service processes trainer workload information through an asynchronous messaging pipeline:

1. **Message Consumption**: Consumes workload messages from the `workload-queue` via ActiveMQ
2. **Message Deserialization**: Converts JMS messages to `TrainerWorkloadRequest` DTOs using Jackson
3. **Data Validation**: Validates incoming workload requests for completeness and accuracy
4. **Trainer Management**: Creates or updates trainer records as needed
5. **Session Processing**: Processes training session data based on action type (ADD, DELETE, UPDATE)
6. **Summary Calculation**: Updates monthly summaries with new workload data
7. **Error Handling**: Failed messages are automatically moved to Dead Letter Queue (DLQ)

### Action Type Processing

**ADD Operations**
- Creates new training session records
- Updates monthly summaries with additional training duration
- Increments session counts for the relevant month/year
- Maintains trainer activity status

**DELETE Operations**
- Removes training session records
- Decreases monthly summary durations
- Adjusts session counts accordingly
- Preserves historical data integrity

### Dead Letter Queue (DLQ) Handling

The service implements comprehensive Dead Letter Queue processing to handle failed message consumption scenarios:

#### DLQ Processing Strategy

**Message Failure Scenarios**
- **Validation Errors**: Invalid workload data format or missing required fields
- **Business Logic Errors**: Data inconsistencies or constraint violations
- **System Errors**: Database connection issues or processing failures
- **Retry Exhaustion**: Messages that exceed maximum redelivery attempts

#### DLQ Configuration

**Redelivery Policy**
- **Maximum Redeliveries**: 3 attempts before moving to DLQ
- **Initial Redelivery Delay**: 2 seconds before first retry
- **Maximum Redelivery Delay**: 5 seconds with exponential backoff
- **Backoff Multiplier**: 2.0 for progressive delay increase
- **Use Exponential Backoff**: Enabled for intelligent retry timing

#### DLQ Message Processing

**Current Implementation**
The service currently implements a monitoring and metrics approach for DLQ messages:

- **DLQ Message Listener**: Dedicated consumer for `ActiveMQ.DLQ` queue
- **Metrics Collection**: Comprehensive Prometheus metrics for DLQ monitoring
- **Error Classification**: Categorization of failures by error type
- **Processing Time Tracking**: Performance monitoring of DLQ message handling

**Planned DLQ Processing Logic**
The architecture is designed to support advanced DLQ processing capabilities:

- **Database Persistence**: Store failed messages in dedicated error tracking collections
- **Email Notifications**: Alert administrators about critical processing failures
- **Manual Reprocessing**: Admin interface for reviewing and reprocessing failed messages
- **Data Reconciliation**: Tools for identifying and resolving data inconsistencies
- **Retry Orchestration**: Intelligent retry mechanisms with different strategies

#### DLQ Metrics and Monitoring

**Prometheus Metrics**
The service exposes comprehensive DLQ metrics for operational monitoring:

- **dlq_messages_total**: Total number of messages moved to DLQ
- **dlq_processing_success_total**: Successfully processed DLQ messages
- **dlq_processing_errors_total**: Errors during DLQ message processing
- **dlq_processing_duration_seconds**: Time taken to process DLQ messages
- **dlq_retry_attempts_total**: Number of retry attempts for DLQ messages
- **dlq_messages_by_error_type_total**: Messages categorized by error type

**Error Type Classification**
- **validation_error**: Data format or field validation failures
- **illegal_argument**: Invalid business logic or constraint violations
- **general_error**: System-level processing failures

#### DLQ Processing Flow

1. **Message Failure Detection**: JMS listener detects processing failure
2. **Error Classification**: Exception is categorized by type and severity
3. **Metrics Recording**: Failure metrics are recorded for monitoring
4. **DLQ Movement**: Message is automatically moved to ActiveMQ.DLQ
5. **DLQ Processing**: Dedicated DLQ listener processes failed messages
6. **Metrics Collection**: Processing time and success/failure metrics are recorded
7. **Future Enhancement**: Ready for advanced processing logic implementation

### Monthly Summary Aggregation

The service maintains sophisticated monthly aggregation logic:

- **Automatic Calculation**: Monthly summaries are computed automatically based on training sessions
- **Year-Month Grouping**: Data is organized by trainer, year, and month combinations
- **Duration Aggregation**: Total training minutes are calculated per month
- **Session Counting**: Number of training sessions is tracked per period
- **Real-time Updates**: Summaries are updated immediately when workload changes occur
- **Historical Preservation**: Past monthly data is preserved for trend analysis

## 🏛️ Architecture Patterns

### Repository Pattern
Data access is abstracted through repository interfaces that provide:
- **Standard CRUD Operations**: Basic entity management functionality
- **Custom Query Methods**: Business-specific data retrieval operations
- **Aggregation Queries**: Monthly summary calculations and reporting queries
- **Performance Optimization**: Efficient queries for large dataset operations

### Service Layer Pattern
Business logic is encapsulated in service components that:
- **Process Workload Requests**: Handle incoming workload data from external services
- **Manage Data Aggregation**: Calculate and maintain monthly summaries
- **Enforce Business Rules**: Implement domain-specific constraints and validations
- **Coordinate Operations**: Orchestrate multiple repository and calculation operations

### DTO Pattern
Data transfer between layers is managed through dedicated transfer objects:
- **Request DTOs**: Input validation and data binding for workload processing
- **Response DTOs**: Structured output formatting for API responses
- **Summary DTOs**: Aggregated data representation for reporting endpoints
- **Mapping Strategy**: Automated conversion between entities and transfer objects

### Mapper Pattern
Data transformation is handled through dedicated mapper components:
- **Entity Mapping**: Conversion between DTOs and database entities
- **Summary Mapping**: Transformation of raw data into aggregated summaries
- **Response Mapping**: Formatting of business data for API responses
- **Validation Integration**: Built-in validation during mapping processes

### Message Consumer Pattern
Asynchronous message processing is implemented through dedicated consumer components:
- **JMS Listener**: Dedicated consumer for `workload-queue` message processing
- **DLQ Listener**: Specialized consumer for `ActiveMQ.DLQ` failed message handling
- **Error Handler**: Custom error handling with metrics collection and error classification
- **Message Converter**: Jackson-based serialization for JSON message processing

## 📋 API Documentation

### Swagger UI
- **URL**: `http://localhost:8082/swagger-ui.html`
- **API Docs**: `http://localhost:8082/api-docs`

### Key Endpoints

#### Monthly Summaries (REST API)
- `GET /api/v1/workloads/trainers/{username}/monthly-summary` - Get complete trainer summary
  - Retrieves all historical monthly data for a trainer
  - Includes year-over-year trending information

- `GET /api/v1/workloads/trainers/{username}/monthly-summary/{year}` - Get yearly summary
  - Retrieves monthly data for a specific year
  - Provides year-specific performance metrics

- `GET /api/v1/workloads/trainers/{username}/monthly-summary/{year}/{month}` - Get monthly summary
  - Retrieves detailed data for a specific month
  - Includes session counts and duration totals

#### Message Consumption (ActiveMQ)
- **Queue**: `workload-queue` - Primary queue for trainer workload messages
- **DLQ**: `ActiveMQ.DLQ` - Dead Letter Queue for failed message processing
- **Message Format**: JSON serialized `TrainerWorkloadRequest` DTOs
- **Processing**: Asynchronous consumption with automatic error handling

## 📊 Monitoring & Observability

### Health Checks
- **Database Health**: MongoDB connection status and query performance
- **Eureka Health**: Service discovery registration status
- **Application Health**: Service availability and response times
- **Data Integrity Health**: Validation of summary calculations and consistency
- **ActiveMQ Health**: Message broker connectivity and queue status
- **DLQ Health**: Dead Letter Queue monitoring and processing status

### Metrics Collection
The service collects comprehensive metrics for operational monitoring:
- **Message Consumption Metrics**: Message processing rates, success rates, and throughput
- **DLQ Metrics**: Failed message counts, processing times, and error type distribution
- **Database Performance**: Query execution times and connection pool utilization
- **Summary Calculation Metrics**: Aggregation performance and accuracy measures
- **JMS Metrics**: Queue depths, consumer performance, and broker connectivity
- **Error Tracking**: Failed message processing and data inconsistency detection

### Distributed Tracing
- **Zipkin Integration**: Request tracing for message processing operations
- **Correlation IDs**: Request tracking through message processing pipeline
- **Performance Monitoring**: End-to-end message processing time analysis
- **Error Correlation**: Linking errors across processing stages
- **JMS Tracing**: Message flow tracking through ActiveMQ broker and DLQ

## 🔧 Configuration Management

### Environment-Specific Configuration
The service supports multiple configuration profiles:
- **Local Development**: Simplified configuration for local development
- **Development Environment**: Extended logging and debugging capabilities
- **Production Environment**: Optimized performance and security settings
- **Testing Environment**: Isolated configuration for automated testing

### Key Configuration Areas
- **Database Connections**: MongoDB connection pooling and performance tuning
- **Service Discovery**: Eureka client registration and health check configuration
- **Processing Parameters**: Batch sizes and aggregation thresholds
- **Security Settings**: API access control and validation parameters

## 🧪 Testing Strategy

### Unit Testing
- **Service Layer Testing**: Business logic validation with mocked dependencies
- **Repository Testing**: Data access layer validation with embedded MongoDB
- **Controller Testing**: API endpoint testing with MockMvc framework
- **Mapper Testing**: Data transformation validation and edge case handling

### Integration Testing
- **Database Integration**: Full database interaction testing with TestContainers
- **API Integration**: End-to-end API testing with realistic data scenarios
- **Summary Calculation**: Validation of aggregation logic with comprehensive datasets
- **Performance Testing**: Load testing for high-volume workload processing

### Component Testing
The service implements comprehensive **Component Testing** using **Cucumber BDD (Behavior Driven Development)** framework to ensure end-to-end functionality validation in an isolated environment.

#### Testing Architecture
- **Cucumber Framework**: BDD testing with Gherkin syntax for readable test scenarios
- **Spring Boot Test**: Full application context testing with embedded components
- **MongoDB Embedded**: In-memory MongoDB database for isolated test execution
- **WebTestClient**: Non-blocking HTTP client for API endpoint testing
- **Test Context Management**: Centralized test state and data management

#### Test Organization Structure
```
component/
├── config/                    # Test configuration classes
│   ├── TestMongoDBConfig.java # MongoDB cleanup configuration
│   └── properties/            # Test properties management
├── hooks/                     # Cucumber lifecycle hooks
│   └── WorkloadTestHooks.java # Test setup and teardown
├── steps/                     # Step definitions
│   ├── CommonHttpSteps.java   # Shared HTTP operations
│   └── WorkloadManagementSteps.java # Workload-specific steps
├── WorkloadTestContext.java   # Test state management
└── WorkloadComponentTestSuite.java # Test suite configuration
```

#### BDD Feature Scenarios
The component tests cover comprehensive workload management scenarios using **Gherkin syntax**:

**Positive Scenarios (Happy Path)**
- ✅ **Workload Processing**: Successfully process trainer workload additions, updates, and deletions
- ✅ **Monthly Summaries**: Retrieve trainer monthly summaries at different aggregation levels
- ✅ **Data Validation**: Ensure proper data persistence and calculation accuracy
- ✅ **Authentication**: Validate proper token-based authentication flow

**Negative Scenarios (Error Handling)**
- ✅ **Invalid Data**: Handle workload requests with missing or invalid data
- ✅ **Authentication Failures**: Proper error responses for missing or invalid tokens
- ✅ **Authorization Errors**: Validate role-based access control (TRAINER/ADMIN roles)
- ✅ **Not Found Scenarios**: Handle requests for non-existent trainer data

**API Endpoint Coverage**
- ✅ **POST /api/v1/workloads**: Process trainer workload requests
- ✅ **GET /api/v1/workloads/trainers/{username}/monthly-summary**: Retrieve complete trainer summary
- ✅ **GET /api/v1/workloads/trainers/{username}/monthly-summary/{year}**: Get yearly summary
- ✅ **GET /api/v1/workloads/trainers/{username}/monthly-summary/{year}/{month}**: Get monthly summary

#### Test Configuration Features
- **Externalized Configuration**: Test properties managed through `application-test.yml`
- **Token Management**: Internal authentication tokens configured via YAML properties
- **Database Cleanup**: Automatic MongoDB cleanup between test executions
- **Mock Services**: Isolated testing environment with mocked external dependencies
- **Test Data Management**: Centralized test context for state management across scenarios

#### Testing Benefits
- **BDD Approach**: Business-readable test scenarios that serve as living documentation
- **Isolated Execution**: Each test runs in a clean, isolated environment
- **Comprehensive Coverage**: Tests cover all major API endpoints and business scenarios
- **Maintainable Tests**: Modular step definitions for easy maintenance and reuse
- **Realistic Testing**: Tests use actual HTTP requests and database operations
- **Error Scenario Validation**: Comprehensive negative testing for robust error handling

### Test Coverage
- **Comprehensive Coverage**: High test coverage across all architectural layers
- **Edge Case Testing**: Validation of boundary conditions and error scenarios
- **Data Consistency**: Testing of aggregation accuracy and data integrity
- **Performance Validation**: Testing of response times under various load conditions

## 🔍 Troubleshooting

### Common Issues

1. **Summary Calculation Inconsistencies**
   - **Symptoms**: Monthly summaries don't match expected values
   - **Diagnosis**: Check for concurrent updates and transaction boundaries
   - **Solution**: Verify aggregation logic and implement proper locking mechanisms

2. **Database Performance Issues**
   - **Symptoms**: Slow response times for summary queries
   - **Diagnosis**: Monitor query execution plans and index usage
   - **Solution**: Optimize queries, add indexes, and review aggregation strategies

3. **Message Processing Failures**
   - **Symptoms**: Failed message consumption from ActiveMQ queue
   - **Diagnosis**: Check message format, validation rules, and consumer configuration
   - **Solution**: Verify DTO mapping, validation rules, and JMS listener configuration

4. **DLQ Message Accumulation**
   - **Symptoms**: Growing number of messages in Dead Letter Queue
   - **Diagnosis**: Check error patterns, validation failures, and processing logic
   - **Solution**: Review error handling, implement DLQ processing logic, and monitor metrics

5. **ActiveMQ Connectivity Issues**
   - **Symptoms**: Message consumption stops or connection failures
   - **Diagnosis**: Check broker availability, network connectivity, and JMS configuration
   - **Solution**: Verify broker status, connection settings, and redelivery policy configuration

6. **Data Synchronization Issues**
   - **Symptoms**: Mismatched data between services
   - **Diagnosis**: Check for failed message processing and DLQ accumulation
   - **Solution**: Implement data reconciliation, monitor DLQ metrics, and review processing logic

### Health Check Endpoints
- `/actuator/health` - Overall service health status
- `/actuator/health/db` - Database connectivity and performance
- `/actuator/metrics` - Service performance metrics including DLQ metrics
- `/actuator/info` - Service information and build details

### Monitoring Queries
Database queries for operational monitoring:
- **Summary Accuracy**: Validate calculated summaries against raw session data
- **Performance Analysis**: Monitor query execution times and resource usage
- **Data Volume Tracking**: Analyze growth patterns and capacity requirements
- **Error Pattern Analysis**: Identify common failure scenarios and root causes

## 📈 Performance Optimizations

### Database Optimizations
- **Connection Pooling**: Optimized MongoDB driver configuration for concurrent operations
- **Query Optimization**: Efficient aggregation queries for summary calculations
- **Indexing Strategy**: Strategic indexes for frequent query patterns
- **Batch Processing**: Optimized batch operations for bulk data updates

### Aggregation Strategy
- **Real-time Updates**: Immediate summary updates for responsive reporting
- **Efficient Calculations**: Optimized algorithms for monthly summary generation
- **Caching Strategy**: Strategic caching of frequently accessed summary data
- **Memory Management**: Efficient memory usage for large dataset processing

### API Performance
- **Response Optimization**: Efficient data serialization and transfer
- **Query Parameter Validation**: Early validation to prevent unnecessary processing
- **Result Pagination**: Support for large result set handling
- **Caching Headers**: Appropriate cache control for summary data

## 📋 Data Model

### Trainer Entity
- Stores basic trainer information for workload tracking
- Links to training sessions and monthly summaries
- Maintains trainer status and identification data

### TrainingSession Entity
- Records individual training sessions with duration and metadata
- Associates sessions with specific trainers and dates
- Supports different session types and classifications

### MonthlySummary Entity
- Aggregated monthly data per trainer
- Includes total duration, session counts, and calculated metrics
- Organized by year-month combinations for efficient querying

---

## 🏆 Key Features

- ✅ **ActiveMQ Message Consumption** with asynchronous processing
- ✅ **Dead Letter Queue (DLQ)** handling with comprehensive error management
- ✅ **Monthly Summary Generation** with automated calculations
- ✅ **RESTful API** for reporting and data retrieval
- ✅ **Database per Service** pattern implementation with MongoDB
- ✅ **Comprehensive Health Checks** and operational monitoring
- ✅ **Distributed Tracing** with Zipkin integration
- ✅ **DLQ Metrics** with Prometheus monitoring
- ✅ **Clean Architecture** with clear separation of concerns
- ✅ **Extensive Testing Strategy** covering all functionality
- ✅ **Flexible Reporting** with multiple aggregation levels
- ✅ **Data Integrity** with transactional consistency
- ✅ **Scalable Design** for high-volume message processing 