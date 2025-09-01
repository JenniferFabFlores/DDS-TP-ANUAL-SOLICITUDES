package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.clients.FuentesProxy;
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
    public Fachada fachada(FuentesProxy fuentesProxy) {
        Fachada fachada = new Fachada();
        fachada.setFachadaFuente(fuentesProxy);
        return fachada;
    }
} 