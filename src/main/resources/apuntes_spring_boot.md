# Apuntes de Spring Boot

## 1. ¿Qué es Spring y Spring Boot?

### Spring Framework

-   Framework para crear aplicaciones Java modulares.
-   Incluye herramientas como acceso a BD, seguridad, inyección de
    dependencias.

### Spring Boot

-   Capa simplificada de Spring.
-   Configuración automática.
-   Trae servidor Tomcat embebido.

## 2. Arquitectura y filosofía

### MVC

-   **Model:** datos y reglas de negocio.
-   **View:** presentación (HTML, JSON...).
-   **Controller:** recibe peticiones HTTP.

### IoC (Inversión de Control)

-   Spring controla la creación de objetos.

### DI (Inyección de Dependencias)

-   Spring inyecta automáticamente dependencias.

## 3. Componentes

  ------------------------------------------------------------------------
  Capa           Anotación                  Descripción
  -------------- -------------------------- ------------------------------
  Controlador    `@RestController` /        Manejo de peticiones HTTP
                 `@Controller`              

  Servicio       `@Service`                 Lógica de negocio

  Repositorio    `@Repository`              Acceso a base de datos

  Entidad        `@Entity`                  Representa tabla BD
  ------------------------------------------------------------------------

## 4. Anotaciones comunes

-   `@SpringBootApplication`: clase principal.
-   `@RestController`: devuelve JSON.
-   `@GetMapping`, `@PostMapping`: rutas HTTP.
-   `@Autowired`: inyección de dependencias.

## 5. Ciclo de una petición

Cliente → Controlador → Servicio → Repositorio → Entidad → BD →
Respuesta JSON

## 6. Inicio de una app Spring Boot

-   Escaneo de clases anotadas.
-   Creación de Beans.
-   Inicialización del servidor embebido.
-   Enrutamiento de peticiones.

## 7. Temas avanzados

-   AOP
-   Spring Security
-   Spring Data JPA
-   DevTools
-   Validación
-   Perfiles
