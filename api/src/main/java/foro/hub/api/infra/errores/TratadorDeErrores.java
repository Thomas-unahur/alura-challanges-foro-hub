package foro.hub.api.infra.errores;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class TratadorDeErrores {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarError404() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DatosErrorValidacion>> tratarError400(MethodArgumentNotValidException e) {
        var errores = e.getFieldErrors().stream()
                .map(DatosErrorValidacion::new)
                .toList();
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(ValidacionDeIntegridad.class)
    public ResponseEntity<DatosMensajeError> errorHandlerValidacionesIntegridad(Exception e) {
        return ResponseEntity.badRequest().body(new DatosMensajeError(e.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<DatosMensajeError> errorHandlerValidacionesDeNegocio(Exception e) {
        return ResponseEntity.badRequest().body(new DatosMensajeError(e.getMessage()));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<DatosMensajeError> errorHandlerAuthorizationException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DatosMensajeError(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<DatosMensajeError> errorHandlerAuthenticationException(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DatosMensajeError(e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DatosMensajeError> errorHandlerJsonNotValidException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new DatosMensajeError("JSON mal formado o estructura invalida."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DatosMensajeError> errorHandlerInternalErrorException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DatosMensajeError("Error inesperado, intenta mas tarde."));
    }

    
    private record DatosErrorValidacion(String campo, String error) {
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

    private record DatosMensajeError(String mensaje) {}
}
