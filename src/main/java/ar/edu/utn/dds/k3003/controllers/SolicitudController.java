package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.controllers.dtos.HechoResponseDTO;
import ar.edu.utn.dds.k3003.controllers.dtos.SolicitudRequestDTO;
import ar.edu.utn.dds.k3003.controllers.dtos.SolicitudUpdateRequestDTO;
import ar.edu.utn.dds.k3003.controllers.dtos.SolicitudResponseDTO;

import ar.edu.utn.dds.k3003.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(SolicitudController.class);
    private final SolicitudRepository repo;

    public SolicitudController(Fachada fachada, SolicitudRepository repo) {
        this.fachada = fachada;
        this.repo = repo;
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

//    @GetMapping("/hechos/{hechoId}")
//    public ResponseEntity<HechoResponseDTO> getSolicitudesByHechoId(@PathVariable String hechoId) {
//        log.info("🔍 Buscando solicitudes para hechoId={}", hechoId);
//        try {
//            List<SolicitudDTO> solicitudes = fachada.buscarSolicitudXHecho(hechoId);
//            log.debug("Solicitudes encontradas: {}", solicitudes);
//
//            if (solicitudes.isEmpty()) {
//                log.info("No hay solicitudes -> hechoId={} activo=true", hechoId);
//                return ResponseEntity.ok(new HechoResponseDTO(hechoId, true));
//            } else if (solicitudes.stream().anyMatch(solicitud -> solicitud.estado() == EstadoSolicitudBorradoEnum.ACEPTADA)) {
//                log.info("Hay al menos una solicitud ACEPTADA -> hechoId={} activo=false", hechoId);
//                return ResponseEntity.ok(new HechoResponseDTO(hechoId, false));
//            }
//
//            log.info("Solicitudes presentes pero ninguna ACEPTADA -> hechoId={} activo=true", hechoId);
//            return ResponseEntity.ok(new HechoResponseDTO(hechoId, true));
//        }
//        catch (Exception e) {
//            return ResponseEntity.ok(new HechoResponseDTO(hechoId, true));
//        }
//    }

    @GetMapping("/hechos/{hechoId}")
    public ResponseEntity<HechoResponseDTO> getSolicitudesByHechoId(@PathVariable String hechoId) {
        log.info("🔍 Buscando solicitudes para hechoId={}", hechoId);

        if(fachada.estaActivo(hechoId))
            return ResponseEntity.ok(new HechoResponseDTO(hechoId, true));
        else
            return ResponseEntity.ok(new HechoResponseDTO(hechoId, false));
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
    public ResponseEntity<SolicitudResponseDTO> updateSolicitud(@RequestBody SolicitudUpdateRequestDTO request) {
        final int descLen = request.descripcion() == null ? 0 : request.descripcion().length();
        log.info("[SolicitudController.updateSolicitud] request: id={}, estado={}, descLen={}",
                request.id(), request.estado(), descLen);

        try {
            // Firma corregida: (idSolicitud, estado, descripcion)
            SolicitudDTO updated = fachada.modificar(
                    request.id(),
                    request.estado(),
                    request.descripcion()
            );
            log.info("[SolicitudController.updateSolicitud] OK: id={}, nuevoEstado={}",
                    updated.id(), updated.estado());
            return ResponseEntity.ok(convertToResponseDTO(updated));

        } catch (NoSuchElementException e) {
            log.warn("[SolicitudController.updateSolicitud] 404 Not Found: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("[SolicitudController.updateSolicitud] 500 Error inesperado", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/purge")
    public ResponseEntity<PurgeResponse> purgeAll() {
        try {
            long total = repo.count();
            log.warn("[SolicitudController.purge] Borrando TODAS las solicitudes. total={}", total);

            // usa una sola query en BD, más eficiente
            repo.deleteAllInBatch(); // o repo.deleteAll() si preferís

            log.info("[SolicitudController.purge] OK. Eliminadas={}", total);
            return ResponseEntity.ok(new PurgeResponse(total));
        } catch (Exception e) {
            log.error("[SolicitudController.purge] Error borrando todas las solicitudes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // respuesta JSON simple { "eliminadas": N }
    public record PurgeResponse(long eliminadas) {}

    private SolicitudResponseDTO convertToResponseDTO(SolicitudDTO solicitudDTO) {
        return new SolicitudResponseDTO(
                solicitudDTO.id(),
                solicitudDTO.descripcion(),
                solicitudDTO.estado().toString().toLowerCase(),
                solicitudDTO.hechoId()
        );
    }
} 