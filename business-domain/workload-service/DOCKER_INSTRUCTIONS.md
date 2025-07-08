# Docker Setup Instructions

## Problema Resuelto ✅

El error que estás viendo indica que **Docker Desktop no está ejecutándose** en tu sistema.

## Pasos para Solucionarlo

### 1. Iniciar Docker Desktop
- **Busca "Docker Desktop"** en el menú de inicio de Windows
- **Ábrelo** y espera a que se inicie completamente
- Verás el ícono de Docker en la barra de tareas (ballena)
- Espera hasta que el ícono esté **verde** o **azul** (no gris)

### 2. Verificar que Docker está funcionando
Ejecuta este comando para verificar:
```bash
docker --version
docker-compose --version
```

Deberías ver algo como:
```
Docker version 24.x.x
Docker Compose version v2.x.x
```

### 3. Ejecutar el Docker Compose
Una vez que Docker Desktop esté corriendo:
```bash
# Navega al directorio del workload-service
cd business-domain/workload-service

# Ejecuta los contenedores
docker-compose up -d
```

## Servicios Disponibles

Una vez que los contenedores estén corriendo:

### MySQL Database
- **Host**: localhost
- **Puerto**: 3306
- **Usuario**: root
- **Contraseña**: root
- **Base de datos**: workload_db

### phpMyAdmin (Interfaz Web - como MySQL Workbench)
- **URL**: http://localhost:8080
- **Usuario**: root
- **Contraseña**: root

## Comandos Útiles

```bash
# Ver el estado de los contenedores
docker-compose ps

# Ver logs
docker-compose logs mysql
docker-compose logs phpmyadmin

# Detener los contenedores
docker-compose down

# Detener y eliminar volúmenes (resetear datos)
docker-compose down -v

# Rebuilding (si hay cambios)
docker-compose up -d --build
```

## Configuración de la Aplicación

Una vez que MySQL esté corriendo, asegúrate de que tu `application.yml` del workload-service esté configurado así:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/workload_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## Troubleshooting

### Si Docker Desktop no arranca:
1. **Reinicia Docker Desktop** (cerrar y abrir)
2. **Reinicia tu computadora**
3. **Verifica que la virtualización esté habilitada** en la BIOS
4. **Verifica que WSL2 esté instalado** (Windows Subsystem for Linux)

### Si el puerto 3306 está ocupado:
Cambia el puerto en el `docker-compose.yml`:
```yaml
ports:
  - "3307:3306"  # Usar puerto 3307 en lugar de 3306
```

Y actualiza tu `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/workload_db...
```

### Si el puerto 8080 está ocupado:
Cambia el puerto de phpMyAdmin:
```yaml
ports:
  - "8081:80"  # Usar puerto 8081 en lugar de 8080
```

¡Una vez que Docker Desktop esté corriendo, todo debería funcionar perfecto! 🐳 