# Implementación de WebSockets

---

### Componentes Principales

1.  **Configuración (`WebSocketConfig.java`):** Define el punto de entrada (Endpoint).
2.  **Manejador (`WebSocketHandler.java`):** Gestiona el ciclo de vida de la conexión y mantiene un registro de las sesiones activas.
3.  **Modelo de Transferencia (`Notification`, `AlumnoNotificationResponse`):** Define la estructura JSON que recibiremos.
4.  **Llamada de Operaciones (`AlumnosServiceImpl.java`):** Nos muestra la notificación de forma asíncrona.

## 1. Configuración de las Notificaciones

### A. Configuración del Endpoint
Habilitamos el soporte de WebSockets con la anotación `@EnableWebSocket` y la implementación de la interfaz `WebSocketConfigurer`.

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketAlumnosHandler(), "/ws/" + apiVersion + "/alumnos");
    }
    // ... definición del Bean del Handler
}
```


## 2. Puesta en Marcha y Pruebas
Requisitos previos:
- Servidor Spring Boot en ejecución.
- Herramienta de pruebas, en nuestro caso el `HTTP request` de IntellIJ IDEA.


### Establecemos una conexion
Ejecutamos la siguiente ruta 
* **Ruta del Endpoint:** `ws://localhost:3000/ws/${api.version}/alumnos`

Una vez conectado nos saldrá un mensaje del servidor confirmando la conexión con los correspondientes mensajes periodicos.
* Updates Web socket: Alumnos - (App de Alumnos)
  * server periodic message 23:53:49.026973500
  * server periodic message 23:53:54.027214600

### Simulando un POST
Al hacer un post de un alumno nuevo, en el apartado de WebSocket nos saldrá esta notificación de que efectivamente hemos realizado un POST con la siguiente estructura.
Así con el PUT y DELETE.
``` java
{
"entity":"ALUMNOS",
"type":"CREATE", // <-- Tipo de Método HTTP
"data":{
    "id":6,
    "nombre":"Ernesto",
    "apellido":"Espinoza",
    "grado":"Programacion",
    "asignatura":"3 lemental",
    "createdAt":"2025-12-11T23:58:40.112679200",
    "updatedAt":"2025-12-11T23:58:40.112679200",
    "uuid":"3f6851b5-eb40-4feb-b261-2ccb4d77de8c"
 },
   "createdAt":"2025-12-11T23:58:40.134733500"
}
```

## 3. Análisis Investigador

En este apartado se evalúa la solución actual frente a estándares de la industria.

### 3.1. Evaluación del Enfoque Actual (WebSockets)

La implementación actual utiliza la API de Spring (WebSocketHandler).

**Puntos Fuertes:**

* **Simplicidad:** Ideal para necesidades sencillas.
* **Control Total:** Acceso directo a la sesión y al ciclo de vida.

**Debilidades:**

* **Gestión Manual:** El desarrollador es responsable de gestionar la lista de sesiones y la concurrencia.
* **Formato de Mensaje:** Se debe serializar a JSON manualmente (ObjectMapper).

### 3.2. Alternativa: STOMP sobre WebSockets 

Si el proyecto crece, se recomienda migrar a STOMP (Simple Text Oriented Messaging Protocol).

* **Ventajas:** Permite anotaciones como `@MessageMapping` y `@SendTo`. Gestiona automáticamente suscripciones a rutas específicas.
* **Cuándo migrar:** Si necesitas enviar mensajes privados a un usuario específico o si tienes múltiples tipos de notificaciones que los clientes deben poder filtrar.
