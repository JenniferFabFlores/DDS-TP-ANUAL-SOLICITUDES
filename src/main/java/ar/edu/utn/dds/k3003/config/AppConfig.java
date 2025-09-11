package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.clients.FuentesProxy;
import ar.edu.utn.dds.k3003.repository.SolicitudRepository; // 👈 importante: mismo paquete que tu repo
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public FuentesProxy fuentesProxy(ObjectMapper objectMapper) {
        return new FuentesProxy(objectMapper);
    }

    @Bean
    public Fachada fachada(SolicitudRepository repo, FuentesProxy fuentesProxy) {
        Fachada fachada = new Fachada(repo);
        fachada.setFachadaFuente(fuentesProxy);
        return fachada;
    }
}
