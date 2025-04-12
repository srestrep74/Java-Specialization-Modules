# Spring REST Project - EPAM Training

### Developed By : Sebastián Restrepo Ortiz

## Project Overview
This project is developed as part of the EPAM Java Specialization Training, focusing on the implementation of a Spring REST-based application. The system is designed to manage trainers and trainees, providing functionalities such as authentication, training assignments, user activations, and profile management. The project follows best practices in software design, including SOLID principles, dependency injection, transaction management, and logging with AOP. Additionally, REST best practices are applied, such as URI naming conventions, API versioning, and endpoint documentation.

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
   - **Hibernate and JPA**: The repository layer is implemented using Hibernate and JPA with the `@Repository` annotation. This layer is responsible for data access and persistence, providing CRUD operations and complex queries. Although transaction management has been moved to the service layer, repositories still utilize Hibernate and JPA for efficient data handling.

## Testing Strategy

1. **Unit Testing**:
   - **JUnit**: Used for writing unit tests for services and repositories. JUnit provides a robust framework for testing Java applications, ensuring that each unit of the application performs as expected.
   - **Mockito**: Utilized for mocking dependencies in unit tests. This allows for isolated testing of components by simulating the behavior of complex dependencies, ensuring that tests are focused and reliable.

2. **Controller Testing**:
   - **WebTestClient (WebFlux)**: Primarily used for testing controllers. It provides a fluent API for testing web applications, allowing for comprehensive testing of HTTP requests and responses.
   - **Alternative Testing Approaches**:
     - **RestTemplateTest**: Another approach used for the `TraineeController`, providing a synchronous way to test RESTful services in Spring Boot.
     - **MockMvc**: Employed for the `TraineeController` to simulate HTTP requests and test the controller's behavior in a servlet environment. MockMvc allows for testing without starting the server, making it efficient for unit tests.

3. **Best Practices**:
   - **FIRST Principles**: Tests are designed to be Fast, Independent, Repeatable, Self-validating, and Timely. This ensures that tests are efficient, reliable, and maintainable.
   - **AAA Pattern**: Tests follow the Arrange-Act-Assert pattern, which structures tests into three clear sections: setting up the test data and environment (Arrange), executing the code under test (Act), and verifying the results (Assert). This pattern promotes clarity and consistency in test design.

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
