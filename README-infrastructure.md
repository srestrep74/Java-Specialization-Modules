# Infrastructure Docker Compose

Este archivo `docker-compose-infrastructure.yml` contiene toda la infraestructura necesaria para ejecutar el proyecto completo de microservicios.

## Servicios Incluidos

### 🔍 **Tracing & Monitoring**
- **Zipkin**: `http://localhost:9411`
  - Trazabilidad distribuida para monitorear requests entre microservicios

### 🏋️ **Gym-Service Infrastructure**
- **PostgreSQL**: `localhost:5432`
  - Base de datos principal para gym-service
  - DB: `jpa_epam`, User: `postgres`, Password: `postgres`
  
- **pgAdmin**: `http://localhost:5050`
  - Interfaz web para administrar PostgreSQL
  - Email: `admin@example.com`, Password: `admin`
  
- **Redis**: `localhost:6379`
  - Cache y almacenamiento de tokens JWT
  
- **RedisInsight**: `http://localhost:5540`
  - Interfaz web para administrar Redis

### 💪 **Workload-Service Infrastructure**
- **MySQL**: `localhost:3307`
  - Base de datos para workload-service
  - DB: `workload_db`, User: `workload_user`, Password: `workload_pass`
  - Root password: `root`
  
- **phpMyAdmin**: `http://localhost:8080`
  - Interfaz web para administrar MySQL
  - User: `root`, Password: `root`

## Uso

### Iniciar toda la infraestructura
```bash
docker-compose -f docker-compose-infrastructure.yml up -d
```

### Detener toda la infraestructura
```bash
docker-compose -f docker-compose-infrastructure.yml down
```

### Ver logs
```bash
# Todos los servicios
docker-compose -f docker-compose-infrastructure.yml logs -f

# Un servicio específico
docker-compose -f docker-compose-infrastructure.yml logs -f zipkin
docker-compose -f docker-compose-infrastructure.yml logs -f postgres
docker-compose -f docker-compose-infrastructure.yml logs -f mysql
```

### Verificar estado de servicios
```bash
docker-compose -f docker-compose-infrastructure.yml ps
```

## Orden de Inicio Recomendado

1. **Primero**: Iniciar infraestructura
   ```bash
   docker-compose -f docker-compose-infrastructure.yml up -d
   ```

2. **Segundo**: Iniciar config-server
   ```bash
   cd infrastructure/config-server
   ./mvnw spring-boot:run
   ```

3. **Tercero**: Iniciar eureka-server
   ```bash
   cd infrastructure/eureka-server
   ./mvnw spring-boot:run
   ```

4. **Cuarto**: Iniciar gateway-server
   ```bash
   cd infrastructure/gateway-server
   ./mvnw spring-boot:run
   ```

5. **Quinto**: Iniciar microservicios
   ```bash
   # Terminal 1 - Gym Service
   cd business-domain/gym-service
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   
   # Terminal 2 - Workload Service
   cd business-domain/workload-service
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

## Puertos Utilizados

| Servicio | Puerto | URL |
|----------|---------|-----|
| Zipkin | 9411 | http://localhost:9411 |
| PostgreSQL | 5432 | localhost:5432 |
| pgAdmin | 5050 | http://localhost:5050 |
| Redis | 6379 | localhost:6379 |
| RedisInsight | 5540 | http://localhost:5540 |
| MySQL | 3307 | localhost:3307 |
| phpMyAdmin | 8080 | http://localhost:8080 |

## Datos Persistentes

Los siguientes volúmenes son creados para persistir datos:
- `postgres_data`: Datos de PostgreSQL
- `redis_data`: Datos de Redis  
- `mysql_data`: Datos de MySQL
- `redisinsight_data`: Configuración de RedisInsight

## Red

Todos los servicios están conectados a la red `microservices-network` para permitir comunicación entre contenedores.

## Troubleshooting

### Problemas de puertos ocupados
Si algún puerto está ocupado, puedes cambiarlos en el archivo `docker-compose-infrastructure.yml`:

```yaml
ports:
  - "NUEVO_PUERTO:PUERTO_INTERNO"
```

### Limpiar volúmenes (⚠️ Elimina todos los datos)
```bash
docker-compose -f docker-compose-infrastructure.yml down -v
```

### Reconstruir servicios
```bash
docker-compose -f docker-compose-infrastructure.yml up -d --force-recreate
```

## Verificación de Servicios

Una vez iniciados todos los servicios, verifica que estén funcionando:

1. **Zipkin**: http://localhost:9411 - Deberías ver la interfaz de Zipkin
2. **pgAdmin**: http://localhost:5050 - Login con admin@example.com/admin
3. **RedisInsight**: http://localhost:5540 - Configura conexión a localhost:6379
4. **phpMyAdmin**: http://localhost:8080 - Login con root/root 