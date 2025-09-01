package ar.edu.utn.dds.k3003.clients.dtos;

public class EstadoPatchDTO {
    private String estado;

    public EstadoPatchDTO() {}

    public EstadoPatchDTO(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
} 