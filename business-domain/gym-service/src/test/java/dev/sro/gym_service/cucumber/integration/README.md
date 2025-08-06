# Workload Integration Tests

Este directorio contiene los tests de integración para la comunicación entre `gym-service` y `workload-service` a través de ActiveMQ.

## Estructura del Proyecto

```
cucumber/integration/
├── config/
│   ├── CucumberTestConfig.java          # Configuración principal de Cucumber
│   └── ActiveMQTestConfig.java          # Configuración de ActiveMQ para tests
├── hooks/
│   └── TestHooks.java                   # Hooks para setup y cleanup
├── steps/
│   └── WorkloadIntegrationSteps.java    # Step definitions
├── WorkloadIntegrationTestRunner.java   # Runner de Cucumber
└── README.md                           # Este archivo
```

## Características de los Tests

### 🎯 Objetivos
- Validar la comunicación completa entre `gym-service` y `workload-service`
- Verificar el mecanismo de fallback cuando ActiveMQ no está disponible
- Asegurar que las notificaciones de workload se envían correctamente
- Probar el manejo de errores y casos edge

### 🏗️ Arquitectura de Testing

#### 1. **BDD con Cucumber**
- Escenarios escritos en lenguaje natural (Gherkin)
- Fácil comprensión para stakeholders no técnicos
- Documentación viva del comportamiento del sistema

#### 2. **Tests de Integración Embebidos**
- ActiveMQ embebido para tests (sin Docker)
- Base de datos H2 en memoria
- Ambiente de test completamente aislado

#### 3. **Configuración de Test**
- Profile `test` activo
- Configuración específica para ActiveMQ
- Logging detallado para debugging

## Escenarios de Test

### ✅ Happy Path
- **Escenario**: Notificación exitosa cuando se crea un training
- **Verifica**: Mensaje enviado a ActiveMQ con información correcta
- **Tag**: `@happy-path`

### 🔄 Fallback Scenario
- **Escenario**: Fallback a base de datos cuando ActiveMQ no está disponible
- **Verifica**: Datos guardados en tabla `pending_workloads`
- **Tag**: `@fallback-scenario`

### 📝 Update Scenario
- **Escenario**: Notificación cuando se actualiza un training
- **Verifica**: Mensaje con action type `UPDATE`
- **Tag**: `@update-scenario`

### 🗑️ Delete Scenario
- **Escenario**: Notificación cuando se elimina un training
- **Verifica**: Mensaje con action type `DELETE`
- **Tag**: `@delete-scenario`

### ❌ Error Handling
- **Escenarios**: Manejo de trainers/trainees inexistentes
- **Verifica**: Errores apropiados sin envío de mensajes
- **Tag**: `@error-handling`

### ⚡ Circuit Breaker
- **Escenario**: Activación del circuit breaker tras múltiples fallos
- **Verifica**: Fallback automático y funcionamiento continuo
- **Tag**: `@circuit-breaker`

## Cómo Ejecutar los Tests

### Ejecutar Todos los Tests de Integración
```bash
mvn test -Dtest=WorkloadIntegrationTestRunner
```

### Ejecutar Tests por Tag
```bash
# Solo happy path
mvn test -Dcucumber.filter.tags="@happy-path"

# Solo fallback scenarios
mvn test -Dcucumber.filter.tags="@fallback-scenario"

# Excluir tests de error handling
mvn test -Dcucumber.filter.tags="not @error-handling"
```

### Ejecutar Tests Tradicionales
```bash
mvn test -Dtest=WorkloadNotificationIntegrationTest
```

## Configuración de Test

### ActiveMQ Embebido
- Broker en memoria para tests
- Sin persistencia
- Configuración automática

### Base de Datos H2
- Base de datos en memoria
- Schema creado automáticamente
- Datos de test inicializados en hooks

### Circuit Breaker
- Configuración específica para tests
- Timeouts reducidos para tests rápidos
- Fallback automático activado

## Estructura de Datos de Test

### Trainers de Test
- `trainer1` - John Doe (activo)
- `trainer2` - Jane Smith (activo)
- `trainer3` - Bob Wilson (activo)
- `trainer4` - Alice Johnson (activo)
- `trainer6` - Charlie Brown (activo)
- `trainer7` - Diana Prince (activo)

### Trainees de Test
- `trainee1` - Mike Ross
- `trainee2` - Rachel Green
- `trainee3` - Chandler Bing
- `trainee4` - Monica Geller
- `trainee5` - Joey Tribbiani
- `trainee7` - Phoebe Buffay

## Reportes

Los tests generan reportes en:
- `target/cucumber-reports/workload-integration.html` - Reporte HTML
- `target/cucumber-reports/workload-integration.json` - Reporte JSON

## Mejores Prácticas Implementadas

### 1. **Aislamiento de Tests**
- Cada escenario tiene su propio contexto
- Limpieza automática de datos entre tests
- Configuración independiente

### 2. **Manejo de Estado**
- Hooks para setup y cleanup
- Datos de test consistentes
- Estado compartido entre steps

### 3. **Assertions Robustas**
- Verificación de contenido de mensajes
- Validación de persistencia de datos
- Manejo de excepciones

### 4. **Logging Detallado**
- Logs para debugging
- Información de contexto en cada step
- Trazabilidad de ejecución

## Troubleshooting

### Problemas Comunes

#### 1. **ActiveMQ no inicia**
```bash
# Verificar que no hay otro broker corriendo
netstat -an | grep 61616
```

#### 2. **Tests fallan por timeout**
```bash
# Aumentar timeout en configuración
spring.jms.template.receive-timeout=5000
```

#### 3. **Datos de test inconsistentes**
```bash
# Verificar hooks de limpieza
# Revisar configuración de transacciones
```

### Debugging

#### 1. **Habilitar Logging Detallado**
```yaml
logging:
  level:
    dev.sro.gym_service: DEBUG
    org.springframework.jms: DEBUG
    org.apache.activemq: DEBUG
```

#### 2. **Verificar Mensajes en Cola**
```java
// En step definitions
Message message = jmsTemplate.receive("workload-queue");
if (message != null) {
    log.info("Message received: {}", message);
}
```

## Contribución

### Agregar Nuevos Escenarios

1. **Actualizar Feature File**
   ```gherkin
   @new-scenario
   Scenario: Descripción del nuevo escenario
     Given precondition
     When action
     Then expected result
   ```

2. **Implementar Step Definitions**
   ```java
   @When("action")
   public void action() {
       // Implementation
   }
   ```

3. **Agregar Tests Unitarios Complementarios**
   ```java
   @Test
   void testNewScenario() {
       // Test implementation
   }
   ```

### Mantenimiento

- Actualizar datos de test cuando cambie el modelo
- Revisar configuración de ActiveMQ periódicamente
- Mantener documentación actualizada
- Ejecutar tests regularmente en CI/CD

## Referencias

- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [ActiveMQ Testing](https://activemq.apache.org/testing.html)
- [BDD Best Practices](https://cucumber.io/docs/bdd/) 