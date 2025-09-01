package ar.edu.utn.dds.k3003.controllers.dtos;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SolicitudUpdateRequestDTO(
    @JsonProperty("id") String id,
    @JsonProperty("estado") EstadoSolicitudBorradoEnum estado,
    @JsonProperty("descripcion") String descripcion
) {} 