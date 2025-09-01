package ar.edu.utn.dds.k3003.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hecho {
    private Long id;
    private String contenido;
    private String fuente;
    private boolean oculto;
    
    public boolean estaOculto() {
        return oculto;
    }
    
    public void setOculto(boolean oculto) {
        this.oculto = oculto;
    }
} 