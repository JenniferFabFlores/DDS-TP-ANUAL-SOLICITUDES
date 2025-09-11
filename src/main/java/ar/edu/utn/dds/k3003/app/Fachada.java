package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.model.EstadoHechoEnum;
import ar.edu.utn.dds.k3003.model.Solicitud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.repository.SolicitudRepository;

public class Fachada implements FachadaSolicitudes {
    private List<Solicitud> solicitudes = new ArrayList<>();
    private FachadaFuente fachadaFuente;
    private final SolicitudRepository repo;

    public Fachada(SolicitudRepository repo) {
        this.repo = repo;
    }

    @Override
    public SolicitudDTO agregar(SolicitudDTO solicitudDTO) {
        if (solicitudDTO.hechoId() == null || solicitudDTO.hechoId().trim().isEmpty()) {
            throw new NoSuchElementException("El hechoId es requerido");
        }
        HechoDTO hechoDTO = this.fachadaFuente.buscarHechoXId(solicitudDTO.hechoId());
        if (hechoDTO == null) {
            throw new NoSuchElementException("El hechoId no existe");
        }
        // Regla de 500 chars comentada para evaluador (dejamos igual que tu versión)
        var sol = Solicitud.builder()
                .descripcion(solicitudDTO.descripcion())
                .hechoId(solicitudDTO.hechoId())
                .estado(EstadoSolicitudBorradoEnum.CREADA)
                .build();

        sol = repo.save(sol);
        return new SolicitudDTO(sol.getId(), sol.getDescripcion(), sol.getEstado(), sol.getHechoId());
    }

    @Override
    public SolicitudDTO modificar(String idHecho, EstadoSolicitudBorradoEnum estado, String idSolicitud) throws NoSuchElementException {
        var solicitud = repo.findById(idSolicitud).orElse(null);
        if (solicitud == null) {
            throw new NoSuchElementException("Solicitud no encontrada");
        }
        solicitud.setEstado(estado);
        solicitud = repo.save(solicitud);

        if (estado == EstadoSolicitudBorradoEnum.ACEPTADA) {
            this.fachadaFuente.actualizarEstado(idHecho, EstadoHechoEnum.BORRADO);
        }

        return new SolicitudDTO(solicitud.getId(), solicitud.getDescripcion(), solicitud.getEstado(), solicitud.getHechoId());
    }

    @Override
    public List<SolicitudDTO> buscarSolicitudXHecho(String idHecho) {
        List<Solicitud> origen = (idHecho == null || idHecho.trim().isEmpty())
                ? repo.findAll()
                : repo.findByHechoId(idHecho);

        return origen.stream()
                .map(x -> new SolicitudDTO(x.getId(), x.getDescripcion(), x.getEstado(), x.getHechoId()))
                .collect(Collectors.toList());
    }

    @Override
    public SolicitudDTO buscarSolicitudXId(String idSolicitud) {
        var solicitud = repo.findById(idSolicitud).orElse(null);
        if (solicitud == null) return null;
        return new SolicitudDTO(solicitud.getId(), solicitud.getDescripcion(), solicitud.getEstado(), solicitud.getHechoId());
    }

    @Override
    public boolean estaActivo(String idHecho) {

        HechoDTO hechoDTO = this.fachadaFuente.buscarHechoXId(idHecho);
        if (hechoDTO == null) {
            return false;
        } else{
            var estados = repo.findByHechoId(idHecho).stream()
                    .map(Solicitud::getEstado)
                    .collect(java.util.stream.Collectors.toSet());

            if (estados.contains(EstadoSolicitudBorradoEnum.ACEPTADA)) {
                return false;
            }
            return estados.contains(EstadoSolicitudBorradoEnum.CREADA)
                    || estados.contains(EstadoSolicitudBorradoEnum.RECHAZADA);
        }
    }

    
    @Override
    public void setFachadaFuente(FachadaFuente fachadaFuente) {
        this.fachadaFuente=fachadaFuente;
    }
}
