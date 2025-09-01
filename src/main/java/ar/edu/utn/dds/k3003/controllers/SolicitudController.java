package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.controllers.dtos.SolicitudRequestDTO;
import ar.edu.utn.dds.k3003.controllers.dtos.SolicitudUpdateRequestDTO;
import ar.edu.utn.dds.k3003.controllers.dtos.SolicitudResponseDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final Fachada fachada;

    public SolicitudController(Fachada fachada) {
        this.fachada = fachada;
    }

    @GetMapping
    public ResponseEntity<List<SolicitudResponseDTO>> getSolicitudes(
            @RequestParam(value = "hecho", required = false) String hechoId) {
        try {
            List<SolicitudDTO> solicitudes;
            solicitudes = fachada.buscarSolicitudXHecho(hechoId);

            List<SolicitudResponseDTO> response = solicitudes.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<SolicitudResponseDTO> createSolicitud(
            @RequestBody SolicitudRequestDTO request) {
        try {
            SolicitudDTO solicitudDTO = new SolicitudDTO(
                    null,
                    request.descripcion(),
                    EstadoSolicitudBorradoEnum.CREADA,
                    request.hecho_id()
            );
            
            SolicitudDTO created = fachada.agregar(solicitudDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(convertToResponseDTO(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponseDTO> getSolicitudById(@PathVariable String id) {
        try {
            SolicitudDTO solicitud = fachada.buscarSolicitudXId(id);
            if (solicitud == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(convertToResponseDTO(solicitud));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping
    public ResponseEntity<SolicitudResponseDTO> updateSolicitud(
            @RequestBody SolicitudUpdateRequestDTO request) {
        try {
            SolicitudDTO updated = fachada.modificar(
                    request.id(),
                    request.estado(),
                    request.descripcion()
            );
            return ResponseEntity.ok(convertToResponseDTO(updated));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private SolicitudResponseDTO convertToResponseDTO(SolicitudDTO solicitudDTO) {
        return new SolicitudResponseDTO(
                solicitudDTO.id(),
                solicitudDTO.descripcion(),
                solicitudDTO.estado().toString().toLowerCase(),
                solicitudDTO.hechoId()
        );
    }
} 