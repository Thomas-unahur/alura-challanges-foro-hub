# Foro-Hub API Rest

Foro-Hub es una API REST diseñada para gestionar un foro orientado a una comunidad de programación. Permite realizar operaciones CRUD sobre tópicos, respuestas y usuarios, manejar autenticación con JWT y proveer documentación automática con OpenAPI (SpringDoc).

## Índice
- Tecnologías
- Instalación y configuración
- Uso rápido (endpoints principales)
- Cambios recientes / Refactorizaciones
- Tests (qué se agregó y cómo ejecutarlos)
- Documentación de la API
- Agradecimientos y cómo contribuir

## Tecnologías
- Java 17
- Spring Boot 3.3.x
- Spring Security
- Spring Data JPA
- MySQL (producción)
- H2 (tests / integración en memoria)
- Maven
- Flyway
- JWT para autenticación
- Lombok
- SpringDoc OpenAPI

## Instalación y configuración
1. Clona el repositorio:

```bash
git clone https://github.com/Thomas-unahur/alura-challanges-foro-hub.git
```

2. Entra al módulo API y construye el proyecto:

```bash
cd api
mvn install
```

3. Configura tu base de datos (MySQL) y actualiza el archivo `src/main/resources/application.properties` o `application.yml` con tus credenciales.

4. Ejecuta la aplicación:

```bash
mvn spring-boot:run
# o desde el directorio api
cd api && mvn spring-boot:run
```

## Uso rápido (endpoints principales)
- Registrar tópico: POST /topicos
- Listar tópicos (paginado): GET /topicos?page=0&size=10&sort=fechaCreacion
- Registrar respuesta: POST /respuestas
- Actualizar respuesta: PUT /respuestas
- Eliminar respuesta: DELETE /respuestas/{id}
- Registrar usuario: POST /usuarios
- Login / obtener JWT: POST /login

Los cuerpos de ejemplo para cada endpoint están disponibles en la documentación OpenAPI (Swagger UI).

## Cambios recientes / Refactorizaciones (resumen)
En los últimos commits se realizaron refactorizaciones y correcciones importantes. Los puntos más relevantes son:

- Refactor en el módulo de Tópicos (controller y service): limpieza de imports, corrección de bugs y mejora en la organización de código para facilitar pruebas.
- Autenticación: el controller de autenticación ahora devuelve `ResponseEntity<DTOJWTToken>` (tipo genérico explícito) para mayor claridad en los controladores.
- RespuestaController:
  - Se añadió la inyección de la entidad de usuario autenticado (`@AuthenticationPrincipal Usuario`) en los endpoints que crean/actualizan/eliminan respuestas.
  - Se agregó endpoint para eliminar respuestas (DELETE /respuestas/{id}).
  - Firmas de servicio actualizadas para recibir el usuario autenticado cuando corresponde.
- Perfil: se agregaron setters en la entidad `Perfil` (facilita setup en tests y en algunos flujos de creación/edición).
- Repositorios/Servicios de Usuario: el método para buscar por login fue refactorizado para devolver `Optional` (mejor manejo de ausencias y pruebas más seguras).
- Limpieza general: eliminación de imports y variables innecesarias en varias clases.

Para ver el historial completo de cambios, revisa los últimos cambios: https://github.com/Thomas-unahur/alura-challanges-foro-hub/commits

## Tests (qué se agregó y cómo ejecutarlos)
Se agregaron tests unitarios e de integración para las piezas críticas del dominio:

- Tests para Topico:
  - Service y Controller: pruebas unitarias que validan flujos de listado, creación, obtención de un tópico con sus respuestas y manejo de errores (p. ej. NotFound).
- Tests para Usuario:
  - Service y/o Repositorio: pruebas que usan una base en memoria (H2) para validar el registro, búsqueda (ahora con Optional) y flujos de autenticación.
- Tests para Respuesta:
  - Service y Controller: pruebas de creación, actualización y eliminación, incluidas las verificaciones de permisos/propiedad del recurso.

Características de la suite de pruebas:
- Las pruebas usan H2 como base de datos en memoria para evitar dependencia de MySQL durante la ejecución.
- Se añadieron dependencias y configuraciones necesarias para ejecutar tests en el módulo `api`.

Cómo ejecutar los tests:

Desde el directorio raíz (o dentro de `api`):

```bash
# desde el módulo api
cd api
mvn test

# o desde el root si tu proyecto está configurado como multi-módulo
mvn -pl api test
```

Dónde revisar los tests:
- Código de tests: api/src/test/java/foro/hub/api/
  - ControllerTest (p. ej. TopicoControllerTest)
  - ServiceTest (p. ej. TopicoServiceTest, RespuestaServiceTest, UsuarioServiceTest)

Nota: los tests agregados cubren los flujos más relevantes y además sirven para prevenir regresiones provocadas por refactorizaciones.

## Documentación de la API
La documentación OpenAPI se genera automáticamente con SpringDoc. Para acceder:

1. Ejecuta la aplicación.
2. Abre en tu navegador:

```
http://localhost:8080/swagger-ui/index.html
```

Cambia el host/puerto si tu aplicación corre en otra dirección.

## Agradecimientos y contribuciones
Gracias a Oracle Next Education y Alura por el desafío y el material.

Si quieres contribuir:
- Abre issues para reportar bugs o sugerir mejoras.
- Envía pull requests con mejoras o correcciones.
- Mejora la documentación o agrega tests.

---
