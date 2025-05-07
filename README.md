# Spring Security Project - EPAM Training

### Developed By : Sebastián Restrepo Ortiz

## Project Overview
This project is developed as part of the EPAM Java Specialization Training, implementing a comprehensive Spring Boot application for training management. The system provides robust functionality for managing trainers and trainees, including user registration, authentication, training assignments, profile management, and reporting.

The application leverages core Spring Boot capabilities and follows modern microservice architecture principles:

- **Spring Data JPA**: For simplified, efficient database access with minimal boilerplate code
- **Spring Boot Actuator**: For production-ready monitoring and metrics collection
- **Spring Security with JWT**: Implementation of secure authentication and authorization using JWT tokens
- **Token Management**: Redis-based token storage for production environments and in-memory storage for local development
- **Environment Profiles**: Configured profiles (local, dev, production) for seamless deployment across environments
- **RESTful API Design**: Following best practices including versioning, consistent response structures, and proper HTTP method usage
- **Advanced Repository Patterns**: Using Specifications and custom queries for complex data operations
- **Comprehensive Testing**: Multi-layered testing strategy with specialized application profiles for testing

The project adheres to software engineering best practices including SOLID principles, dependency injection, AOP for cross-cutting concerns, comprehensive exception handling, and standardized response formats.


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

## Spring Security Implementation

This project implements a comprehensive security solution using Spring Security framework combined with JWT (JSON Web Tokens) for authentication and authorization. The implementation provides stateless security, role-based access control, token blacklisting, and protection against common security threats.

### 1. Security Configuration

The `SecurityConfig` class is the central component that configures Spring Security:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    // Configuration implementation
}
```

Key security features implemented:

- **Stateless Authentication**: Using JWT tokens instead of session-based authentication
- **CORS Configuration**: Configured for cross-origin requests
- **CSRF Protection**: Disabled for stateless REST API (compensated by other security measures)
- **Public Endpoints**: Registration and authentication endpoints are publicly accessible
- **Protected Resources**: All other endpoints require authentication
- **Custom Exception Handling**: Using `CustomAuthenticationEntryPoint` and `CustomAccessDeniedHandler`
- **Method-Level Security**: Enabled with `@EnableMethodSecurity`

### 2. User Entity with Role-Based Authorization

The `User` abstract class includes role-based authorization capabilities:

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User {
    // Other fields omitted for brevity
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleType role;
    
    public String getRoleName() {
        return role.name();
    }
}
```

This design enables:
- Role-based access control using Spring Security's authorities
- Hierarchical user structure with Trainer and Trainee entities extending the base User
- Integration with Spring Security's authorization mechanism

### 3. JWT Authentication Filter

The `JwtAuthenticationFilter` intercepts all requests to validate JWT tokens:

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Implementation details
}
```

The filter's responsibilities include:
- Extracting the JWT token from the Authorization header
- Validating the token and checking if it's blacklisted
- Setting up the Spring Security context with user authentication
- Handling various token validation exceptions

This filter enables stateless authentication where each request must include a valid JWT token in the Authorization header.

### 4. Custom User Details Service

The `CustomUserDetailsService` bridges our application's user model with Spring Security:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    // Implementation details
}
```

Key features:
- Loads user details from our database using either `traineeRepository` or `trainerRepository`
- Converts our domain `User` objects to Spring Security's `UserDetails`
- Provides password validation during authentication
- Creates `CustomUserDetails` objects with appropriate authorities based on user roles

### 5. JWT Utility

The `JwtUtil` class handles all JWT-related operations:

```java
@Component
public class JwtUtil {
    // Implementation details
}
```

This utility provides:
- Token generation for both access and refresh tokens
- Token validation and parsing
- Claims extraction (username, roles, expiration)
- Token ID extraction for blacklisting purposes
- Secure signing using HMAC-SHA512 algorithm with a Base64-encoded secret key

The implementation uses the `jjwt` library and includes security checks such as:
- Minimum key length verification
- Token expiration validation
- Exception handling for malformed tokens

### 6. Login Attempt Service

The `LoginAttemptService` implements protection against brute force attacks:

```java
@Service
public class LoginAttemptService {
    // Implementation details
}
```

Features include:
- Tracking failed login attempts per username
- Configurable maximum attempts (default: 3)
- Configurable lockout duration (default: 5 minutes)
- Auto-expiration of lockouts after the specified duration
- Attempt counter reset on successful authentication

This service uses a thread-safe `ConcurrentHashMap` to track login attempts and provides methods to register failures, check if an account is blocked, and get remaining attempts.

### 7. Token Blacklisting System

The application implements token blacklisting to invalidate tokens before their natural expiration (e.g., during logout). This is implemented using the `TokenStorageService` interface with two implementations:

#### In-Memory Implementation (Local Environment)

```java
@Service
@Profile("local")
public class InMemoryTokenStorageServiceImpl implements TokenStorageService {
    // Implementation details
}
```

Features:
- Uses `ConcurrentHashMap` for thread-safe token storage
- Scheduled task to automatically clean up expired tokens
- Separate storage for blacklisted tokens and refresh tokens
- Efficient token validation with automatic cleanup of expired entries

#### Redis Implementation (Development and Production)

```java
@Service
@Profile("dev")
public class RedisTokenStorageServiceImpl implements TokenStorageService {
    // Implementation details
}
```

Features:
- Uses Redis for distributed token storage
- Automatic expiration handled by Redis TTL mechanism
- Scales horizontally across multiple application instances
- Persistent storage that survives application restarts
- Efficient token blacklist checking using Redis key lookups

### 8. Environment-Specific Token Storage

The token storage system adapts to different environments through Spring profiles:

1. **Local Development**:
   - Uses in-memory implementation for simplicity
   - Configures more frequent cleanup intervals
   - Disables Redis dependencies

2. **Development/Production**:
   - Uses Redis-based implementation
   - Configures Redis connection properties
   - Enables distributed token storage

This approach ensures that:
- Developers can run the application locally without external dependencies
- Production environments have robust, distributed token storage
- The application can scale horizontally with proper token invalidation

### 9. Security Best Practices Implemented

The security implementation follows several best practices:

- **Secure Password Storage**: Using BCrypt hashing
- **Token-Based Authentication**: Stateless JWT tokens with proper signing
- **Short-Lived Access Tokens**: 15-minute default expiration
- **Refresh Token Rotation**: New refresh tokens issued when refreshing authentication
- **Token Blacklisting**: Prevents use of logged-out tokens
- **Brute Force Protection**: Account lockout after failed attempts
- **Custom Error Responses**: Structured, informative but secure error messages
- **Role-Based Access Control**: Different permissions for trainers and trainees
- **Method-Level Security**: Fine-grained access control at method level

This comprehensive security implementation provides robust protection for the application while maintaining usability and performance.

## Token Management and Blacklisting System

The application implements a sophisticated token management system to handle JWT authentication tokens securely across different environments. This system is crucial for maintaining security when using stateless JWT tokens, particularly for supporting logout functionality and token revocation.

### Core Design Principles

The token management system is built on these key principles:

1. **Environment-Specific Implementation**: Different storage strategies based on the runtime environment
2. **Interface-Based Abstraction**: Common interface with environment-specific implementations
3. **Automatic Token Expiration**: Tokens automatically expire after their designated lifetime
4. **Blacklisting Mechanism**: Ability to invalidate tokens before their natural expiration
5. **Refresh Token Management**: Secure storage and rotation of refresh tokens

### TokenStorageService Interface

At the core of the system is the `TokenStorageService` interface that defines the contract for token management:

```java
public interface TokenStorageService {
    void blacklistToken(String tokenId, Instant expiryDate);
    boolean isTokenBlacklisted(String tokenId);
    void storeRefreshToken(String username, String tokenId);
    Set<String> getUserRefreshTokens(String username);
    void removeRefreshToken(String username, String tokenId);
    void clearUserRefreshTokens(String username);
}
```

This interface provides methods to:
- Blacklist tokens (for logout and token revocation)
- Check if a token is blacklisted (during authentication)
- Store and manage refresh tokens
- Retrieve all refresh tokens for a user
- Remove specific or all refresh tokens

### Redis-Based Implementation for Production

For development and production environments, the system uses Redis as a distributed token store. This implementation offers several advantages:

```java
@Service
@Profile("dev")
public class RedisTokenStorageServiceImpl implements TokenStorageService {
    // Implementation using Redis
}
```

#### Key Features of Redis Implementation

1. **Distributed Token Storage**: Redis provides a centralized storage accessible by all application instances, essential for scaled deployments.

2. **Automatic Expiration**: Redis time-to-live (TTL) mechanism is leveraged to automatically expire blacklisted tokens:
   ```java
   redisTemplate.opsForValue().set(key, "1", timeToExpiry.toMillis(), TimeUnit.MILLISECONDS);
   ```
   This ensures that the blacklist doesn't grow indefinitely, as Redis automatically removes expired keys.

3. **Efficient Token Validation**: O(1) lookup time to check if a token is blacklisted:
   ```java
   return Boolean.TRUE.equals(redisTemplate.hasKey(key));
   ```

4. **Set-Based Refresh Token Storage**: Uses Redis Sets to efficiently store and manage refresh tokens per user:
   ```java
   redisTemplate.opsForSet().add(key, tokenId);
   redisTemplate.expire(key, refreshTokenExpiryDays, TimeUnit.DAYS);
   ```

5. **Persistence Across Restarts**: Token information persists even if the application restarts, crucial for production systems.

### Redis Configuration

The Redis connection is configured based on the environment:

```properties
# For development environment
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=5000
spring.data.redis.password=
spring.cache.type=redis
```

For production, these values are typically externalized using environment variables for security.

### In-Memory Implementation for Local Development

For local development, an in-memory implementation is provided to eliminate the need for a Redis server:

```java
@Service
@Profile("local")
public class InMemoryTokenStorageServiceImpl implements TokenStorageService {
    // Implementation using ConcurrentHashMap
}
```

#### Key Features of In-Memory Implementation

1. **ConcurrentHashMap Storage**: Thread-safe storage using Java's concurrent collections:
   ```java
   private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();
   private final Map<String, Set<String>> userRefreshTokens = new ConcurrentHashMap<>();
   ```

2. **Scheduled Cleanup**: Uses Spring's scheduled tasks to periodically remove expired tokens:
   ```java
   @Scheduled(fixedDelayString = "${jwt.blacklist.cleanup-interval}")
   public void cleanupExpiredTokens() {
       Instant now = Instant.now();
       blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
   }
   ```

3. **On-Demand Cleanup**: Additionally performs cleanup during token validation:
   ```java
   if (expiryDate.isBefore(Instant.now())) {
       blacklistedTokens.remove(key);
       return false;
   }
   ```

### Token Lifecycle Management

The system handles the complete lifecycle of access and refresh tokens:

1. **Token Creation**: When a user logs in, both access and refresh tokens are created.

2. **Token Storage**: Refresh tokens are stored with user association for future validation.

3. **Token Refresh**: When an access token expires, the user can request a new one using their refresh token.

4. **Token Revocation**:
   - When a user logs out, all their refresh tokens are invalidated
   - Specific tokens can be invalidated (e.g., for security reasons)
   - When refreshing tokens, the old refresh token is invalidated

5. **Token Expiration**: Tokens have a natural expiration time:
   - Access tokens: 15 minutes by default
   - Refresh tokens: 7 days by default

### Security Benefits

This token management system provides several security benefits:

1. **Effective Logout**: Even though JWT tokens are stateless, the system provides true logout functionality.

2. **Session Revocation**: Ability to immediately revoke user sessions in case of security concerns.

3. **Refresh Token Rotation**: New refresh tokens are issued when refreshing authentication, limiting the window of opportunity if a refresh token is compromised.

4. **Scalability**: Works effectively in distributed environments with multiple application instances.

5. **Defense in Depth**: Combines the advantages of stateless authentication with the security benefits of session management.

This system balances security needs with performance considerations, ensuring that the application remains secure while maintaining the scalability benefits of JWT-based authentication.

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


## Setting Up Database Services with Docker

This project uses Docker Compose to easily set up all the required database services: PostgreSQL for the main application data and Redis for token storage. The setup is configured in the `docker-compose.yml` file.

### Prerequisites

- Docker and Docker Compose installed on your system
- Basic understanding of Docker commands

### Starting All Services

To start all database services (PostgreSQL, pgAdmin, and Redis) with a single command:

     ```sh
     docker-compose up -d
     ```

This command launches all services in detached mode, allowing you to continue using the terminal.

### PostgreSQL Setup

The PostgreSQL database is configured with the following details:

- **Container Name**: `postgres_db`
- **Port**: `5432` (mapped to host port `5432`)
     - **Database Name**: `jpa_epam`
     - **Username**: `postgres`
     - **Password**: `postgres`
- **Data Volume**: `postgres_data` (data persists across container restarts)

#### Accessing PostgreSQL

You can connect to PostgreSQL directly using any database client with the connection details above, or use the included pgAdmin web interface.

#### pgAdmin Access

pgAdmin provides a web interface to manage your PostgreSQL database:

- **URL**: `http://localhost:5050`
- **Login Email**: `admin@example.com`
- **Login Password**: `admin`

The server connection to PostgreSQL is pre-configured through the mounted `pgadmin-servers.json` file.

### Redis Setup

Redis is used for token storage in development and production environments:

- **Container Name**: `redis-jwt`
- **Port**: `6379` (mapped to host port `6379`)
- **Persistence**: Enabled with append-only file mode
- **Data Volume**: `redis_data` (data persists across container restarts)

#### Accessing Redis

You can connect to the Redis CLI for inspection and debugging:

     ```sh
docker exec -it redis-jwt redis-cli
```

Common Redis commands for working with the application:

```sh
# List all blacklisted tokens
KEYS blacklisted_token:*

# Check if a token is blacklisted
KEYS blacklisted_token:<token_id>

# List all refresh tokens for a user
SMEMBERS user:refresh_tokens:<username>

#### Stopping Services

     ```sh
# Stop but preserve volumes and data
docker-compose down

# Stop and remove volumes (erases all data)
     docker-compose down -v
     ```

#### Restarting Services

```sh
docker-compose restart
```

### Container Network

All services are connected via the `app_network` bridge network, allowing them to communicate with each other using their service names as hostnames.

When configuring your application to connect to these services, use the following connection details:

- **PostgreSQL**: `postgres:5432` (from other containers) or `localhost:5432` (from host)
- **Redis**: `redis:6379` (from other containers) or `localhost:6379` (from host)
