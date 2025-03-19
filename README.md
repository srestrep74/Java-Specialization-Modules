# Hibernate JPA Project - EPAM Training

### Developed By : Sebastián Restrepo Ortiz

## Project Overview
This project is developed as part of the EPAM Java Specialization Training, focusing on the implementation of a Hibernate JPA-based application. The system is designed to manage trainers and trainees, providing functionalities such as authentication, training assignments, user activations, and profile management. The project follows best practices in software design, including SOLID principles, dependency injection, transaction management, and logging with AOP.

## Features Implemented

### 1. Trainer Management
- **Create Trainer Profile**: Allows adding a new trainer with automatically generated credentials.
- **Update Trainer Profile**: Enables modification of trainer details.
- **Change Trainer Password**: Implements secure password update functionality.
- **Activate/Deactivate Trainer Profile**: Allows enabling or disabling trainer accounts.
- **Retrieve Trainer Profile by Username**: Fetches trainer information based on their username.
- **Get Trainer's Training List**: Provides a list of trainings assigned to a trainer, with filtering options such as date range, trainee name, and training type.

### 2. Trainee Management
- **Create Trainee Profile**: Adds a new trainee with an auto-generated username and password.
- **Update Trainee Profile**: Allows modifying trainee details.
- **Change Trainee Password**: Secure password update functionality.
- **Activate/Deactivate Trainee Profile**: Enables or disables trainee accounts.
- **Retrieve Trainee Profile by Username**: Fetches trainee information based on their username.
- **Get Trainee's Training List**: Lists trainings assigned to a trainee, with filters for date range, trainer name, and training type.

### 3. Training Management
- **Assign Trainers to Trainees**: Supports many-to-many relationships between trainers and trainees.
- **Add Training Sessions**: Links training sessions to both trainers and trainees.
- **Remove a Trainee**: Implements hard deletion with cascading effects on related trainings.
- **Retrieve Unassigned Trainers for a Trainee**: Fetches trainers that have not been assigned to a specific trainee.

## Architecture and Best Practices
### **1. Design Principles**
- **SOLID Principles**: Ensures maintainability and scalability.
- **Separation of Concerns**: Applied via layered architecture (Controllers → Services → Repositories → Entities).
- **Dependency Inversion & Injection**: Managed using Spring Boot’s `@Service`, `@Repository`, and `@Component` annotations.
- **DTO (Data Transfer Objects)**: Used to separate persistence and business logic layers.

### **2. Spring **
- **Spring Beans & Auto Scanning**: Automatic discovery and management of components.
- **Service Layer with Transaction Management**: Ensures data consistency.
- **Aspect-Oriented Programming (AOP) Logging**: Implements centralized logging of method calls and execution times.

## Database Schema and Relations
- **User Table**: Parent table with a inheritance relationship with `Trainer` and `Trainee` tables.
- **Trainer & Trainee**: Many-to-Many relationship.
- **Training**: Linked to both `Trainer` and `Trainee` via foreign keys.
- **Training Type**: A static table storing predefined training types.

### **Data Types and Constraints**
- **isActive Field**: Boolean type indicating whether a profile is active.
- **Training Date**: Stored as `Date` type.
- **Training Duration**: Numeric field representing session length.
- **Cascade Deletion**: Removing a trainee deletes all their associated training records.

## Implementation Details
### **1. Entities and Relationships**
Entities are designed using JPA annotations such as `@Entity`, `@Table`, `@OneToOne`, `@ManyToMany`, and `@JoinColumn` to define relationships.

### **2. Repositories**
Implemented using Hibernate JPA with `@Repository` annotation, managing `EntityManager` and `Transaction` management.

### **3. Services**
Service layer implements business logic .

### **4. Facades**
Facade pattern is used to encapsulate complex interactions between services, reducing controller complexity.

### **5. Menus**
A CLI-based interactive menu is implemented to provide a structured interface for user interactions.

## Testing Strategy
### **1. Unit Testing**
- **JUnit**: Used for writing unit tests for services, repositories, and facades.
- **Mockito**: Used for mocking dependencies to ensure isolated testing.

### **2. Coverage and Best Practices**
- High test coverage ensures minimal regression issues.
- Assertions validate business logic.
- Mocked dependencies allow independent testing of components.

## Logging and Monitoring
### **1. AOP-Based Logging**
- Implemented using Spring AOP to log method calls, execution times, and exceptions.
- Centralized logging strategy ensures maintainability and debugging efficiency.

### **2. Log Levels**
- **INFO**: General application flow.
- **DEBUG**: Detailed information for troubleshooting.
- **ERROR**: Critical issues requiring immediate attention.

## Setting Up PostgreSQL with Docker
### **1. Running PostgreSQL Container**
```sh
 docker-compose up -d
```

### **2. Accessing the Database**
Navigate to `http://localhost:5050` and log in with:
- **Email**: `admin@example.com`
- **Password**: `admin`

### **3. Configuring the Database Connection**
- **Host**: `postgres`
- **Port**: `5432`
- **Database Name**: `jpa_epam`
- **Username**: `postgres`
- **Password**: `postgres`

### **4. Stopping Containers**
```sh
 docker-compose down
```

### **5. Removing Data Permanently (Optional)**
```sh
 docker-compose down -v
```
This removes all containers, networks, and volumes, erasing PostgreSQL data.


