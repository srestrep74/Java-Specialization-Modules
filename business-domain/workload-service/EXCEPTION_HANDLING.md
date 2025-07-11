# 🛡️ Workload Service - Exception Handling System

## Overview

El workload-service ahora implementa un sistema de manejo de excepciones robusto y consistente, siguiendo el mismo patrón establecido en gym-service. Este sistema proporciona respuestas de error estandarizadas y una arquitectura limpia de separación de responsabilidades.

## 🏗️ Arquitectura del Sistema

### Componentes Principales

1. **ApiStandardError** - Estructura estándar de respuesta de error
2. **ErrorResponseBuilder** - Constructor de respuestas de error consistentes
3. **Excepciones Personalizadas** - Excepciones específicas del dominio
4. **GlobalExceptionHandler** - Manejo centralizado de todas las excepciones
5. **Controllers Limpios** - Sin try-catch, excepciones se propagan automáticamente

## 🎯 Excepciones Personalizadas del Dominio

### TrainerNotFoundException
```java
// Lanzada cuando no se encuentra un trainer
throw new TrainerNotFoundException("john.doe");
```

### WorkloadProcessingException
```java
// Lanzada cuando hay errores procesando workload
throw new WorkloadProcessingException(trainerUsername, "processTrainerWorkload", cause);
```

### InvalidWorkloadDataException
```java
// Lanzada cuando los datos del workload son inválidos
throw new InvalidWorkloadDataException("trainingDuration", "-5", "Training duration must be positive");
```

### MonthlySummaryNotFoundException
```java
// Lanzada cuando no se encuentra resumen mensual
throw new MonthlySummaryNotFoundException("john.doe", 2024, 3);
```

## 📋 Estructura de Respuesta de Error

### ApiStandardError
```json
{
  "timestamp": "2024-01-15T10:30:45.123",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Trainer not found with username: john.doe",
  "path": "/api/v1/trainers/workload"
}
```

### Campos de la Respuesta
- **timestamp**: Momento exacto cuando ocurrió el error
- **status**: Código de estado HTTP
- **error**: Tipo de error categorizado
- **message**: Mensaje descriptivo del error
- **path**: Endpoint donde ocurrió el error

## 🚀 Mapeo de Excepciones a Respuestas HTTP

| Excepción | Status Code | Error Type | Uso |
|-----------|-------------|------------|-----|
| `TrainerNotFoundException` | 404 | Resource Not Found | Trainer no encontrado |
| `MonthlySummaryNotFoundException` | 404 | Resource Not Found | Resumen mensual no encontrado |
| `InvalidWorkloadDataException` | 400 | Validation Error | Datos de workload inválidos |
| `WorkloadProcessingException` | 500 | Internal Server Error | Error procesando workload |
| `MethodArgumentNotValidException` | 400 | Validation Error | Validación de @Valid falló |
| `ConstraintViolationException` | 400 | Validation Error | Violación de constraints |
| `AuthenticationException` | 401 | Unauthorized | Fallo de autenticación |
| `AccessDeniedException` | 403 | Forbidden | Acceso denegado |
| `Exception` (genérica) | 500 | Internal Server Error | Errores no categorizados |

## 🔄 Flujo de Manejo de Excepciones

### 1. Service Layer
```java
@Service
public class TrainerWorkloadServiceImpl {
    
    public TrainerMonthlySummaryResponse getTrainerMonthlySummary(String username) {
        // Validación de entrada
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }
        
        try {
            // ❌ ANTES: Devolvía respuesta vacía
            // return createEmptyResponse(username);
            
            // ✅ AHORA: Lanza excepción específica del dominio
            Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException(username));
            
            // ... resto de la lógica
            return trainerMonthlySummaryMapper.toResponse(trainer, summaries);
            
        } catch (TrainerNotFoundException e) {
            throw e; // Re-lanza excepciones de dominio
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanza excepciones de validación
        } catch (Exception e) {
            // Convierte excepciones técnicas en excepciones de dominio
            throw new WorkloadProcessingException(username, "getTrainerMonthlySummary", e);
        }
    }
}
```

### 2. Controller Layer
```java
@RestController
public class TrainerWorkloadController {
    
    @GetMapping("/{username}/monthly-summary")
    public ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummary(@PathVariable String username) {
        // ❌ ANTES: try-catch manual
        // try {
        //     TrainerMonthlySummaryResponse response = service.getTrainerMonthlySummary(username);
        //     return ResponseEntity.ok(response);
        // } catch (Exception e) {
        //     return ResponseEntity.status(500).build();
        // }
        
        // ✅ AHORA: Sin try-catch, excepciones se propagan
        TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary(username);
        return ResponseEntity.ok(response);
    }
}
```

### 3. GlobalExceptionHandler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(TrainerNotFoundException.class)
    public ResponseEntity<ApiStandardError> handleTrainerNotFoundException(
            TrainerNotFoundException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.notFound(ex.getMessage(), request);
    }
}
```

## 📊 Ejemplos de Respuestas de Error

### Trainer No Encontrado
```http
GET /api/v1/trainers/nonexistent/monthly-summary
```

```json
HTTP/1.1 404 Not Found
{
  "timestamp": "2024-01-15T10:30:45.123",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Trainer not found with username: nonexistent",
  "path": "/api/v1/trainers/nonexistent/monthly-summary"
}
```

### Datos de Workload Inválidos
```http
POST /api/v1/trainers/workload
{
  "trainerUsername": "",
  "trainingDuration": -5
}
```

```json
HTTP/1.1 400 Bad Request
{
  "timestamp": "2024-01-15T10:30:45.123",
  "status": 400,
  "error": "Validation Error",
  "message": "Invalid workload data for field 'trainerUsername' with value '' : Trainer username is required",
  "path": "/api/v1/trainers/workload"
}
```

### Error de Validación Spring
```http
POST /api/v1/trainers/workload
{
  "trainerUsername": null,
  "actionType": null
}
```

```json
HTTP/1.1 400 Bad Request
{
  "timestamp": "2024-01-15T10:30:45.123",
  "status": 400,
  "error": "Validation Error",
  "message": "trainerUsername: must not be blank, actionType: must not be null",
  "path": "/api/v1/trainers/workload"
}
```

## 📚 Patrón de Try-Catch en Services

### ¿Por qué Try-Catch en Services pero no en Controllers?

**Services SÍ usan try-catch** porque:
1. **Transforman excepciones técnicas en excepciones de dominio**
2. **Mantienen abstracción entre capas** (infraestructura → dominio)
3. **Proporcionan contexto específico del negocio**
4. **Re-lanzan excepciones de dominio apropiadas**

**Controllers NO usan try-catch** porque:
1. **No deben manejar lógica de negocio**
2. **Las excepciones se propagan automáticamente**
3. **GlobalExceptionHandler maneja la conversión a HTTP**

### Patrón de Manejo en Services

```java
@Service
public class TrainerWorkloadServiceImpl {
    
    public void processTrainerWorkload(TrainerWorkloadRequest request) {
        // 1. Validación de entrada inmediata
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        
        try {
            // 2. Lógica de negocio que puede fallar
            Trainer trainer = getOrCreateTrainer(request);
            updateMonthlySummary(trainer, request);
            
        } catch (InvalidWorkloadDataException e) {
            throw e; // 3a. Re-lanza excepciones de dominio
        } catch (IllegalArgumentException e) {
            throw e; // 3b. Re-lanza excepciones de validación
        } catch (Exception e) {
            // 3c. Convierte excepciones técnicas en dominio
            throw new WorkloadProcessingException(request.trainerUsername(), "processTrainerWorkload", e);
        }
    }
}
```

### Tipos de Excepciones por Capa

| Capa | Tipos de Excepciones | Manejo |
|------|---------------------|--------|
| **Infrastructure** | `DataAccessException`, `ConstraintViolationException`, `SQLException` | Capturadas por Service |
| **Domain** | `TrainerNotFoundException`, `InvalidWorkloadDataException` | Re-lanzadas por Service |
| **Application** | `IllegalArgumentException`, Validation errors | Re-lanzadas por Service |
| **Presentation** | Todas las anteriores convertidas | Manejadas por GlobalExceptionHandler |

## 🎯 Mejores Prácticas Implementadas

### ✅ Separación de Responsabilidades
- **Controllers**: Solo manejan HTTP, sin try-catch
- **Services**: Convierten excepciones técnicas en excepciones de dominio
- **GlobalExceptionHandler**: Centraliza la conversión de excepciones a respuestas HTTP

### ✅ Consistencia
- Todas las respuestas de error siguen la misma estructura
- Misma lógica que gym-service para uniformidad entre microservicios
- Logging manejado automáticamente por LoggingAspect

### ✅ Información Rica
- Mensajes de error descriptivos y específicos
- Timestamps para debugging
- Paths para identificar endpoints problemáticos
- Request IDs (via LoggingAspect) para trazabilidad

### ✅ Seguridad
- No exposición de información sensible en mensajes de error
- Manejo apropiado de excepciones de autenticación y autorización
- Logs automáticos para auditoría (via LoggingAspect)

### ✅ Mantenibilidad
- Excepciones específicas del dominio facilitan identificación de problemas
- ErrorResponseBuilder centraliza la construcción de respuestas
- Fácil agregar nuevos tipos de errores

## 🚀 Ventajas del Nuevo Sistema

1. **Respuestas Consistentes**: Estructura uniforme en todos los endpoints
2. **Debugging Facilitado**: Información rica y request tracing automático
3. **Código Limpio**: Controllers sin try-catch, lógica centralizada
4. **Escalabilidad**: Fácil agregar nuevos tipos de excepciones
5. **Interoperabilidad**: Mismo patrón que gym-service
6. **Observabilidad**: Integración automática con LoggingAspect
7. **Mantenibilidad**: Separación clara de responsabilidades

Este sistema proporciona una base sólida para el manejo de errores robusto y profesional en el workload-service. 