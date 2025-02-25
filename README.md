# SpringCoreTask1

**Developed By** : Sebastián Restrepo Ortiz

## Overview
SpringCoreTask1 is a Java application developed using **Spring Core**, making extensive use of **dependency injection (DI)**, the **Spring container**, and best practices in Spring-based development. The application is designed to manage training-related entities such as trainees, trainers, and training sessions while following a structured **layered architecture**. The project leverages **Spring Beans**, **annotations**, and **Java-based configuration** to ensure modularity and maintainability. Additionally, rigorous **unit tests** were conducted for both the service and DAO layers to validate the application's functionality and data integrity.

## Features
- **Trainee Service:** Create, update, delete, and retrieve trainee profiles.
- **Trainer Service:** Create, update, and retrieve trainer profiles.
- **Training Service:** Create and retrieve training sessions.
- **In-memory storage:** Uses a `java.util.Map` to store entities under separate namespaces.
- **Preloaded data:** Initializes storage with data from a JSON file using Spring bean post-processing.
- **Auto-wiring and dependency injection:** Uses constructor and setter-based injection.
- **Testing:** Comprehensive unit tests were implemented for both services and DAOs.

## Architecture
The application follows a well-defined **multi-layered architecture** to promote separation of concerns and maintainability:

### 1. **Presentation Layer (Console Menu)**
- `ConsoleMenu.java` provides a simple console-based UI for interacting with the application.

### 2. **Service Layer**
- `TraineeService.java`, `TrainerService.java`, and `TrainingService.java` handle business logic.
- Services interact with DAO objects to retrieve and manipulate data.
- **Unit tests** were conducted to verify service logic and interactions with DAOs.

### 3. **DAO Layer**
- DAO interfaces (`TraineeDAO`, `TrainerDAO`, `TrainingDAO`) define data access methods.
- Implementations (`TraineeDAOImpl`, `TrainerDAOImpl`, `TrainingDAOImpl`) handle CRUD operations using in-memory storage.
- `BaseDAO.java` provides common functionalities for DAOs.
- **Unit tests** were also conducted for DAOs to ensure data persistence and retrieval consistency.

### 4. **Data Transfer Objects (DTOs)**
- `TraineeDTO.java`, `TrainerDTO.java`, `TrainingDTO.java` facilitate data transfer between layers.

### 5. **Mappers**
- `TraineeMapper.java`, `TrainerMapper.java`, `TrainingMapper.java` convert between DTOs and domain models.

### 6. **Storage (In-Memory and Initialization)**
- `InitialData.java` loads predefined data from `init_data.json`.
- `JsonFileReader.java` handles JSON parsing.

### 7. **Configuration**
- `AppConfig.java` configures **Spring Beans** and manages the application context.
- `application.properties` sets application properties like data file paths.

### 8. **Exception Handling**
- Custom exceptions: `EntityNotFoundException.java`, `StorageInitializationException.java`.

### 9. **Testing**
- **Unit tests for services:**
  - `TraineeServiceTest.java`, `TrainerServiceTest.java`, `TrainingServiceTest.java` ensure business logic correctness.
- **Unit tests for DAOs:**
  - DAO layer was thoroughly tested to verify CRUD operations.
- **Integration testing:**
  - `SpringCoreTask1ApplicationTests.java` contains integration tests to validate the entire application workflow.

## Setup and Execution
### Prerequisites
- Java 17+
- Maven 3+
- Spring Core

### Steps to Run
1. Clone the repository:
   ```sh
   git clone <repository_url>
   cd SpringCoreTask1
   ```
2. Build the project:
   ```sh
   mvn clean install
   ```
3. Run the application:
   ```sh
   mvn spring-boot:run
   ```

This project showcases core **Spring Core** principles, including **dependency injection, Spring Beans, application context management, and layered architecture**, while following best practices in modular and maintainable software development.

