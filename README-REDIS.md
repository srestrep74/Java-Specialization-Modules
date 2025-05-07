# Configuración de Redis para la gestión de tokens JWT

## Descripción
Este documento describe la implementación de Redis para el manejo de la blacklist de tokens JWT en la aplicación.

## Requisitos
- Redis 6.x o superior
- Java 17 o superior
- Spring Boot 3.x

## Instalación de Redis

### Windows
1. Descarga Redis para Windows desde: https://github.com/microsoftarchive/redis/releases
2. Instala el paquete MSI
3. Inicia el servicio de Redis desde los servicios de Windows

### Linux
```bash
sudo apt update
sudo apt install redis-server
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

### macOS
```bash
brew install redis
brew services start redis
```

### Docker
```bash
docker run --name redis-jwt -p 6379:6379 -d redis
```

## Configuración de la aplicación

La configuración de Redis se encuentra en `application.properties`:

```properties
spring.redis.host=localhost
spring.redis.port=6379
# spring.redis.password= # Descomentar y configurar si Redis tiene contraseña
spring.redis.timeout=5000
spring.cache.type=redis

# JWT Blacklist
jwt.blacklist.prefix=blacklisted_token:
```

## Arquitectura de la solución

1. **TokenBlacklist**: Clase que gestiona la lista negra de tokens usando Redis
   - Almacena los tokens con tiempo de expiración automático
   - Usa el ID del token como clave en Redis
   - No requiere limpieza manual, ya que Redis elimina las claves automáticamente

2. **RedisConfig**: Configura la conexión a Redis y los serializadores

3. **JwtAuthenticationFilter**: Verifica si un token está en la blacklist durante la autenticación

## Ventajas sobre la implementación anterior

1. **Persistencia**: Los tokens invalidados sobreviven a reinicios del servidor
2. **Escalabilidad**: Funciona en entornos con múltiples instancias del servicio
3. **Rendimiento**: Redis ofrece acceso de alta velocidad y baja latencia
4. **Mantenimiento automático**: Redis gestiona la expiración de claves automáticamente

## Monitoreo

Para verificar los tokens en la blacklist, puedes usar el CLI de Redis:

```bash
redis-cli
keys blacklisted_token:*
```

## Solución de problemas

### No se puede conectar a Redis
1. Verifica que Redis esté en ejecución: `redis-cli ping` (debería responder PONG)
2. Revisa la configuración en `application.properties`
3. Asegúrate de que el puerto 6379 esté abierto

### Los tokens no se están invalidando
1. Verifica que la clave en Redis esté siendo creada correctamente
2. Comprueba que el ID del token se esté extrayendo adecuadamente
3. Verifica que el tiempo de expiración sea correcto 