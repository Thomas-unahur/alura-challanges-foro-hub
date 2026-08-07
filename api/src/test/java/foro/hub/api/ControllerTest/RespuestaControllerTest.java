package foro.hub.api.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import foro.hub.api.controller.RespuestaController;
import foro.hub.api.domain.respuestas.DTOActualizarRespuesta;
import foro.hub.api.domain.respuestas.DTORegistroRespuesta;
import foro.hub.api.domain.respuestas.DTOResponseRespuesta;
import foro.hub.api.domain.respuestas.RespuestaService;
import foro.hub.api.infra.security.SecurityConfigurations;
import foro.hub.api.infra.security.SecurityFilter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.FilterType;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
    controllers = RespuestaController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfigurations.class, SecurityFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
class RespuestaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RespuestaService respuestaService;


    @Test
    @DisplayName("Debe registrar respuesta correctamente y retornar codigo 201 con header Location")
    void registrarRespuestaExito() throws Exception {
        DTORegistroRespuesta dtoRegistro = new DTORegistroRespuesta("Mensaje de respuesta", 10L);
        DTOResponseRespuesta dtoResponse = new DTOResponseRespuesta(1L, "Mensaje de respuesta", 10L, new Date(), 1L, false);

        when(respuestaService.registrarRespuesta(any(DTORegistroRespuesta.class), any()))
                .thenReturn(dtoResponse);

        mockMvc.perform(post("/respuestas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRegistro)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/respuestas/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mensaje").value("Mensaje de respuesta"))
                .andExpect(jsonPath("$.topicoID").value(10))
                .andExpect(jsonPath("$.autorRespuestaID").value(1));

        verify(respuestaService).registrarRespuesta(any(DTORegistroRespuesta.class), any());
    }

    @Test
    @DisplayName("Debe retornar codigo 400 cuando los datos de registro son invalidos")
    void registrarRespuestaBadRequest() throws Exception {
        DTORegistroRespuesta dtoInvalido = new DTORegistroRespuesta("", null);

        mockMvc.perform(post("/respuestas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Debe actualizar respuesta correctamente y retornar codigo 200")
    void actualizarRespuestaExito() throws Exception {
        DTOActualizarRespuesta dtoActualizar = new DTOActualizarRespuesta(1L, "Mensaje actualizado");
        DTOResponseRespuesta dtoResponse = new DTOResponseRespuesta(1L, "Mensaje actualizado", 10L, new Date(), 1L, false);

        when(respuestaService.actualizarRespuesta(any(DTOActualizarRespuesta.class), any()))
                .thenReturn(dtoResponse);

        mockMvc.perform(put("/respuestas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mensaje").value("Mensaje actualizado"));

        verify(respuestaService).actualizarRespuesta(any(DTOActualizarRespuesta.class), any());
    }

    @Test
    @DisplayName("Debe retornar codigo 400 cuando los datos de actualizacion son invalidos")
    void actualizarRespuestaBadRequest() throws Exception {
        DTOActualizarRespuesta dtoInvalido = new DTOActualizarRespuesta(null, "");

        mockMvc.perform(put("/respuestas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe eliminar respuesta correctamente y retornar codigo 204")
    void eliminarRespuestaExito() throws Exception {
        Long idRespuesta = 1L;

        mockMvc.perform(delete("/respuestas/{id}", idRespuesta))
                .andExpect(status().isNoContent());

        verify(respuestaService).eliminarRespuesta(eq(idRespuesta), any());
    }
}