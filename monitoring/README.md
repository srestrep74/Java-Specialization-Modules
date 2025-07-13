# 🔍 Monitoreo de Microservicios - Gym Service

Este directorio contiene la configuración completa para monitorear el **gym-service** y **workload-service** usando **Prometheus** y **Grafana**.

## 📋 Características del Monitoreo

### ✅ Métricas Disponibles

#### 🎯 Métricas Personalizadas (Custom Metrics)
- **Training Sessions**: `training_sessions_total`
- **Trainee Sessions**: `trainee_sessions_total` 
- **Trainer Sessions**: `trainer_sessions_total`
- **Training Duration**: `training_duration_minutes_total`
- **Trainee Training Duration**: `trainee_training_duration_minutes_total`
- **Trainer Training Duration**: `trainer_training_duration_minutes_total`

#### 🌐 Métricas de Spring Boot
- **HTTP Requests**: Rate, latency, status codes
- **JVM Memory**: Heap, non-heap, garbage collection
- **CPU Usage**: Process and system CPU
- **Database Connection Pool**: Active, idle, max connections
- **Application Health**: Startup time, readiness

#### 🔒 Métricas de Circuit Breaker (Resilience4j)
- **Circuit Breaker State**: OPEN, CLOSED, HALF_OPEN
- **Failure Rate**: Percentage of failed calls
- **Call Duration**: Slow calls tracking
- **Successful/Failed Calls**: Request success/failure rates

## 🚀 Paso a Paso - Configuración Completa

### 1. Preparar el Entorno

```bash
# Navegar al directorio raíz del proyecto
cd SpringCoreTask1

# Verificar que tienes la estructura de carpetas correcta
ls -la monitoring/
```

### 2. Iniciar la Infraestructura

```bash
# Iniciar servicios de infraestructura (bases de datos, Prometheus, Grafana)
docker-compose -f docker-compose-infrastructure.yml up -d

# Verificar que los servicios estén corriendo
docker-compose -f docker-compose-infrastructure.yml ps
```

**Servicios disponibles después de este paso:**
- 🔍 **Prometheus**: http://localhost:9090
- 📊 **Grafana**: http://localhost:3000 (admin/admin)
- 🐘 **PostgreSQL**: localhost:5432
- 🗄️ **MySQL**: localhost:3307
- 🔴 **Redis**: localhost:6379
- 📈 **Zipkin**: http://localhost:9411

### 3. Iniciar los Microservicios

```bash
# Iniciar todos los microservicios
docker-compose -f docker-compose-services.yml up -d

# Verificar que todos los servicios estén corriendo
docker-compose -f docker-compose-services.yml ps
```

**Servicios disponibles después de este paso:**
- 🏃‍♂️ **Config Server**: http://localhost:8888
- 🌐 **Eureka Server**: http://localhost:8761
- 🚪 **Gateway Server**: http://localhost:8083
- 💪 **Gym Service**: http://localhost:8081
- 📊 **Workload Service**: http://localhost:8082

### 4. Verificar Métricas

#### Verificar endpoints de métricas:
```bash
# Gym Service - Métricas de Prometheus
curl http://localhost:8081/actuator/prometheus

# Gym Service - Health check
curl http://localhost:8081/actuator/health

# Workload Service - Métricas de Prometheus  
curl http://localhost:8082/actuator/prometheus

# Workload Service - Health check
curl http://localhost:8082/actuator/health
```

#### Verificar Prometheus:
1. Abrir: http://localhost:9090
2. Ir a **Status > Targets**
3. Verificar que todos los targets estén **UP**:
   - gym-service (8081)
   - workload-service (8082)
   - config-server (8888)
   - eureka-server (8761)
   - gateway-server (8083)

### 5. Acceder a Grafana

#### Primer acceso:
1. **URL**: http://localhost:3000
2. **Usuario**: `admin`
3. **Contraseña**: `admin`
4. (Opcional) Cambiar contraseña cuando se solicite

#### Dashboards disponibles:
- **🔍 ALL METRICS - Comprehensive Dashboard**: Dashboard completo con TODAS las métricas disponibles
- **🔒 Circuit Breaker & Resilience Monitoring**: Dashboard especializado en Circuit Breakers y Resilience4j
- **Gym Service - Comprehensive Monitoring**: Dashboard principal con métricas clave
- **Health Monitoring Dashboard**: Dashboard enfocado en salud de servicios

### 6. Generar Datos de Prueba

Para ver métricas en acción, genera algunas transacciones:

```bash
# Ejemplo de requests para generar métricas
curl -X POST http://localhost:8083/gym-service/api/v1/trainings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "traineeUsername": "john.doe",
    "trainerUsername": "jane.smith", 
    "trainingName": "Cardio Session",
    "trainingDate": "2024-01-15",
    "trainingDuration": 60
  }'
```

## 📊 Interpretación de Dashboards

### 🔍 Dashboard "ALL METRICS - Comprehensive Dashboard"

Este es el dashboard más completo que incluye **TODAS** las métricas disponibles organizadas en secciones:

#### 🎯 Custom Training Metrics
- **Training Session Counters**: Contadores totales de sesiones
- **Training Duration Totals**: Duración total acumulada en minutos
- **Training Metrics Rate**: Tasas de cambio de las métricas

#### 🔒 Circuit Breaker Metrics (Resilience4j)
- **Circuit Breaker State**: Estados del circuit breaker (CLOSED=0, OPEN=1, HALF_OPEN=2)
- **Failure & Slow Call Rates**: Tasas de fallas y llamadas lentas
- **Circuit Breaker Calls**: Total de llamadas y tasas

#### 🌐 HTTP & Request Metrics
- **HTTP Request Rate by Endpoint**: Requests por segundo por endpoint
- **HTTP Request Duration Percentiles**: Latencia en percentiles 50, 95, 99

#### ☕ JVM & Application Metrics
- **JVM Memory Usage**: Uso de memoria heap y non-heap
- **JVM Garbage Collection**: Frecuencia y tiempo de GC
- **JVM Threads**: Threads activos, daemon y pico

#### 💾 Database & Connection Pool Metrics
- **HikariCP Connection Pool**: Conexiones activas, idle, max, min
- **HikariCP Connection Times**: Tiempos de adquisición y uso

#### 🖥️ System & Process Metrics
- **CPU Usage**: Uso de CPU del proceso y sistema
- **Process File Descriptors**: Archivos abiertos vs máximos
- **Application Startup & Uptime**: Tiempos de inicio y uptime

#### 🔄 Feign Client & Retry Metrics
- **Resilience4j Retry Metrics**: Reintentos totales y tasas
- **Resilience4j TimeLimiter Metrics**: Métricas de timeout

#### 📊 Workload Service Metrics
- **Workload Service HTTP Requests**: Requests al servicio de workload
- **Workload Service JVM Memory**: Memoria del workload service

### 🔒 Dashboard "Circuit Breaker & Resilience Monitoring"

Dashboard especializado para monitorear en detalle los Circuit Breakers y mecanismos de resilencia:

#### Métricas Principales:
- **🔒 Circuit Breaker Current State**: Estado actual en tiempo real (CLOSED/OPEN/HALF_OPEN)
- **⚠️ Circuit Breaker Failure Rate**: Porcentaje de fallas con gauge visual
- **📊 Circuit Breaker Call Statistics**: Llamadas exitosas, fallidas y no permitidas
- **🔄 Circuit Breaker Call Rate**: Tasa de llamadas por segundo
- **📈 Failure & Slow Call Rates**: Comparación de tasas de falla y llamadas lentas
- **🔄 Retry Mechanism Calls**: Estadísticas de reintentos
- **⏱️ TimeLimiter Calls**: Métricas de timeout
- **📊 Resilience4j Rates Overview**: Vista general de todas las tasas

#### Estados del Circuit Breaker:
- **CLOSED (0)**: Estado normal, permitiendo llamadas
- **OPEN (1)**: Estado de falla, bloqueando llamadas
- **HALF_OPEN (2)**: Estado de recuperación, probando el servicio

### Dashboard Principal: "Gym Service - Comprehensive Monitoring"

#### Panel 1: Custom Training Metrics - Session Counts
- **Qué muestra**: Número total de sesiones de entrenamiento
- **Métricas**: `training_sessions_total`, `trainee_sessions_total`, `trainer_sessions_total`
- **Interpretación**: Aumenta cada vez que se crea una sesión de entrenamiento

#### Panel 2: Custom Training Metrics - Duration  
- **Qué muestra**: Duración total de entrenamientos en minutos
- **Métricas**: `training_duration_minutes_total`, etc.
- **Interpretación**: Suma acumulativa de minutos de entrenamiento

#### Panel 3: HTTP Request Rate
- **Qué muestra**: Requests por segundo a diferentes endpoints
- **Métrica**: `rate(http_server_requests_seconds_count[5m])`
- **Interpretación**: Tráfico de la aplicación en tiempo real

#### Panel 4: HTTP Request Duration
- **Qué muestra**: Latencia de requests (percentiles 50 y 95)
- **Métrica**: `histogram_quantile(0.95, ...)`
- **Interpretación**: Rendimiento de la aplicación

#### Panel 5: Circuit Breaker Metrics
- **Qué muestra**: Estado y tasa de fallas del circuit breaker
- **Métricas**: `resilience4j_circuitbreaker_state`, `resilience4j_circuitbreaker_failure_rate`
- **Interpretación**: 
  - **0**: CLOSED (normal)
  - **1**: OPEN (fallando)
  - **2**: HALF_OPEN (recuperándose)

#### Panel 6: JVM Memory Usage
- **Qué muestra**: Uso de memoria de la JVM
- **Métricas**: `jvm_memory_used_bytes`, `jvm_memory_max_bytes`
- **Interpretación**: Monitoreo de memoria heap y non-heap

#### Panel 7: CPU Usage
- **Qué muestra**: Uso de CPU del proceso y del sistema
- **Métricas**: `process_cpu_usage`, `system_cpu_usage`
- **Interpretación**: Rendimiento del sistema

#### Panel 8: Database Connection Pool
- **Qué muestra**: Conexiones activas, idle y máximas de HikariCP
- **Métricas**: `hikaricp_connections_*`
- **Interpretación**: Salud del pool de conexiones

## 🔧 Configuración Avanzada

### Personalizar Alertas

Para agregar alertas, puedes crear reglas en Prometheus:

```yaml
# /monitoring/prometheus/alerts.yml
groups:
  - name: gym-service-alerts
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High error rate detected"
```

### Agregar Nuevas Métricas

En tu código Java, puedes agregar métricas personalizadas:

```java
@Component
public class CustomMetrics {
    private final Counter customCounter;
    
    public CustomMetrics(MeterRegistry meterRegistry) {
        this.customCounter = Counter.builder("custom_operations_total")
            .description("Total custom operations")
            .register(meterRegistry);
    }
    
    public void incrementCustomOperation() {
        customCounter.increment();
    }
}
```

## 🛠️ Troubleshooting

### Problema: Targets DOWN en Prometheus

**Solución**:
1. Verificar que los servicios estén corriendo: `docker ps`
2. Verificar logs: `docker logs gym-service`
3. Verificar conectividad de red: `docker network ls`

### Problema: No se ven métricas personalizadas

**Solución**:
1. Verificar que las métricas se estén generando: `curl http://localhost:8081/actuator/prometheus | grep training`
2. Verificar que Prometheus esté scrapeando: http://localhost:9090/targets
3. Generar tráfico para activar las métricas

### Problema: Grafana no puede conectar a Prometheus

**Solución**:
1. Verificar que Prometheus esté corriendo: http://localhost:9090
2. Verificar la configuración del datasource en Grafana
3. Usar la URL interna de Docker: `http://prometheus:9090`

## 📝 Comandos Útiles

```bash
# Ver logs de un servicio específico
docker logs -f gym-service

# Reiniciar solo Prometheus
docker-compose -f docker-compose-infrastructure.yml restart prometheus

# Reiniciar solo Grafana
docker-compose -f docker-compose-infrastructure.yml restart grafana

# Ver métricas específicas
curl http://localhost:8081/actuator/prometheus | grep -E "(training|circuit|jvm)"

# Ver TODAS las métricas de Circuit Breaker
curl http://localhost:8081/actuator/prometheus | grep resilience4j

# Ver métricas personalizadas de training
curl http://localhost:8081/actuator/prometheus | grep -E "(training_|trainee_|trainer_)"

# Listar TODAS las métricas disponibles (nombres únicos)
curl -s http://localhost:8081/actuator/prometheus | grep "^[a-zA-Z]" | cut -d'{' -f1 | sort | uniq

# Verificar health de todos los servicios
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health

# Parar todo
docker-compose -f docker-compose-services.yml down
docker-compose -f docker-compose-infrastructure.yml down
```

## 🎯 Próximos Pasos

1. **Alertas**: Configurar alertas en Grafana para métricas críticas
2. **Logs**: Integrar ELK Stack para logs centralizados
3. **Métricas de Negocio**: Agregar más métricas específicas del dominio
4. **Dashboards**: Crear dashboards específicos por equipo/funcionalidad
5. **Automatización**: Scripts para deployment y configuración automática

---

¡Ahora tienes un stack completo de monitoreo para tu arquitectura de microservicios! 🚀 