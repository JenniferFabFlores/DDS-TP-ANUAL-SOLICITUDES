package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.controllers.dtos.SolicitudRequestDTO;
import ar.edu.utn.dds.k3003.controllers.dtos.SolicitudUpdateRequestDTO;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Fachada fachada;

    @Autowired
    private ObjectMapper objectMapper;

    private String descripcionValida;

    @BeforeEach
    void setUp() {
        descripcionValida = "Esta es una descripción válida que tiene más de 500 caracteres. ".repeat(10);
    }

    @Test
    void testCreateSolicitudSuccess() throws Exception {
        SolicitudRequestDTO request = new SolicitudRequestDTO(descripcionValida, "1");
        
        when(fachada.agregar(any())).thenReturn(
            new ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO("1", descripcionValida, EstadoSolicitudBorradoEnum.CREADA, "1")
        );

        mockMvc.perform(post("/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.descripcion").value(descripcionValida))
                .andExpect(jsonPath("$.estado").value("creada"))
                .andExpect(jsonPath("$.hecho_id").value("1"));
    }

    @Test
    void testCreateSolicitudInvalidDescription() throws Exception {
        SolicitudRequestDTO request = new SolicitudRequestDTO("descripción corta", "1");
        
        when(fachada.agregar(any())).thenThrow(new IllegalArgumentException("La descripcion debe tener al menos 500 caracteres"));

        mockMvc.perform(post("/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetSolicitudByIdSuccess() throws Exception {
        when(fachada.buscarSolicitudXId("1")).thenReturn(
            new ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO("1", descripcionValida, EstadoSolicitudBorradoEnum.CREADA, "1")
        );

        mockMvc.perform(get("/solicitudes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.descripcion").value(descripcionValida))
                .andExpect(jsonPath("$.estado").value("creada"))
                .andExpect(jsonPath("$.hecho_id").value("1"));
    }

    @Test
    void testGetSolicitudByIdNotFound() throws Exception {
        when(fachada.buscarSolicitudXId("999")).thenReturn(null);

        mockMvc.perform(get("/solicitudes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetSolicitudesByHecho() throws Exception {
        when(fachada.buscarSolicitudXHecho("1")).thenReturn(
            java.util.List.of(
                new ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO("1", descripcionValida, EstadoSolicitudBorradoEnum.CREADA, "1")
            )
        );

        mockMvc.perform(get("/solicitudes?hecho=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].descripcion").value(descripcionValida))
                .andExpect(jsonPath("$[0].estado").value("creada"))
                .andExpect(jsonPath("$[0].hecho_id").value("1"));
    }

    @Test
    void testGetSolicitudesWithoutHecho() throws Exception {
        when(fachada.buscarSolicitudXHecho(null)).thenReturn(
            java.util.List.of(
                new ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO("1", descripcionValida, EstadoSolicitudBorradoEnum.CREADA, "1"),
                new ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO("2", descripcionValida, EstadoSolicitudBorradoEnum.ACEPTADA, "2")
            )
        );

        mockMvc.perform(get("/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
    }

    @Test
    void testUpdateSolicitudSuccess() throws Exception {
        SolicitudUpdateRequestDTO request = new SolicitudUpdateRequestDTO("1", EstadoSolicitudBorradoEnum.ACEPTADA, "Nueva descripción");
        
        when(fachada.modificar(eq("1"), eq(EstadoSolicitudBorradoEnum.ACEPTADA), eq("Nueva descripción")))
            .thenReturn(new ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO("1", "Nueva descripción", EstadoSolicitudBorradoEnum.ACEPTADA, "1"));

        mockMvc.perform(patch("/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.descripcion").value("Nueva descripción"))
                .andExpect(jsonPath("$.estado").value("aceptada"))
                .andExpect(jsonPath("$.hecho_id").value("1"));
    }

    @Test
    void testUpdateSolicitudNotFound() throws Exception {
        SolicitudUpdateRequestDTO request = new SolicitudUpdateRequestDTO("999", EstadoSolicitudBorradoEnum.ACEPTADA, "Nueva descripción");
        
        when(fachada.modificar(eq("999"), eq(EstadoSolicitudBorradoEnum.ACEPTADA), eq("Nueva descripción")))
            .thenThrow(new java.util.NoSuchElementException("Solicitud no encontrada"));

        mockMvc.perform(patch("/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
} 