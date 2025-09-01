package ar.edu.utn.dds.k3003.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;

@Data
@NoArgsConstructor
public class Solicitud {
    private String id;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private EstadoSolicitudBorradoEnum estado;
    private String justificacion;
    private Hecho hechoAEliminar;
    private boolean ocultado;
    private String hechoId;

    public Solicitud(String id, String descripcion, String hechoId) {
        this.id = id;
        this.descripcion = descripcion;
        this.hechoId = hechoId;
        this.estado = EstadoSolicitudBorradoEnum.CREADA;
    }

    public boolean esJustificacionValida() {
        return justificacion != null && justificacion.length() >= 500;
    }

    public void aceptarSolicitud() {
        if (this.estado != EstadoSolicitudBorradoEnum.CREADA) {
            throw new IllegalStateException("La solicitud no está en estado pendiente");
        }
        this.estado = EstadoSolicitudBorradoEnum.ACEPTADA;
        this.ocultado = true;
        this.hechoAEliminar.setOculto(true);
    }

    public void rechazarSolicitud() {
        if (this.estado != EstadoSolicitudBorradoEnum.CREADA) {
            throw new IllegalStateException("La solicitud no está en estado pendiente");
        }
        this.estado = EstadoSolicitudBorradoEnum.RECHAZADA;
    }
} 