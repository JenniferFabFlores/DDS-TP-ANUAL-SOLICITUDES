package ar.edu.utn.dds.k3003.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SolicitudResponseDTO(
    @JsonProperty("id") String id,
    @JsonProperty("descripcion") String descripcion,
    @JsonProperty("estado") String estado,
    @JsonProperty("hecho_id") String hecho_id
) {} 