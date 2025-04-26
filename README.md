# Spring Boot Project - EPAM Training

### Developed By : Sebastián Restrepo Ortiz

## Project Overview
This project is developed as part of the EPAM Java Specialization Training, implementing a comprehensive Spring Boot application for training management. The system provides robust functionality for managing trainers and trainees, including user registration, authentication, training assignments, profile management, and reporting.

The application leverages core Spring Boot capabilities and follows modern microservice architecture principles:

- **Spring Data JPA**: For simplified, efficient database access with minimal boilerplate code
- **Spring Boot Actuator**: For production-ready monitoring and metrics collection
- **Environment Profiles**: Configured profiles (local, dev, production) for seamless deployment across environments
- **RESTful API Design**: Following best practices including versioning, consistent response structures, and proper HTTP method usage
- **Advanced Repository Patterns**: Using Specifications and custom queries for complex data operations
- **Comprehensive Testing**: Multi-layered testing strategy with specialized application profiles for testing

The project adheres to software engineering best practices including SOLID principles, dependency injection, AOP for cross-cutting concerns, comprehensive exception handling, and standardized response formats.

## Features Implemented

1. **Trainee Registration (POST method)**
   - **Request**: First Name (required), Last Name (required), Date of Birth (optional), Address (optional)
   - **Response**: Username, Password

2. **Trainer Registration (POST method)**
   - **Request**: First Name (required), Last Name (required), Specialization (required)
   - **Response**: Username, Password

3. **Login (GET method)**
   - **Request**: Username (required), Password (required)
   - **Response**: 200 OK

4. **Change Password (PUT method)**
   - **Request**: Username (required), Old Password (required), New Password (required)
   - **Response**: 200 OK

5. **Get Trainee Profile (GET method)**
   - **Request**: Username (required)
   - **Response**: First Name, Last Name, Date of Birth, Address, Is Active, Trainers List

6. **Update Trainee Profile (PUT method)**
   - **Request**: Username (required), First Name (required), Last Name (required), Date of Birth (optional), Address (optional), Is Active (required)
   - **Response**: Username, First Name, Last Name, Date of Birth, Address, Is Active, Trainers List

7. **Delete Trainee Profile (DELETE method)**
   - **Request**: Username (required)
   - **Response**: 200 OK

8. **Get Trainer Profile (GET method)**
   - **Request**: Username (required)
   - **Response**: First Name, Last Name, Specialization, Is Active, Trainees List

9. **Update Trainer Profile (PUT method)**
   - **Request**: Username (required), First Name (required), Last Name (required), Specialization (read only), Is Active (required)
   - **Response**: Username, First Name, Last Name, Specialization, Is Active, Trainees List

10. **Get not assigned on trainee active trainers (GET method)**
    - **Request**: Username (required)
    - **Response**: Trainer Username, Trainer First Name, Trainer Last Name, Trainer Specialization

11. **Update Trainee's Trainer List (PUT method)**
    - **Request**: Trainee Username, Trainers List (required)
    - **Response**: Trainers List

12. **Get Trainee Trainings List (GET method)**
    - **Request**: Username (required), Period From (optional), Period To (optional), Trainer Name (optional), Training Type (optional)
    - **Response**: Training Name, Training Date, Training Type, Training Duration, Trainer Name

13. **Get Trainer Trainings List (GET method)**
    - **Request**: Username (required), Period From (optional), Period To (optional), Trainee Name (optional)
    - **Response**: Training Name, Training Date, Training Type, Training Duration, Trainee Name

14. **Add Training (POST method)**
    - **Request**: Trainee username (required), Trainer username (required), Training Name (required), Training Date (required), Training Duration (required)
    - **Response**: 200 OK

15. **Activate/De-Activate Trainee (PATCH method)**
    - **Request**: Username (required), Is Active (required)
    - **Response**: 200 OK

16. **Activate/De-Activate Trainer (PATCH method)**
    - **Request**: Username (required), Is Active (required)
    - **Response**: 200 OK

17. **Get Training types (GET method)**
    - **Request**: No data
    - **Response**: Training types

## API Documentation
To document the APIs effectively and adhere to best practices and conventions, we used the `springdoc-openapi` library. This library helps in generating OpenAPI documentation for Spring Boot applications, making it easier to visualize and interact with the API endpoints.

### Library Used
We utilized the `springdoc-openapi-starter-webmvc-ui` library, which integrates seamlessly with Spring Boot to generate OpenAPI 3 documentation. This library provides a user-friendly interface to explore and test the API endpoints.

### Accessing the API Documentation
Once the application is running, you can access the API documentation by navigating to the following URL in your web browser:
```
http://localhost:8080/swagger-ui.html
```
This URL will open the Swagger UI, a web-based interface that allows you to view the API documentation, explore the available endpoints, and test them directly from the browser.

## Additional Features Implemented

1. **HATEOAS Implementation**:
   - **Library Used**: We utilized the `spring-boot-starter-hateoas` library to implement HATEOAS (Hypermedia as the Engine of Application State) in our REST API. This approach enhances the RESTful services by providing links to related resources, making the API more navigable and self-descriptive.
   - **Usage in TrainerController**: In the `TrainerController`, HATEOAS is used to add links to the responses, such as links to the trainer's profile, update operations, and related training sessions. This allows clients to discover and interact with the API more intuitively.

2. **Global Exception Handling**:
   - **Handler Used**: The `GlobalExceptionHandler` is implemented to manage exceptions across the application. It ensures that all exceptions are caught and handled in a consistent manner.
   - **Standard Error Response**: The handler uses the `ApiStandardError` Java record to provide a standardized error response structure. This includes details such as the timestamp, HTTP status code, error type, detailed message, and the request path that generated the error. This approach improves error transparency and debugging efficiency.

3. **Standardized Response Format**:
   - **Purpose**: To ensure consistency and clarity in API responses, both for successful operations and error handling.
   - **Successful Responses**: The `ApiStandardResponse` record is used to standardize successful responses. It includes:
     - **Timestamp**: When the response was generated.
     - **HTTP Status Code**: Indicates the result of the HTTP request.
     - **Message**: Describes the outcome of the operation.
     - **Request Path**: The endpoint that was accessed.
     - **Data Payload**: Contains the actual data returned by the API.
   - **Error Responses**: Managed using the `ApiStandardError` record, which provides a structured format for error details, including:
     - **Timestamp**: When the error occurred.
     - **HTTP Status Code**: Indicates the type of error.
     - **Error Type**: A brief description of the error category.
     - **Detailed Message**: Provides more context about the error.
     - **Request Path**: The endpoint that triggered the error.
   - **Response Builders**: 
     - **ErrorResponseBuilder**: Used by the `GlobalExceptionHandler` to create `ApiStandardError` responses. It provides methods for common HTTP error statuses like 404 (Not Found), 409 (Conflict), 400 (Bad Request), 401 (Unauthorized), and 500 (Internal Server Error).
     - **ResponseBuilder**: Used to create `ApiStandardResponse` for successful operations. It offers methods for standard responses like 200 (OK), 201 (Created), and 204 (No Content).
   - **Application in Controllers**: 
     - **TraineeController**: Utilizes `ResponseBuilder` to ensure all responses are consistent and follow the standardized format.
     - **Other Controllers**: Return `ResponseEntity` directly, but can be adapted to use `ResponseBuilder` for uniformity.
   - **Location**: All these utilities are located in the `util/response` package, centralizing response handling logic and promoting reuse across the application.

4. **Custom Authentication and Authorization**:
   - **Aspect-Oriented Approach**: Instead of using Spring Security, a custom aspect (`AuthAspect`) is implemented to handle authentication and authorization. This aspect checks if a user is authenticated and whether they have the required role to access specific endpoints.
   - **Custom Annotation**: The `@Authenticated` annotation is used to specify authentication and authorization requirements for each endpoint. It allows defining whether an endpoint requires authentication and if a specific user role (e.g., trainee or trainer) is needed.
   - **Example Usage**: For instance, in the `TraineeController`, the `updateActivationStatus` endpoint is annotated with `@Authenticated(requireTrainee = true)`, indicating that only authenticated trainees can access this endpoint.
   - **Implementation Details**: The `AuthAspect` uses the `AuthService` to verify user authentication and role. If the conditions are not met, an `UnauthorizedException` is thrown, denying access to the resource.

5. **API Versioning**:
   - **Purpose**: To follow REST best practices and ensure backward compatibility as the API evolves.
   - **Versioning Strategy**: The API is versioned using a path-based approach, with the current version being `v1`. This is reflected in the URL structure, such as `/api/v1/trainers` and `/api/v1/trainees`.
   - **Benefits**: Versioning allows for the introduction of new features and changes without disrupting existing clients. It provides a clear path for deprecating older versions while maintaining service continuity.

6. **DTOs and Versioning**:
   - **Purpose**: To create a clean, scalable codebase by ensuring that each request and response is handled with specific data transfer objects (DTOs).
   - **Specific DTOs**: Each endpoint has its own request and response DTOs, such as `LoginRequest`, `LoginResponse`, `UpdateTraineePorfileRequest`, and `TraineeProfileResponse`. This specificity ensures that only the necessary data is included, making the code more maintainable and clear.
   - **Versioning of DTOs**: DTOs are versioned alongside the API, allowing for changes and improvements without affecting existing clients. This ensures that as the API evolves, the DTOs can be updated to reflect new requirements while maintaining backward compatibility.
   - **Benefits**: Using specific DTOs for each operation helps in maintaining a clean separation between different parts of the application, ensuring that changes in one area do not inadvertently affect others. It also aids in validating and documenting the data structure expected by each endpoint.

## Implementation Details

1. **REST Controllers**:
   - **Spring Boot's `@RestController`**: The REST controllers are implemented using Spring Boot's `@RestController` annotation. They manage HTTP requests and responses, serving as the entry point for client interactions with the application. Each controller is responsible for handling specific endpoints, processing incoming requests, and returning appropriate responses.

2. **Services**:
   - **Business Logic**: The service layer contains the core business logic of the application. It acts as an intermediary between the controllers and repositories, ensuring that business rules are applied consistently.
   - **Transactional Management**: The `@Transactional` annotation is used in the service layer to manage transactions. This ensures data consistency and integrity by automatically handling transaction boundaries, making the codebase cleaner and more maintainable.

3. **Repositories**:
   - **Spring Data JPA Implementation**: The repository layer leverages Spring Data JPA, which provides a powerful abstraction over Hibernate and JPA. This approach significantly reduces boilerplate code while maintaining the flexibility needed for complex data operations.
   - **JpaRepository Extension**: Repositories are defined as interfaces extending `JpaRepository`, providing out-of-the-box implementations for common CRUD operations and pagination. This approach eliminates the need for manual EntityManager handling and boilerplate implementation code.
   - **Advanced Query Methods**:
     - **Method Name Conventions**: Simple queries are implemented using Spring Data's method naming conventions (e.g., `findByUsername`, `countByActive`), which automatically generate the appropriate JPQL queries.
     - **@Query Annotation**: For more complex queries, the `@Query` annotation is used to define custom JPQL or native SQL queries. This approach provides fine-grained control over the query logic while maintaining the clean repository interface.
     - **Specification Pattern**: For dynamic queries with multiple optional filtering criteria (e.g., searching trainings by date range, trainer name, etc.), the Specification pattern is implemented. The application uses the `JpaSpecificationExecutor` interface and custom specification classes to build type-safe, composable query predicates.
   - **Repository Features Used**:
     - **Custom Modifying Queries**: Using `@Modifying` and `@Query` for operations like password updates and username-based deletions.
     - **Parameter Binding**: Using named parameters with `@Param` annotation for safer and more readable queries.
   - **Example Implementation**: The `TrainingRepository` showcases these advanced features by implementing the `JpaSpecificationExecutor` interface to support complex filtering in training searches, combined with custom queries for metrics calculations.

## Monitoring and Metrics with Spring Boot Actuator

The application leverages Spring Boot Actuator to provide production-ready features for monitoring and managing the application. This includes health checks, metrics collection, and endpoint exposure.

### Enabled Actuator Endpoints

The following Actuator endpoints are enabled in the application:

- **Health (`/actuator/health`)**: Provides basic application health information
- **Metrics (`/actuator/metrics`)**: Exposes metrics collected from the application
- **Prometheus (`/actuator/prometheus`)**: Exposes metrics in a format that can be scraped by Prometheus
- **Info (`/actuator/info`)**: Displays application information

### Accessing Actuator Endpoints

Actuator endpoints can be accessed via HTTP at the base path `/actuator`:

```
http://localhost:8080/actuator
```

For specific endpoints:
```
http://localhost:8080/actuator/health
http://localhost:8080/actuator/metrics
http://localhost:8080/actuator/prometheus
```

### Custom Metrics Implementation

The application includes three custom metrics components that collect and expose detailed statistics about trainings, trainees, and trainers. These metrics are automatically registered with Micrometer, which makes them available through the metrics endpoint and for scraping by Prometheus.

#### 1. TrainingMetrics

This component captures general training statistics:

- **Counter: `training.creations`**: Tracks the total number of training sessions created in the system.
- **Summary: `training.duration`**: Collects statistics about the duration of training sessions in minutes, including percentiles.
- **Gauge: `training.total.count`**: Reports the current total number of training sessions.
- **Gauge: `training.today.count`**: Shows the number of training sessions scheduled for the current day.
- **Gauge: `training.weekly.count`**: Displays the number of training sessions scheduled for the current week.

#### 2. TraineeTrainingMetrics

This component focuses on trainee-specific training metrics:

- **Counter: `trainee.training.sessions`**: Counts the total number of training sessions attended by trainees.
- **Summary: `trainee.training.duration`**: Captures distribution statistics of training durations for trainees.
- **Gauge: `trainee.active.count`**: Monitors the current number of active trainees.
- **Gauge: `trainee.avg.training.count`**: Calculates the average number of trainings per trainee.

#### 3. TrainerTrainingMetrics

This component tracks trainer-related metrics:

- **Counter: `trainer.training.sessions`**: Counts the total number of training sessions conducted by trainers.
- **Summary: `trainer.training.duration`**: Records distribution of training durations for trainers.
- **Gauge: `trainer.active.count`**: Shows the current number of active trainers.
- **Gauge: `trainer.avg.training.count`**: Calculates the average number of trainings per trainer.

### Accessing Custom Metrics

All custom metrics can be viewed through the metrics endpoint. For example:

1. To see all available metrics:
   ```
   http://localhost:8080/actuator/metrics
   ```

2. To view a specific metric, append the metric name to the URL:
   ```
   http://localhost:8080/actuator/metrics/trainee.training.sessions
   http://localhost:8080/actuator/metrics/trainer.active.count
   http://localhost:8080/actuator/metrics/training.duration
   ```

3. For Prometheus integration, all metrics are available in Prometheus format at:
   ```
   http://localhost:8080/actuator/prometheus
   ```

4. Custom health indicators are grouped under:
   ```
   http://localhost:8080/actuator/health/custom
   ```

These metrics provide valuable insights into the performance and usage patterns of the application, allowing for better monitoring, capacity planning, and issue detection.

## Spring Profiles Implementation

The application implements a robust profile-based configuration strategy to seamlessly adapt to different environments: local development, development server, and production. This approach allows for environment-specific configurations without code changes.

### Profile Structure

The application uses a hierarchical configuration approach:

1. **Common Configuration** (`application.properties`):
   - Contains shared settings applicable across all environments
   - Includes base application name, JPA common settings, and basic Actuator configuration
   - Configures common Swagger/OpenAPI paths

2. **Environment-Specific Profiles**:
   - **Local Profile** (`application-local.properties`)
   - **Development Profile** (`application-dev.properties`) 
   - **Production Profile** (`application-prod.properties`)

### Local Profile (Development Workstation)

The `local` profile is optimized for development on a local machine:

- **Database**: Uses H2 in-memory database for quick startup and isolated testing
  ```
  spring.datasource.url=jdbc:h2:mem:trainingdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
  spring.datasource.driver-class-name=org.h2.Driver
  ```
- **H2 Console**: Enabled at `/h2-console` for easy database inspection
- **Hibernate**: Configured with `create-drop` strategy to reset the database on each restart
- **Logging**: Verbose `DEBUG` level logging for application code and framework components
- **Actuator**: All metrics enabled with full details for comprehensive monitoring during development
- **Development Tools**: Includes data initialization from `init_data.json` and enabled debug mode

**Running with Local Profile**:
```sh
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Alternatively, you can set the active profile directly in the base `application.properties` file:
```properties
spring.profiles.active=local
```

### Development Profile (Staging Environment)

The `dev` profile is designed for shared development or staging environments:

- **Database**: Uses PostgreSQL database for a production-like experience
  ```
  spring.datasource.url=jdbc:postgresql://localhost:5432/jpa_epam
  spring.datasource.username=postgres
  spring.datasource.password=postgres
  ```
- **Hibernate**: Uses `update` strategy to maintain schema between restarts while allowing updates
- **Logging**: `INFO` level for application and Spring, `WARN` for Hibernate to reduce noise
- **Actuator**: All metrics enabled with detailed health information

**Running with Development Profile**:
```sh
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Alternatively, you can set the active profile directly in the base `application.properties` file:
```properties
spring.profiles.active=dev
```

### Production Profile

The `prod` profile implements stricter settings suitable for production deployment:

- **Database**: Uses PostgreSQL with environment variables for sensitive credentials
  ```
  spring.datasource.url=jdbc:postgresql://${DB_PROD_HOST}:${DB_PROD_PORT}/jpa_epam
  spring.datasource.username=${DB_PROD_USER}
  spring.datasource.password=${DB_PROD_PASSWORD}
  ```
- **Connection Pooling**: Configures Hikari connection pool with optimized settings
- **Hibernate**: Uses `validate` strategy to verify schema without making changes
- **Logging**: Minimal `WARN` and `ERROR` levels to reduce overhead
- **Actuator**: Limited metrics with authorized-only health details
- **Security**: Disables stack traces and error messages in responses
- **API Documentation**: Disables Swagger UI and API docs in production

**Running with Production Profile**:

First, set the required environment variables:

**Windows (PowerShell)**:
```powershell
$env:DB_PROD_HOST="localhost"
$env:DB_PROD_PORT="5432"
$env:DB_PROD_USER="postgres"
$env:DB_PROD_PASSWORD="postgres"
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
```

Alternatively, you can set the active profile directly in the base `application.properties` file:
```properties
spring.profiles.active=prod
```

Remember to still set the required environment variables when using this method.

**Linux/macOS**:
```bash
export DB_PROD_HOST=localhost
export DB_PROD_PORT=5432
export DB_PROD_USER=postgres
export DB_PROD_PASSWORD=postgres
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Benefits of Profile-Based Configuration

1. **Environment Isolation**: Each environment has its appropriate settings without code changes
2. **Security**: Sensitive credentials are externalized for production
3. **Performance Tuning**: Each environment is optimized for its use case
4. **Developer Experience**: Local development is streamlined with appropriate tools
5. **Operational Control**: Production has stricter settings for stability and security

## Testing Strategy

1. **Unit Testing**:
   - **JUnit**: Used for writing unit tests for services and repositories. JUnit provides a robust framework for testing Java applications, ensuring that each unit of the application performs as expected.
   - **Mockito**: Utilized for mocking dependencies in unit tests. This allows for isolated testing of components by simulating the behavior of complex dependencies, ensuring that tests are focused and reliable.

2. **Repository Testing**:
   - **Spring Data Testing Support**: The application leverages Spring Boot's testing capabilities for repository layer testing through the `@DataJpaTest` annotation.
   - **Test Profile**: A dedicated test profile (`application-test.properties`) is created to ensure tests run against an in-memory H2 database rather than the actual production or development database.
   - **TestEntityManager**: Used for test setup and data preparation without relying on repository methods being tested.
   - **Isolated Database Operations**: Repository tests execute in isolated transactions that are rolled back after each test, ensuring test independence.
   - **Comprehensive Test Coverage**: Tests cover both standard JpaRepository methods and custom query methods defined with `@Query` annotations.
   
   Example configuration from `application-test.properties`:
   ```properties
   # H2 In-Memory Database configuration for tests
   spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
   spring.datasource.driver-class-name=org.h2.Driver
   
   # JPA settings for tests
   spring.jpa.hibernate.ddl-auto=create-drop
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   ```

3. **Controller Testing**:
   - **WebTestClient (WebFlux)**: Primarily used for testing controllers. It provides a fluent API for testing web applications, allowing for comprehensive testing of HTTP requests and responses.
   - **Alternative Testing Approaches**:
     - **RestTemplateTest**: Another approach used for the `TraineeController`, providing a synchronous way to test RESTful services in Spring Boot.
     - **MockMvc**: Employed for the `TraineeController` to simulate HTTP requests and test the controller's behavior in a servlet environment. MockMvc allows for testing without starting the server, making it efficient for unit tests.

4. **Best Practices**:
   - **FIRST Principles**: Tests are designed to be Fast, Independent, Repeatable, Self-validating, and Timely. This ensures that tests are efficient, reliable, and maintainable.
   - **AAA Pattern**: Tests follow the Arrange-Act-Assert pattern, which structures tests into three clear sections: setting up the test data and environment (Arrange), executing the code under test (Act), and verifying the results (Assert). This pattern promotes clarity and consistency in test design.
   - **Test Isolation**: Each test runs in isolation, with its own test data setup and tear-down, preventing interference between tests.
   - **Profile-Based Testing**: Using Spring profiles to switch between different test configurations, allowing for specialized test setups depending on the testing scenario.

## Logging and Monitoring

1. **Aspect-Oriented Logging**:
   - **Logging Aspect**: A custom `LoggingAspect` is implemented to provide detailed logging across the application. This aspect logs method invocations, return values, and exceptions for services, repositories, and controllers.
   - **Service Logging**: Logs the start and successful completion of service methods, including method arguments and return values. Exceptions are also logged with error details.
   - **Repository Logging**: Logs the start of repository operations and any exceptions that occur, providing insights into data access issues.
   - **Controller Logging**: Logs incoming REST requests, including HTTP method, URI, headers, and parameters. It also logs the response status and body, as well as any exceptions that occur during request handling.

2. **Logging Levels**:
   - **Configuration**: Logging levels are configured in `application.properties` to control the verbosity of logs. For example, `DEBUG` level is set for the application's package to capture detailed information, while `INFO` level is used for Spring and Hibernate to reduce noise.
   - **Transaction Logging**: Enabled for Spring transactions to monitor transaction boundaries and any issues that may arise during transaction management.

3. **Benefits**:
   - **Enhanced Debugging**: Detailed logs provide valuable insights into the application's behavior, making it easier to diagnose and resolve issues.
   - **Performance Monitoring**: By logging method execution times and transaction details, performance bottlenecks can be identified and addressed.
   - **Security and Compliance**: Logging access to sensitive resources and operations helps in auditing and ensuring compliance with security policies.

## Setting Up PostgreSQL with Docker

1. **Docker Compose Setup**:
   - The project uses Docker Compose to manage PostgreSQL and pgAdmin services. The `docker-compose.yml` file defines the configuration for these services.

2. **Running the PostgreSQL and pgAdmin Containers**:
   - To start the containers, run the following command in the terminal:
     ```sh
     docker-compose up -d
     ```
   - This command will start the PostgreSQL database and pgAdmin in detached mode, allowing you to continue using the terminal.

3. **Accessing the Database**:
   - **pgAdmin**: You can access pgAdmin by navigating to `http://localhost:5050` in your web browser. Log in with the following credentials:
     - **Email**: `admin@example.com`
     - **Password**: `admin`
   - **PostgreSQL**: The database is accessible on port `5432`. Use the following connection details:
     - **Host**: `postgres`
     - **Port**: `5432`
     - **Database Name**: `jpa_epam`
     - **Username**: `postgres`
     - **Password**: `postgres`

4. **Managing Credentials**:
   - The credentials for pgAdmin and PostgreSQL are defined in the `docker-compose.yml` file and the `pgadmin-servers.json` file. The `pgadmin-servers.json` file is mounted as a volume in the pgAdmin container to pre-configure the server connection.

5. **Stopping and Removing Containers**:
   - To stop the containers, use the following command:
     ```sh
     docker-compose down
     ```
   - To remove all containers, networks, and volumes, erasing PostgreSQL data, use:
     ```sh
     docker-compose down -v
     ```

6. **Volume Management**:
   - The PostgreSQL data is stored in a Docker volume named `postgres_data`, ensuring that data persists across container restarts.
