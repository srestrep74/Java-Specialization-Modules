# Implementación de TokenStorageService para entorno local

## Descripción

Este documento describe la implementación alternativa de TokenStorageService para el entorno local, que utiliza estructuras de datos en memoria en lugar de Redis para el manejo de tokens JWT.

## Diferencias con la implementación Redis

| Característica | Redis (dev/prod) | En memoria (local) |
|----------------|------------------|-------------------|
| Persistencia | Persiste entre reinicios | Se pierde al reiniciar la aplicación |
| Escalabilidad | Compatible con múltiples instancias | Solo funciona en una instancia |
| Manejo de expiración | Automático por Redis | Mediante tarea programada |
| Dependencias | Requiere servidor Redis | Sin dependencias externas |
| Configuración | Más compleja | Mínima configuración |

## Componentes principales

### InMemoryTokenStorageServiceImpl

Implementación de `TokenStorageService` que usa ConcurrentHashMap para almacenar:
- Lista negra de tokens (tokenId -> fecha de expiración)
- Tokens de refresco por usuario (username -> conjunto de tokenIds)

```java
@Service
@Profile("local")
public class InMemoryTokenStorageServiceImpl implements TokenStorageService {
    // Implementación...
}
```

### Tareas programadas

La implementación incluye una tarea programada que se ejecuta periódicamente para limpiar tokens expirados de la lista negra:

```java
@Scheduled(fixedDelayString = "${jwt.blacklist.cleanup-interval:600000}")
public void cleanupExpiredTokens() {
    // Elimina tokens expirados de la lista negra
}
```

## Configuración

El comportamiento de esta implementación se puede ajustar mediante las siguientes propiedades en `application-local.properties`:

```properties
# Prefijos para las claves
jwt.blacklist.prefix=blacklisted_token:
jwt.refresh.prefix=user:refresh_tokens:

# Tiempo de expiración para tokens de refresco (días)
jwt.refresh.expiry=30

# Intervalo de limpieza de tokens expirados (milisegundos)
jwt.blacklist.cleanup-interval=60000
```

## Activación del perfil local

Para usar esta implementación, asegúrate de que el perfil activo sea "local":

```bash
# Línea de comando
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

O en `application.properties`:
```properties
spring.profiles.active=local
```

## Ventajas para desarrollo local

1. **Simplicidad**: No requiere configurar Redis para desarrollo
2. **Rápido inicio**: La aplicación arranca más rápido sin dependencias externas
3. **Depuración**: Es más fácil inspeccionar el estado de los tokens durante el desarrollo
4. **Recursos**: Consume menos recursos del sistema

## Limitaciones

1. **Sin persistencia**: Los tokens se pierden al reiniciar la aplicación
2. **No distribuible**: No funciona en un entorno con múltiples instancias
3. **Rendimiento**: Para gran cantidad de tokens, el rendimiento puede ser inferior a Redis
4. **Memoria**: Consumo de memoria proporcional al número de tokens almacenados

## Cuándo usar esta implementación

Esta implementación está diseñada específicamente para:
- Desarrollo local
- Pruebas y depuración
- Entornos donde no es posible o deseable instalar Redis

Para entornos de producción o staging, se recomienda usar la implementación basada en Redis. 