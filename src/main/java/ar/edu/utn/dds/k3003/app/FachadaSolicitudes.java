package ar.edu.utn.dds.k3003.app;

import java.util.List;
import java.util.NoSuchElementException;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;

public interface FachadaSolicitudes {
    SolicitudDTO agregar(SolicitudDTO var1);
 
    SolicitudDTO modificar(String var1, EstadoSolicitudBorradoEnum var2, String var3) throws NoSuchElementException;
 
    List<SolicitudDTO> buscarSolicitudXHecho(String var1);
 
    SolicitudDTO buscarSolicitudXId(String var1);
 
    boolean estaActivo(String var1);
 
    void setFachadaFuente(FachadaFuente var1);
 }
 