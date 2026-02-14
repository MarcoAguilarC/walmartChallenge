# Delivery Slots

Solución para el desafío técnico de reserva de ventanas de entrega.

### Cómo correrlo

Para correr la solución se necesita tener instalado docker y docker-compose, en las últimas versiones de los clientes de docker suelen venir ambos incluidos.

teniendo docker-compose solo necesitamos situarnos en la carpeta raíz del proyecto y ejecutar el siguiente comando:

```bash
docker-compose up --build
```

- **Front**: http://localhost:3000
- **Back**: http://localhost:8080/swagger-ui.html
- **Prometheus**: http://localhost:9090

### Sobre la concurrencia

Al abordar este desafío, la mayor complejidad a resolver fue garantizar la consistencia en las escrituras a la base de datos y evitar condiciones de carrera, situaciones que ocurrirán naturalmente al escalar horizontalmente en un ambiente distribuido.Para mitigar esto, se implementó una estrategia en capas. Primero, se utiliza Virtual Threads de Java 21 para manejar la alta concurrencia de I/O y así no bloquear los hilos del servidor. Luego, para controlar el acceso crítico a los cupos limitados, se implementaron cache y bloqueos distribuidos con Redis, asegurando que solo una instancia pueda intentar reservar un cupo específico a la vez. Finalmente se implementa Optimistic Locking a nivel de la base de datos para prevenir cualquier sobrescritura accidental en el improbable caso de que dos transacciones se encuentren con una condición de final de carrera.
