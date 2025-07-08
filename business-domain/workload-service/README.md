# Workload Service

A Spring Boot microservice that manages trainer workloads and calculates monthly training hour summaries.

## Architecture

This service follows **Clean Architecture** principles with the following layers:

- **Domain Layer**: Contains entities, enums, and repository interfaces
- **Application Layer**: Contains DTOs, services, and business logic
- **Infrastructure Layer**: Contains JPA repository implementations
- **Presentation Layer**: Contains REST controllers and exception handlers

## Features

- Process trainer workload data (ADD/DELETE training sessions)
- Calculate monthly training hour summaries
- Store data in MySQL database
- Query trainer workload by username, year, and month
- Eureka service discovery integration
- Comprehensive validation and error handling

## API Endpoints

### 1. Process Trainer Workload

**POST** `/api/v1/trainers/workload`

Processes trainer workload data and updates monthly summaries.

**Request Body:**
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

**Response:**
```json
{
  "message": "Trainer workload processed successfully",
  "success": true
}
```

### 2. Get Trainer Monthly Summary

**GET** `/api/v1/trainers/{username}/monthly-summary`

Returns all monthly summaries for a trainer.

**Response:**
```json
{
  "trainerUsername": "john.doe",
  "trainerFirstName": "John",
  "trainerLastName": "Doe",
  "trainerStatus": true,
  "years": [
    {
      "year": 2024,
      "months": [
        {
          "month": 1,
          "trainingSummaryDuration": 180
        },
        {
          "month": 2,
          "trainingSummaryDuration": 120
        }
      ]
    }
  ]
}
```

### 3. Get Trainer Monthly Summary by Year

**GET** `/api/v1/trainers/{username}/monthly-summary/{year}`

Returns monthly summaries for a specific year.

### 4. Get Trainer Monthly Summary by Month

**GET** `/api/v1/trainers/{username}/monthly-summary/{year}/{month}`

Returns monthly summary for a specific month.

## Domain Models

### TrainerWorkloadRequest
- `trainerUsername`: String (required)
- `trainerFirstName`: String (required)
- `trainerLastName`: String (required)
- `isActive`: Boolean (required)
- `trainingDate`: LocalDate (required)
- `trainingDuration`: Integer (required, positive)
- `actionType`: ActionType (ADD/DELETE)

### TrainerMonthlySummaryResponse
- `trainerUsername`: String
- `trainerFirstName`: String
- `trainerLastName`: String
- `trainerStatus`: Boolean
- `years`: List of YearSummary
  - `year`: Integer
  - `months`: List of MonthSummary
    - `month`: Integer
    - `trainingSummaryDuration`: Integer (in minutes)

## Database Schema

### trainers
- `username` (VARCHAR, PRIMARY KEY)
- `first_name` (VARCHAR)
- `last_name` (VARCHAR)
- `is_active` (BOOLEAN)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### training_sessions
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `trainer_username` (VARCHAR, FOREIGN KEY)
- `training_date` (DATE)
- `training_duration` (INTEGER)
- `action_type` (VARCHAR)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### monthly_summaries
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `trainer_username` (VARCHAR, FOREIGN KEY)
- `year` (INTEGER)
- `month` (INTEGER)
- `total_duration` (INTEGER)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)
- UNIQUE CONSTRAINT: (`trainer_username`, `year`, `month`)

## Configuration

### Application Properties
```yaml
spring:
  application:
    name: workload-service
  datasource:
    url: jdbc:mysql://localhost:3306/workload_db
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8081

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## Running the Service

1. **Prerequisites:**
   - Java 21
   - MySQL 8.0+
   - Maven 3.8+

2. **Database Setup:**
   ```sql
   CREATE DATABASE workload_db;
   ```

3. **Build and Run:**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

4. **Health Check:**
   ```bash
   curl http://localhost:8081/actuator/health
   ```

## Testing

Run tests with:
```bash
./mvnw test
```

The service includes integration tests that verify:
- Trainer workload processing
- Monthly summary calculations
- ADD/DELETE operations
- Data persistence

## Error Handling

The service provides comprehensive error handling:
- Validation errors return 400 Bad Request
- Service errors return 500 Internal Server Error
- All errors include descriptive messages

## Integration with Gym Service

This service is designed to receive workload data from the main gym service when training sessions are planned or cancelled. The gym service should call the `/api/v1/trainers/workload` endpoint with the trainer workload data.

## Monitoring

The service exposes actuator endpoints for monitoring:
- `/actuator/health` - Health check
- `/actuator/info` - Application info
- `/actuator/metrics` - Application metrics 