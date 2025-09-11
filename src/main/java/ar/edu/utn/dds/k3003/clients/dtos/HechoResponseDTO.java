package ar.edu.utn.dds.k3003.clients.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record HechoResponseDTO(
        @JsonProperty("id") String id,
        @JsonProperty("nombre_coleccion") String nombreColeccion,
        @JsonProperty("titulo") String titulo,
        @JsonProperty("etiquetas") List<String> etiquetas,
        @JsonProperty("categoria") String categoria,
        @JsonProperty("ubicacion") String ubicacion,
        @JsonProperty("fecha") LocalDateTime fecha,
        @JsonProperty("origen") String origen,
        @JsonProperty("estado") String estado
) {}
