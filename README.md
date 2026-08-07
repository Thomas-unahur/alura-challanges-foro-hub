<h1>💬 Foro-Hub API Rest</h1>

<p>Foro-Hub es una API REST diseñada para gestionar un foro orientado a una comunidad de programación. Permite realizar operaciones CRUD sobre tópicos, respuestas y usuarios, manejar autenticación segura con JWT y proveer documentación automática con OpenAPI (SpringDoc).</p>

<h2>📑 Índice</h2>
<ul>
  <li><a href="#tecnologias">Tecnologías</a></li>
  <li><a href="#instalacion-y-configuracion">Instalación y configuración</a></li>
  <li><a href="#uso-rapido-endpoints-principales">Uso rápido (Endpoints principales)</a></li>
  <li><a href="#cambios-recientes-y-refactorizaciones">Cambios recientes y Refactorizaciones</a></li>
  <li><a href="#tests-cobertura-y-ejecucion">Tests (Cobertura y ejecución)</a></li>
  <li><a href="#ejemplos-de-codigo">Ejemplos de código</a></li>
  <li><a href="#documentacion-de-la-api">Documentación de la API</a></li>
  <li><a href="#agradecimientos-y-contribuciones">Agradecimientos y contribuciones</a></li>
</ul>

<hr>

<h2 id="tecnologias">🛠️ Tecnologías</h2>
<ul>
  <li><strong>Backend:</strong> Java 17, Spring Boot 3.3.x</li>
  <li><strong>Seguridad:</strong> Spring Security, JWT (JSON Web Tokens)</li>
  <li><strong>Base de Datos & ORM:</strong> Spring Data JPA, Hibernate, MySQL (Producción), H2 Database (Tests)</li>
  <li><strong>Herramientas:</strong> Maven, Flyway, Lombok, SpringDoc OpenAPI</li>
</ul>

<hr>

<h2 id="instalacion-y-configuracion">🚀 Instalación y configuración</h2>
<ol>
  <li>
    <strong>Clona el repositorio:</strong>
<pre><code class="language-bash">git clone https://github.com/Thomas-unahur/alura-challanges-foro-hub.git</code></pre>
  </li>
  <li>
    <strong>Entra al módulo API y construye el proyecto:</strong>
<pre><code class="language-bash">cd api
mvn install</code></pre>
  </li>
  <li>
    <strong>Configura tu base de datos (MySQL):</strong><br>
    Actualiza el archivo <code>src/main/resources/application.properties</code> o <code>application.yml</code> con tus credenciales locales.
  </li>
  <li>
    <strong>Ejecuta la aplicación:</strong>
<pre><code class="language-bash">mvn spring-boot:run
# o desde el directorio raíz si es multi-módulo:
cd api && mvn spring-boot:run</code></pre>
  </li>
</ol>

<hr>

<h2 id="uso-rapido-endpoints-principales">⚡ Uso rápido (Endpoints principales)</h2>
<ul>
  <li><strong>Registrar tópico:</strong> <code>POST /topicos</code></li>
  <li><strong>Listar tópicos (paginado):</strong> <code>GET /topicos?page=0&size=10&sort=fechaCreacion</code></li>
  <li><strong>Registrar respuesta:</strong> <code>POST /respuestas</code></li>
  <li><strong>Actualizar respuesta:</strong> <code>PUT /respuestas</code></li>
  <li><strong>Eliminar respuesta:</strong> <code>DELETE /respuestas/{id}</code></li>
  <li><strong>Registrar usuario:</strong> <code>POST /usuarios</code></li>
  <li><strong>Login / obtener JWT:</strong> <code>POST /login</code></li>
</ul>
<blockquote><strong>Nota:</strong> Los cuerpos de ejemplo para cada endpoint en formato JSON están disponibles en la documentación OpenAPI (Swagger UI).</blockquote>

<hr>

<h2 id="cambios-recientes-y-refactorizaciones">🔄 Cambios recientes y Refactorizaciones</h2>
<p>En los últimos commits se realizaron refactorizaciones y correcciones importantes. Los puntos más relevantes son:</p>
<ul>
  <li><strong>Módulo de Tópicos (Controller y Service):</strong> Limpieza de imports, corrección de dependencias y mejora en la organización del código para facilitar la inyección por constructor y las pruebas aisladas.</li>
  <li><strong>Autenticación:</strong> El controlador de autenticación ahora devuelve <code>ResponseEntity&lt;DTOJWTToken&gt;</code> (tipo genérico explícito) para mayor claridad en el contrato de la API.</li>
  <li><strong>RespuestaController:</strong>
    <ul>
      <li>Se añadió la inyección de la entidad directamente en los parámetros (<code>@AuthenticationPrincipal Usuario</code>) para los endpoints que crean, actualizan o eliminan respuestas, desacoplando la seguridad de la capa de servicio.</li>
      <li>Se agregó el endpoint para eliminar respuestas (<code>DELETE /respuestas/{id}</code>).</li>
    </ul>
  </li>
  <li><strong>Perfil:</strong> Se agregaron métodos <em>setters</em> en la entidad <code>Perfil</code> para facilitar la preparación de datos (<em>setup</em>) en los tests.</li>
  <li><strong>Repositorios/Servicios de Usuario:</strong> El método para buscar por login (<code>findByLogin</code>) fue refactorizado para devolver un <code>Optional</code>, garantizando un mejor manejo de ausencias y previniendo excepciones de tipo <code>NullPointerException</code>.</li>
  <li><strong>Limpieza general:</strong> Eliminación de imports sin uso y variables innecesarias.</li>
</ul>

<hr>

<h2 id="tests-cobertura-y-ejecucion">🧪 Tests (Cobertura y ejecución)</h2>
<p>Se implementó una suite completa de pruebas automatizadas (unitarias y de integración) para las piezas críticas del dominio:</p>
<ul>
  <li><strong>Tópicos:</strong> Pruebas unitarias que validan flujos de listado, creación, obtención de un tópico con sus respuestas y manejo de errores (ej. <code>EntityNotFoundException</code>).</li>
  <li><strong>Usuarios:</strong> Pruebas de integración sobre repositorios utilizando una base de datos en memoria (H2) para validar el registro, las búsquedas y los flujos de autenticación.</li>
  <li><strong>Respuestas:</strong> Pruebas de creación, actualización y eliminación, verificando estrictamente los permisos y la autoría de los recursos.</li>
</ul>
<p><strong>Características de la suite:</strong></p>
<ul>
  <li>Las pruebas de persistencia utilizan <strong>H2 Database</strong> para evitar ensuciar la base de datos de MySQL durante la ejecución.</li>
  <li>Se abstrajo el tiempo y se utilizaron <em>Mocks</em> para garantizar que las pruebas se ejecuten en milisegundos.</li>
</ul>
<p><strong>Cómo ejecutar los tests:</strong></p>
<p>Desde el directorio raíz (o dentro de la carpeta <code>api</code>):</p>
<pre><code class="language-bash">cd api
mvn test</code></pre>
<hr>

<h2 id="ejemplos-de-codigo">💻 Ejemplos de código (Métodos clave)</h2>
<p>A continuación, se exponen fragmentos representativos de la arquitectura implementada:</p>
<p><strong>1. RespuestaController — Uso de <code>@AuthenticationPrincipal</code></strong></p>
<pre><code class="language-java">@PostMapping
ResponseEntity&lt;DTOResponseRespuesta&gt; registrarRespuesta(
        @RequestBody @Valid DTORegistroRespuesta dtoRegistroRespuesta,
        @AuthenticationPrincipal Usuario usuarioAutenticado,
        UriComponentsBuilder uriComponentsBuilder)

@PutMapping
ResponseEntity&lt;DTOResponseRespuesta&gt; actualizarRespuesta(
        @RequestBody @Valid DTOActualizarRespuesta dtoActualizarRespuesta,
        @AuthenticationPrincipal Usuario usuarioAutenticado)

@DeleteMapping("/{id}")
ResponseEntity&lt;Void&gt; eliminarRespuesta(
        @PathVariable Long id,
        @AuthenticationPrincipal Usuario usuarioAutenticado)</code></pre>

<p><strong>2. AuthLoginService — Orquestación de Autenticación</strong></p>
<pre><code class="language-java">public DTOJWTToken autenticarUsuario(@RequestBody @Valid DTOAuthUsuario datosAutenticacionUsuario) {
    if (intentosLoginService.estaBloqueado(datosAutenticacionUsuario.login())) {
        throw new AuthenticationFailedException("Usuario bloqueado por múltiples intentos fallidos");
    }
    Authentication usuarioAutenticado = authenticationManager.authenticate(...);
    var JWTtoken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
    return new DTOJWTToken(JWTtoken);
}</code></pre>

<p><strong>3. TopicoService — Lógica transaccional y validación de reglas de negocio</strong></p>
<pre><code class="language-java">@Service
@RequiredArgsConstructor
@Transactional
public class TopicoService {
    
    private final TopicoRepository topicoRepository;
    private final List&lt;ValidadorDeDuplicados&gt; validadores;
    private final RespuestaRepository respuestaRepository;

    public DTOResponseTopic actualizarTopico(DTOActualizarTopico datos, Usuario usuarioAutenticado){
        Topico topico = topicoRepository.findById(datos.id())
                .orElseThrow(() -&gt; new EntityNotFoundException("Tópico no encontrado"));

        validarAutoria(topico, usuarioAutenticado, "No estás autorizado a realizar esta acción");
        validadores.forEach(v -&gt; v.validar(datos));

        topico.actualizarDatos(datos);
        topicoRepository.save(topico);

        return mapearADTOResponseTopic(topico);
    }

    // Resto de operaciones (registrar, eliminar, listar)...
    
    private void validarAutoria(Topico topico, Usuario usuarioAutenticado, String mensajeDeError){
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
}</code></pre>
<hr>
<h2 id="documentacion-de-la-api">📖 Documentación de la API</h2>
<p>La documentación interactiva OpenAPI se genera automáticamente con SpringDoc. Para acceder:</p>
<ol>
  <li>Ejecuta la aplicación.</li>
  <li>Abre en tu navegador:
<pre><code class="language-text">http://localhost:8080/swagger-ui/index.html</code></pre>
  </li>
</ol>
<p><em>(Cambia el host o el puerto si tu aplicación corre en otra dirección).</em></p>

<hr>

<h2 id="agradecimientos-y-contribuciones">🤝 Agradecimientos y contribuciones</h2>
<p>Quiero expresar mi agradecimiento al programa <strong>Oracle Next Education</strong> y a <strong>Alura</strong> por el desafío y el excelente material provisto.</p>

<p><strong>Si quieres contribuir:</strong></p>
<ul>
  <li>Abre <em>issues</em> para reportar bugs o sugerir mejoras.</li>
  <li>Envía <em>pull requests</em> con refactorizaciones o nuevas funcionalidades.</li>
  <li>Toda mejora en la documentación o cobertura de tests es bienvenida.</li>
</ul>
