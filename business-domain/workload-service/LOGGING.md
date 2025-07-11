# 📋 Workload Service - Logging System Documentation

## Overview

Este documento describe el sistema de logging comprehensivo implementado en el workload-service usando **AOP (Aspect-Oriented Programming)** y siguiendo las mejores prácticas de la industria.

## 🏗️ Arquitectura del Sistema de Logging

### Componentes Principales

1. **LoggingAspect** - Intercepta automáticamente métodos en todas las capas
2. **GlobalExceptionHandler** - Manejo centralizado de excepciones con logging detallado
3. **MDC (Mapped Diagnostic Context)** - Trazabilidad de requests con IDs únicos
4. **Configuración de Logging** - Patrones optimizados para desarrollo y producción

## 🎯 Características Implementadas

### ✅ Interceptores AOP por Capa

#### 🌐 Controller Layer
- **Paquete**: `dev.sro.workload_service.presentation.controller.v1.*`
- **Logs**: 
  - Inicio de requests HTTP con detalles completos
  - Finalización con status codes y tiempo de ejecución
  - Headers relevantes y parámetros de query
  - Manejo de excepciones específicas

#### 🔧 Application Service Layer
- **Paquete**: `dev.sro.workload_service.application.service.*`
- **Logs**:
  - Inicio y finalización de métodos de servicio
  - Tiempo de ejecución y argumentos (en modo DEBUG)
  - Manejo de excepciones con stack traces

#### 📊 Domain Repository Layer
- **Paquete**: `dev.sro.workload_service.domain.repository.*`
- **Logs**:
  - Operaciones de repositorio de dominio
  - Tiempo de ejecución para análisis de performance
  - Logging en nivel DEBUG por defecto

#### 🏗️ Infrastructure Repository Layer
- **Paquete**: `dev.sro.workload_service.infrastructure.repository.*`
- **Logs**:
  - Operaciones de infraestructura y persistencia
  - Tiempo de ejecución detallado
  - Logging en nivel DEBUG por defecto

#### 🔄 Mapper Layer
- **Paquete**: `dev.sro.workload_service.application.mapper.*`
- **Logs**:
  - Transformaciones de datos entre DTOs y entidades
  - Solo activo en nivel TRACE para evitar spam
  - Tiempo de ejecución para optimización

### 🏷️ Request Tracing con MDC

```java
// Cada request obtiene un ID único para trazabilidad
String requestId = UUID.randomUUID().toString().substring(0, 8);
MDC.put("requestId", requestId);
MDC.put("user", username);
```

**Ventajas:**
- ✅ Seguimiento completo de requests a través de todas las capas
- ✅ Correlación de logs en sistemas distribuidos
- ✅ Identificación rápida de problemas por request específico
- ✅ Información de usuario para auditoría

### 🏷️ Identificadores de Log por Capa

| Prefijo | Significado | Uso |
|---------|-------------|-----|
| REST Request | HTTP Controllers | Requests y responses HTTP |
| Application Service | Lógica de negocio | Servicios de aplicación |
| Domain Repository | Repositorios de dominio | Operaciones de dominio |
| Infrastructure Repository | Persistencia | Operaciones de infraestructura |
| Mapper | Transformaciones | Mapeo entre DTOs y entidades |

### 🛡️ Exception Handling Mejorado

#### Tipos de Excepciones Manejadas

1. **MethodArgumentNotValidException** - Validación de entrada
2. **ConstraintViolationException** - Violaciones de constraints
3. **MethodArgumentTypeMismatchException** - Tipos incorrectos
4. **IllegalArgumentException** - Argumentos inválidos
5. **AuthenticationException** - Fallos de autenticación
6. **AccessDeniedException** - Acceso denegado
7. **Exception** - Excepciones genéricas

#### Estructura de Respuesta de Error

```json
{
  "success": false,
  "message": "Error description",
  "error": "Detailed error information",
  "timestamp": "2024-01-15T10:30:45.123",
  "requestId": "abc12345"
}
```

## 📈 Configuración de Logging

**⚠️ Nota Importante**: La configuración de logging se encuentra en el **Config Server**, no en el `application.yml` local del servicio.

### Ubicación de Configuraciones

- **Producción**: `infrastructure/config-server/src/main/resources/config/workload-service/workload-service.yml`
- **Desarrollo**: `infrastructure/config-server/src/main/resources/config/workload-service/workload-service-dev.yml`
- **Local**: `infrastructure/config-server/src/main/resources/config/workload-service/workload-service-local.yml`

### Niveles de Log por Paquete (Ejemplo Producción)

```yaml
logging:
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - [RequestID:%X{requestId:-}] [User:%X{user:-}] %msg%n"
  level:
    # Application layers
    dev.sro.workload_service: INFO
    dev.sro.workload_service.application.aspect.LoggingAspect: INFO
    dev.sro.workload_service.application.service: INFO
    dev.sro.workload_service.domain.repository: INFO
    dev.sro.workload_service.infrastructure.repository: INFO
    dev.sro.workload_service.application.mapper: WARN
    
    # Framework logging
    org.springframework.web: INFO
    org.springframework.security: INFO
    org.hibernate.SQL: WARN
```

### Patrones de Logging

#### Console Pattern (Desarrollo)
```
%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %X{requestId:--} %X{user:--} %m%n%wEx
```

#### File Pattern (Producción)
```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - [RequestID:%X{requestId:-}] [User:%X{user:-}] %msg%n
```

## 🚀 Ejemplos de Uso

### Request Flow Completo

```
2024-01-15 10:30:45.123  INFO --- [nio-8080-exec-1] d.s.w.application.aspect.LoggingAspect   : abc12345 john.doe REST Request - START: POST /api/v1/trainers/workload | Handler: TrainerWorkloadController.processTrainerWorkload() | RequestID: abc12345 | User: john.doe

2024-01-15 10:30:45.125  INFO --- [nio-8080-exec-1] d.s.w.application.aspect.LoggingAspect   : abc12345 john.doe Application Service - START: TrainerWorkloadServiceImpl.processTrainerWorkload() | RequestID: abc12345

2024-01-15 10:30:45.127 DEBUG --- [nio-8080-exec-1] d.s.w.application.aspect.LoggingAspect   : abc12345 john.doe Domain Repository - START: TrainerRepository.findByUsername() | RequestID: abc12345

2024-01-15 10:30:45.135 DEBUG --- [nio-8080-exec-1] d.s.w.application.aspect.LoggingAspect   : abc12345 john.doe Domain Repository - SUCCESS: TrainerRepository.findByUsername() | ExecutionTime: 8ms | RequestID: abc12345

2024-01-15 10:30:45.140  INFO --- [nio-8080-exec-1] d.s.w.application.aspect.LoggingAspect   : abc12345 john.doe Application Service - SUCCESS: TrainerWorkloadServiceImpl.processTrainerWorkload() | ExecutionTime: 15ms | RequestID: abc12345

2024-01-15 10:30:45.142  INFO --- [nio-8080-exec-1] d.s.w.application.aspect.LoggingAspect   : abc12345 john.doe REST Request - COMPLETED: POST /api/v1/trainers/workload | Status: 200 | ExecutionTime: 19ms | Handler: TrainerWorkloadController.processTrainerWorkload() | RequestID: abc12345
```

### Exception Handling

```
2024-01-15 10:30:45.150  WARN --- [nio-8080-exec-1] d.s.w.p.e.GlobalExceptionHandler         : abc12345 john.doe Validation Exception | RequestID: abc12345 | Request: POST /api/v1/trainers/workload | ValidationErrors: {trainerUsername=must not be blank, actionType=must not be null}
```

## 🔧 Configuración y Personalización

### Activar/Desactivar Logging por Capa

```yaml
logging:
  level:
    # Desactivar logging de mappers
    dev.sro.workload_service.application.mapper: OFF
    
    # Logging muy detallado para debugging
    dev.sro.workload_service.domain.repository: TRACE
```

### Headers Relevantes Registrados

El sistema registra automáticamente headers importantes para debugging:
- `content-type`
- `accept`
- `user-agent`
- `x-forwarded-for`
- `x-real-ip`

**Nota**: Headers sensibles como `Authorization` son filtrados por seguridad.

## 📊 Monitoreo y Métricas

### Endpoints de Management

- `/actuator/loggers` - Ver y modificar niveles de log en runtime
- `/actuator/metrics` - Métricas de aplicación
- `/actuator/health` - Estado de salud de la aplicación

### Performance Monitoring

- ⏱️ **Tiempo de ejecución** registrado para todos los métodos
- 🎯 **Identificación de bottlenecks** en repositories e infrastructure
- 📈 **Análisis de patrones** de uso por request ID

## 🛠️ Troubleshooting

### Problemas Comunes

1. **MDC no se limpia correctamente**
   - ✅ Solucionado: MDC.clear() automático al final de requests

2. **Demasiados logs en producción**
   - ✅ Configuración por niveles granular
   - ✅ Mappers solo en TRACE level

3. **Performance impactado por logging**
   - ✅ Checks de nivel antes de operaciones costosas
   - ✅ Truncado de responses largas

4. **Información sensible en logs**
   - ✅ Filtrado de headers sensibles
   - ✅ No logging de passwords o tokens

## 🎯 Mejores Prácticas Implementadas

### ✅ Seguridad
- Filtrado automático de información sensible
- No logging de datos de autenticación
- Levels apropiados para información de debug

### ✅ Performance
- Checks de nivel antes de string formatting costoso
- MDC cleanup automático para prevenir memory leaks
- Truncado de responses largas

### ✅ Observabilidad
- Request IDs únicos para trazabilidad completa
- Información de usuario para auditoría
- Tiempo de ejecución para análisis de performance

### ✅ Mantenibilidad
- Código de aspect separado de lógica de negocio
- Configuración centralizada
- Documentación clara de cada componente

### ✅ Escalabilidad
- Patrones de log optimizados para grandes volúmenes
- Rotación automática de archivos de log
- Configuración diferenciada desarrollo/producción

## 🚀 Ventajas del Sistema

1. **Trazabilidad Completa**: Seguimiento end-to-end de cada request
2. **Debugging Facilitado**: Información contextual rica en cada log
3. **Performance Monitoring**: Identificación automática de bottlenecks
4. **Seguridad**: Filtrado automático de información sensible
5. **Escalabilidad**: Configuración optimizada para producción
6. **Mantenibilidad**: Separación clara de concerns usando AOP
7. **Observabilidad**: Integración con herramientas de monitoreo

Este sistema de logging proporciona una base sólida para el debugging, monitoreo y mantenimiento del workload-service en todas las etapas del ciclo de vida del software. 