# Spring Boot Microservices Gym Management System

A comprehensive gym management system built with Spring Boot microservices architecture, demonstrating enterprise-level patterns and best practices for distributed systems.

## 🏗️ Project Overview

This project implements a complete gym management solution using microservices architecture with two main business domains:

- **Gym Service**: Manages trainers, trainees, training sessions, and user authentication
- **Workload Service**: Handles trainer workload tracking and monthly summaries

The system follows Domain-Driven Design (DDD) principles and implements various microservices patterns to ensure scalability, reliability, and maintainability.

## 🚀 Architecture & Design Patterns

### Microservices Patterns
- **API Gateway Pattern**: Centralized routing and cross-cutting concerns
- **Service Discovery**: Eureka server for service registration and discovery
- **Configuration Management**: External configuration with Spring Cloud Config
- **Circuit Breaker Pattern**: Resilience4j for fault tolerance
- **Database per Service**: Each microservice has its own database
- **Asynchronous Messaging**: ActiveMQ for decoupled inter-service communication

### Design Patterns & Best Practices
- **Clean Architecture**: Clear separation of concerns and dependency inversion
- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Data transfer objects for API communication
- **Builder Pattern**: For complex object construction
- **Specification Pattern**: For dynamic query building

### Enterprise Patterns
- **JWT Authentication**: Stateless authentication with refresh tokens
- **Rate Limiting**: Protection against abuse
- **Distributed Tracing**: Request tracking across services
- **Health Checks**: Service monitoring and availability
- **Metrics Collection**: Performance and business metrics
- **Centralized Logging**: Structured logging with correlation IDs

## 🛠️ Technologies Stack

### Core Framework
- **Spring Boot 3.3+**: Application framework
- **Spring Cloud 2023.x**: Microservices toolkit
- **Java 21**: Programming language with latest features

### Data & Persistence
- **PostgreSQL**: Primary database for Gym Service
- **MySQL**: Database for Workload Service
- **Spring Data JPA**: Data access layer
- **Hibernate**: ORM framework
- **Redis**: JWT token storage and caching

### Infrastructure & DevOps
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration
- **Maven**: Build automation

### Microservices Components
- **Eureka Server**: Service discovery
- **Spring Cloud Gateway**: API gateway and routing
- **Spring Cloud Config**: Centralized configuration
- **ActiveMQ**: Asynchronous messaging for inter-service communication
- **Resilience4j**: Circuit breaker and resilience patterns

### Monitoring & Observability
- **Micrometer**: Metrics collection
- **Prometheus**: Metrics storage and alerting
- **Grafana**: Metrics visualization and dashboards
- **Zipkin**: Distributed tracing
- **Spring Boot Actuator**: Health checks and endpoints

### Security & Authentication
- **Spring Security**: Authentication and authorization
- **JWT**: Stateless authentication tokens
- **BCrypt**: Password hashing
- **CORS**: Cross-origin resource sharing

### Documentation & Testing
- **OpenAPI 3 (Swagger)**: API documentation
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework

### Database Management
- **pgAdmin**: PostgreSQL administration
- **phpMyAdmin**: MySQL administration
- **RedisInsight**: Redis management interface

## 📋 System Requirements

### Prerequisites
- **Docker Desktop**: Latest version
- **Docker Compose**: v2.0+
- **Java 21**: For local development (optional)
- **Maven 3.9+**: For local builds (optional)
- **Git**: Version control

### System Resources
- **RAM**: Minimum 8GB (16GB recommended)
- **CPU**: Dual-core (Quad-core recommended)
- **Storage**: 10GB free space
- **Network**: Internet access for downloading images

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd SpringCoreTask1
```

### 2. Start Infrastructure Services
```bash
# Start databases, monitoring, and infrastructure
docker-compose -f docker-compose-infrastructure.yml up -d
```

This will start:
- PostgreSQL (port 5432) + pgAdmin (port 5050)
- MySQL (port 3307) + phpMyAdmin (port 8080)
- Redis (port 6379) + RedisInsight (port 5540)
- ActiveMQ (port 61616) + ActiveMQ Web Console (port 8161)
- Prometheus (port 9090)
- Grafana (port 3000)
- Zipkin (port 9411)

### 3. Start Microservices
```bash
# Start all microservices
docker-compose -f docker-compose-services.yml up -d
```

This will start:
- Config Server (port 8888)
- Eureka Server (port 8761)
- API Gateway (port 8083)
- Gym Service (port 8081)
- Workload Service (port 8082)

### 4. Verify Services
- **Eureka Dashboard**: http://localhost:8761
- **API Gateway Health**: http://localhost:8083/actuator/health
- **Gym Service Swagger**: http://localhost:8081/swagger-ui.html
- **Workload Service Swagger**: http://localhost:8082/swagger-ui.html
- **ActiveMQ Web Console**: http://localhost:8161 (admin/admin)
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090
- **Zipkin**: http://localhost:9411

## 📊 Service Endpoints

### API Gateway Routes
- **Gym Service**: `http://localhost:8083/api/v1/{trainees|trainers|trainings|training-types|auth}/**`
- **Workload Service**: `http://localhost:8083/api/v1/workloads/**`

### Direct Service Access
- **Gym Service**: `http://localhost:8081/api/v1/**`
- **Workload Service**: `http://localhost:8082/api/v1/**`

## 🔧 Configuration

### Environment Variables
Key environment variables can be configured:

```bash
# Database Configuration
POSTGRES_DB=jpa_epam
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

MYSQL_DATABASE=workload_db
MYSQL_USER=workload_user
MYSQL_PASSWORD=workload_pass

# Service Configuration
EUREKA_SERVER_URL=http://eureka-server:8761/eureka/
ZIPKIN_SERVER_URL=http://zipkin:9411/api/v2/spans
JWT_SECRET=your-secret-key
```

### Service Ports
- Config Server: 8888
- Eureka Server: 8761
- API Gateway: 8083
- Gym Service: 8081
- Workload Service: 8082
- ActiveMQ: 61616
- ActiveMQ Web Console: 8161

## 🛑 Stopping Services

```bash
# Stop all services
docker-compose -f docker-compose-services.yml down
docker-compose -f docker-compose-infrastructure.yml down

# Remove volumes (optional - removes all data)
docker-compose -f docker-compose-infrastructure.yml down -v
```

## 📖 Documentation

- **API Documentation**: Available via Swagger UI at each service endpoint
- **Architecture Diagrams**: See `/docs` folder
- **Monitoring Dashboards**: Pre-configured Grafana dashboards
- **Health Checks**: All services expose `/actuator/health` endpoints

## 🧪 Testing

```bash
# Run unit tests
./mvnw test

# Run integration tests
./mvnw verify -P integration-tests
```

## 🔍 Monitoring & Observability

- **Health Monitoring**: Spring Boot Actuator endpoints
- **Metrics**: Micrometer + Prometheus + Grafana
- **Distributed Tracing**: Zipkin with B3 propagation
- **Logging**: Structured JSON logs with correlation IDs
- **Circuit Breaker**: Resilience4j monitoring

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🏆 Key Features Demonstrated

- ✅ Microservices Architecture
- ✅ Service Discovery & Registration
- ✅ API Gateway Pattern
- ✅ Circuit Breaker Pattern
- ✅ Distributed Configuration
- ✅ JWT Authentication
- ✅ Database per Service
- ✅ Distributed Tracing
- ✅ Metrics & Monitoring
- ✅ Containerization
- ✅ Health Checks
- ✅ Asynchronous Inter-service Communication (ActiveMQ)
- ✅ Resilience Patterns
- ✅ Clean Architecture
- ✅ Test-Driven Development 