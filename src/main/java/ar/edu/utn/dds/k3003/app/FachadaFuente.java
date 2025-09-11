package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.model.EstadoHechoEnum;

import java.util.NoSuchElementException;

public interface FachadaFuente {

  HechoDTO buscarHechoXId(String hechoId) throws NoSuchElementException;

  HechoDTO actualizarEstado(String hechoId, EstadoHechoEnum nuevoEstado);

}