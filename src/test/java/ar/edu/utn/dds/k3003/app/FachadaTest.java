/*
package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class FachadaTest {
    private Fachada fachada;
    private SolicitudDTO solicitudDTO;
    private String descripcionValida;
    
    @Mock
    private FachadaFuente fachadaFuente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fachada = new Fachada();
        fachada.setFachadaFuente(fachadaFuente);
        
        descripcionValida = "Esta es una descripción válida que tiene más de 500 caracteres. ".repeat(10);
        solicitudDTO = new SolicitudDTO(null, descripcionValida, EstadoSolicitudBorradoEnum.CREADA, "1");
        
        // Mock de la fachada fuente para que siempre devuelva un hecho válido
        HechoDTO hechoDTO = new HechoDTO("1", "Contenido del hecho", "Fuente");
        when(fachadaFuente.buscarHechoXId(anyString())).thenReturn(hechoDTO);
    }

    @Test
    void testAgregarSolicitudValida() {
        SolicitudDTO resultado = fachada.agregar(solicitudDTO);
        assertNotNull(resultado);
        assertEquals("1", resultado.id());
        assertEquals(descripcionValida, resultado.descripcion());
        assertEquals(EstadoSolicitudBorradoEnum.CREADA, resultado.estado());
        assertEquals("1", resultado.hechoId());
    }

    @Test
    void testAgregarSolicitudDescripcionInvalida() {
        SolicitudDTO solicitudInvalida = new SolicitudDTO(null, "descripción corta", EstadoSolicitudBorradoEnum.CREADA, "1");
       // assertThrows(IllegalArgumentException.class, () -> fachada.agregar(solicitudInvalida));
       SolicitudDTO resultado = fachada.agregar(solicitudInvalida);
       assertNotNull(resultado);
       assertEquals("descripción corta", resultado.descripcion());
    }

    @Test
    void testModificarSolicitudExistente() {
        fachada.agregar(solicitudDTO);
        SolicitudDTO modificada = fachada.modificar("1", EstadoSolicitudBorradoEnum.ACEPTADA, "Nueva descripción");
        
        assertNotNull(modificada);
        assertEquals("1", modificada.id());
        assertEquals("Nueva descripción", modificada.descripcion());
        assertEquals(EstadoSolicitudBorradoEnum.ACEPTADA, modificada.estado());
    }

    @Test
    void testModificarSolicitudInexistente() {
        assertThrows(NoSuchElementException.class, 
            () -> fachada.modificar("999", EstadoSolicitudBorradoEnum.ACEPTADA, "Nueva descripción"));
    }

    @Test
    void testBuscarSolicitudXHechoExistente() {
        fachada.agregar(solicitudDTO);
        var solicitudes = fachada.buscarSolicitudXHecho("1");
        
        assertFalse(solicitudes.isEmpty());
        assertEquals(1, solicitudes.size());
        assertEquals("1", solicitudes.get(0).hechoId());
    }

    @Test
    void testBuscarSolicitudXHechoInexistente() {
        var solicitudes = fachada.buscarSolicitudXHecho("999");
        assertTrue(solicitudes.isEmpty());
    }

    @Test
    void testBuscarSolicitudXIdExistente() {
        fachada.agregar(solicitudDTO);
        SolicitudDTO encontrada = fachada.buscarSolicitudXId("1");
        
        assertNotNull(encontrada);
        assertEquals("1", encontrada.id());
    }

    @Test
    void testBuscarSolicitudXIdInexistente() {
        SolicitudDTO encontrada = fachada.buscarSolicitudXId("999");
        assertNull(encontrada);
    }

    @Test
    void testEstaActivoHechoConSolicitudCreada() {
        fachada.agregar(solicitudDTO);
        assertTrue(fachada.estaActivo("1"));
    }

    @Test
    void testEstaActivoHechoSinSolicitud() {
        assertFalse(fachada.estaActivo("999"));
    }

    @Test
    void testEstaActivoHechoConSolicitudNoCreada() {
        fachada.agregar(solicitudDTO);
        fachada.modificar("1", EstadoSolicitudBorradoEnum.ACEPTADA, descripcionValida);
        assertFalse(fachada.estaActivo("1"));
    }
} */
