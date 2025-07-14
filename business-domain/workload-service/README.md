# Workload Service Microservice

A specialized microservice built with Spring Boot for tracking and managing trainer workloads and monthly summaries in a distributed gym management system. This service focuses on workload analytics and reporting capabilities.

## 🏗️ Service Overview

The **Workload Service** is a dedicated analytics microservice responsible for:

- **Trainer Workload Tracking**: Real-time processing of trainer training sessions
- **Monthly Summaries**: Aggregated trainer performance and workload data
- **Workload Analytics**: Statistical analysis of trainer productivity
- **Training Session Processing**: Handling workload updates from the Gym Service
- **Reporting Services**: Providing comprehensive trainer workload reports
- **Data Aggregation**: Consolidating training data for business intelligence

## 🎯 Business Domain

### Core Entities
- **Trainer**: Basic trainer information with workload tracking capabilities
- **TrainingSession**: Individual training sessions with duration and metadata
- **MonthlySummary**: Aggregated monthly data per trainer with training statistics
- **ActionType**: Enumeration for workload operations (ADD, DELETE, UPDATE)

### Key Business Rules
- Each trainer has associated training sessions tracked over time
- Monthly summaries are automatically calculated based on training sessions
- Workload data is aggregated by year and month for reporting purposes
- Training session durations contribute to monthly workload calculations
- Historical data is maintained for trend analysis and reporting
- Data consistency is maintained through transactional operations

## 🛠️ Infrastructure & Dependencies

### Database Layer
- **Primary Database**: MySQL
  - Port: `3307` (to avoid conflicts with standard MySQL)
  - Database: `workload_db`
  - User: `workload_user`
  - Connection Pool: HikariCP
  - JPA/Hibernate for ORM
- **Administration**: phpMyAdmin (port `8080`)

### Service Discovery
- **Eureka Client**: Registers with Eureka Server
  - Server URL: `http://localhost:8761/eureka/`
  - Instance ID: Dynamic with random values
  - Health checks and heartbeat configuration

### Configuration Management
- **Spring Cloud Config Client**: External configuration
  - Config Server: `http://localhost:8888`
  - Profile-based configurations (dev, prod, local)

## 📊 Workload Processing Architecture

### Workload Data Flow

The service processes trainer workload information through a well-defined pipeline:

1. **Workload Reception**: Receives workload updates from the Gym Service via REST API
2. **Data Validation**: Validates incoming workload requests for completeness and accuracy
3. **Trainer Management**: Creates or updates trainer records as needed
4. **Session Processing**: Processes training session data based on action type (ADD, DELETE, UPDATE)
5. **Summary Calculation**: Updates monthly summaries with new workload data
6. **Response Generation**: Provides confirmation and status information back to calling service

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

## 📋 API Documentation

### Swagger UI
- **URL**: `http://localhost:8082/swagger-ui.html`
- **API Docs**: `http://localhost:8082/api-docs`

### Key Endpoints

#### Workload Processing
- `POST /api/v1/workloads` - Process trainer workload updates
  - Accepts workload data from Gym Service
  - Validates and processes training session information
  - Updates monthly summaries automatically

#### Monthly Summaries
- `GET /api/v1/workloads/trainers/{username}/monthly-summary` - Get complete trainer summary
  - Retrieves all historical monthly data for a trainer
  - Includes year-over-year trending information

- `GET /api/v1/workloads/trainers/{username}/monthly-summary/{year}` - Get yearly summary
  - Retrieves monthly data for a specific year
  - Provides year-specific performance metrics

- `GET /api/v1/workloads/trainers/{username}/monthly-summary/{year}/{month}` - Get monthly summary
  - Retrieves detailed data for a specific month
  - Includes session counts and duration totals

## 📊 Monitoring & Observability

### Health Checks
- **Database Health**: MySQL connection status and query performance
- **Eureka Health**: Service discovery registration status
- **Application Health**: Service availability and response times
- **Data Integrity Health**: Validation of summary calculations and consistency

### Metrics Collection
The service collects comprehensive metrics for operational monitoring:
- **Workload Processing Metrics**: Request rates, processing times, and success rates
- **Database Performance**: Query execution times and connection pool utilization
- **Summary Calculation Metrics**: Aggregation performance and accuracy measures
- **Error Tracking**: Failed operations and data inconsistency detection

### Distributed Tracing
- **Zipkin Integration**: Request tracing for workload processing operations
- **Correlation IDs**: Request tracking through data processing pipeline
- **Performance Monitoring**: End-to-end processing time analysis
- **Error Correlation**: Linking errors across processing stages

## 🔧 Configuration Management

### Environment-Specific Configuration
The service supports multiple configuration profiles:
- **Local Development**: Simplified configuration for local development
- **Development Environment**: Extended logging and debugging capabilities
- **Production Environment**: Optimized performance and security settings
- **Testing Environment**: Isolated configuration for automated testing

### Key Configuration Areas
- **Database Connections**: MySQL connection pooling and performance tuning
- **Service Discovery**: Eureka client registration and health check configuration
- **Processing Parameters**: Batch sizes and aggregation thresholds
- **Security Settings**: API access control and validation parameters

## 🧪 Testing Strategy

### Unit Testing
- **Service Layer Testing**: Business logic validation with mocked dependencies
- **Repository Testing**: Data access layer validation with embedded databases
- **Controller Testing**: API endpoint testing with MockMvc framework
- **Mapper Testing**: Data transformation validation and edge case handling

### Integration Testing
- **Database Integration**: Full database interaction testing with TestContainers
- **API Integration**: End-to-end API testing with realistic data scenarios
- **Summary Calculation**: Validation of aggregation logic with comprehensive datasets
- **Performance Testing**: Load testing for high-volume workload processing

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

3. **Workload Processing Failures**
   - **Symptoms**: Failed workload updates from Gym Service
   - **Diagnosis**: Check request validation and data format compatibility
   - **Solution**: Verify DTO mapping and validation rules

4. **Data Synchronization Issues**
   - **Symptoms**: Mismatched data between services
   - **Diagnosis**: Check for failed transactions and rollback scenarios
   - **Solution**: Implement data reconciliation and monitoring procedures

### Health Check Endpoints
- `/actuator/health` - Overall service health status
- `/actuator/health/db` - Database connectivity and performance
- `/actuator/metrics` - Service performance metrics
- `/actuator/info` - Service information and build details

### Monitoring Queries
Database queries for operational monitoring:
- **Summary Accuracy**: Validate calculated summaries against raw session data
- **Performance Analysis**: Monitor query execution times and resource usage
- **Data Volume Tracking**: Analyze growth patterns and capacity requirements
- **Error Pattern Analysis**: Identify common failure scenarios and root causes

## 📈 Performance Optimizations

### Database Optimizations
- **Connection Pooling**: Optimized HikariCP configuration for concurrent operations
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

- ✅ **Workload Processing** with real-time aggregation
- ✅ **Monthly Summary Generation** with automated calculations
- ✅ **RESTful API** for workload data management
- ✅ **Database per Service** pattern implementation
- ✅ **Comprehensive Health Checks** and operational monitoring
- ✅ **Distributed Tracing** with Zipkin integration
- ✅ **Performance Metrics** with Micrometer
- ✅ **Clean Architecture** with clear separation of concerns
- ✅ **Extensive Testing Strategy** covering all functionality
- ✅ **Flexible Reporting** with multiple aggregation levels
- ✅ **Data Integrity** with transactional consistency
- ✅ **Scalable Design** for high-volume workload processing 