package ar.edu.utn.dds.k3003.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HechoResponseDTO(
    @JsonProperty("id") String id,
    @JsonProperty("activo") Boolean activo

) {} 
