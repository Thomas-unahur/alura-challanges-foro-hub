# Foro-Hub API Rest

Foro-Hub es una API REST diseñada para gestionar un foro orientado a una comunidad de programación. Permite realizar operaciones CRUD sobre tópicos, respuestas y usuarios, manejar autenticación con JWT y proveer documentación automática con OpenAPI (SpringDoc).

## Índice
- Tecnologías
- Instalación y configuración
- Uso rápido (endpoints principales)
- Cambios recientes / Refactorizaciones
- Tests (qué se agregó y cómo ejecutarlos)
- Ejemplos de código
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

D dónde revisar los tests:
- Código de tests: api/src/test/java/foro/hub/api/
  - ControllerTest (p. ej. TopicoControllerTest)
  - ServiceTest (p. ej. TopicoServiceTest, RespuestaServiceTest, UsuarioServiceTest)

Nota: los tests agregados cubren los flujos más relevantes y además sirven para prevenir regresiones provocadas por refactorizaciones.

## Ejemplos de código (métodos clave)
A continuación se muestran las firmas de los métodos más importantes y representativos (solo lo esencial):

1) RespuestaController — crear / actualizar / eliminar (uso de @AuthenticationPrincipal)

```java
// api/src/main/java/foro/hub/api/controller/RespuestaController.java
@PostMapping
ResponseEntity<DTOResponseRespuesta> registrarRespuesta(@RequestBody @Valid DTORegistroRespuesta dtoRegistroRespuesta,
                                                        @AuthenticationPrincipal Usuario usuarioAutenticado,
                                                        UriComponentsBuilder uriComponentsBuilder)

@PutMapping
ResponseEntity<DTOResponseRespuesta> actualizarRespuesta(@RequestBody @Valid DTOActualizarRespuesta dtoActualizarRespuesta,
                                                          @AuthenticationPrincipal Usuario usuarioAutenticado)

@DeleteMapping("/{id}")
ResponseEntity<Void> eliminarRespuesta(@PathVariable Long id,
                                      @AuthenticationPrincipal Usuario usuarioAutenticado)
```

2) AuthLoginService — método principal de autenticación (control de intentos y emisión de JWT)

```java
// api/src/main/java/foro/hub/api/infra/security/AuthLoginService.java
public DTOJWTToken autenticarUsuario(@RequestBody @Valid DTOAuthUsuario datosAutenticacionUsuario) {
    if (intentosLoginService.estaBloqueado(datosAutenticacionUsuario.login())) throw ...
    Authentication usuarioAutenticado = authenticationManager.authenticate(...)
    var JWTtoken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
    return new DTOJWTToken(JWTtoken);
}
```

3) TopicoService — implementación completa (registro, obtención con respuestas y validación de autoría)

```java
// api/src/main/java/foro/hub/api/domain/topico/TopicoService.java
package foro.hub.api.domain.topico;

import foro.hub.api.domain.respuestas.DTOResponseRespuesta;
import foro.hub.api.domain.respuestas.RespuestaRepository;
import foro.hub.api.domain.topico.validaciones.ValidadorDeDuplicados;
import foro.hub.api.domain.usuarios.DTOInfoUsuario;
import foro.hub.api.domain.usuarios.Usuario;
import foro.hub.api.infra.errores.AuthorizationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicoService {
    
    private final TopicoRepository topicoRepository;
    
    private final List<ValidadorDeDuplicados> validadores;
    
    private final RespuestaRepository respuestaRepository;

    
    public DTOResponseTopic actualizarTopico(DTOActualizarTopico datos,Usuario usuarioAutenticado){
        Topico topico = topicoRepository.findById(datos.id()).orElseThrow(
            () -> new EntityNotFoundException()
        );

        validarAutoria(topico,usuarioAutenticado, "No estas autorizado a realizar esta accion");

        validadores.forEach(v->v.validar(datos));

        topico.actualizarDatos(datos);
        topicoRepository.save(topico);

        return (mapearADTOResponseTopic(topico));

    }

   
    public DTOResponseTopic registrarTopico(DTORegistroTopico datos,Usuario usuarioAutenticado){

        Topico topico = new Topico(datos);
        topico.setAutor(usuarioAutenticado);

        validadores.forEach(v->v.validar(datos));
        topicoRepository.save(topico);

        return (mapearADTOResponseTopic(topico));

    }

 
    public void eliminarTopico(Long id,Usuario usuarioAutenticado){

        var topico = topicoRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException()
        );


        validarAutoria(topico, usuarioAutenticado,"No estas autorizado a realizar esta accion");
        topicoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public DTOTopicoYRespuestas retonarDatosTopico(Long id, Pageable pag){
        Topico topico = topicoRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException()
        );

        DTOResponseTopic dtoResponseTopic = mapearADTOResponseTopic(topico);

        Page<DTOResponseRespuesta> dtoResponseRespuestas = respuestaRepository
        .findAllByTopicoIdOrderBySolucionDesc(id,pag)
                .map(DTOResponseRespuesta::new);
        
        return new DTOTopicoYRespuestas(dtoResponseTopic,dtoResponseRespuestas);
    }

    @Transactional(readOnly = true)
    public Page<DTOListadoTopico> listar(Pageable paginacion){
        return topicoRepository.findAll(paginacion).map(DTOListadoTopico::new);
    }


    private void validarAutoria(Topico topico,Usuario usuarioAutenticado, String mensajeDeError){
        if(!topico.getAutor().getId().equals(usuarioAutenticado.getId())){
            throw new AuthorizationException(mensajeDeError);
        }
    }

    private DTOResponseTopic mapearADTOResponseTopic(Topico topico) {
        return new DTOResponseTopic(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                new DTOInfoUsuario(topico.getAutor().getId(), topico.getAutor().getPerfil().getNombre()), // ID de Autor corregido
                topico.getCurso(),
                topico.getNumRespuestas()
        );
    }



}
```

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
