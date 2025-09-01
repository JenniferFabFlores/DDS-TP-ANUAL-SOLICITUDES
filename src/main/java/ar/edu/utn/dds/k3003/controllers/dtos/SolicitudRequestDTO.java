package ar.edu.utn.dds.k3003.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SolicitudRequestDTO(
    @JsonProperty("descripcion") String descripcion,
    @JsonProperty("hecho_id") String hecho_id
) {} 