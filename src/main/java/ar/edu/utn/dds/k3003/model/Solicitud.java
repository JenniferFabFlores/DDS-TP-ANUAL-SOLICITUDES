package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;

@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String descripcion;

    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 32)
    private EstadoSolicitudBorradoEnum estado;

    private String justificacion;

    @Transient
    private Hecho hechoAEliminar;

    private boolean ocultado;

    private String hechoId;


    @PrePersist
    private void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        // ocultado como boolean ya es false por defecto
        if (this.estado == null) {
            this.estado = EstadoSolicitudBorradoEnum.CREADA;
        }
    }

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