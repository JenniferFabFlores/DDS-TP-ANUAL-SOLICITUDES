package ar.edu.utn.dds.k3003.clients;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.utn.dds.k3003.app.FachadaFuente;
import ar.edu.utn.dds.k3003.clients.dtos.EstadoPatchDTO;
import ar.edu.utn.dds.k3003.clients.dtos.HechoResponseDTO;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.model.EstadoHechoEnum;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FuentesProxy implements FachadaFuente {
    private final String endpoint;
    private final FuentesRetrofitClient service;
    private static final Logger log = LoggerFactory.getLogger(FuentesProxy.class);
  
    public FuentesProxy(ObjectMapper objectMapper) {
      var env = System.getenv();
      this.endpoint = env.getOrDefault("URL_FUENTES", "https://tp-anual-dds-fuentes.onrender.com/api/");
  
      var retrofit =
          new Retrofit.Builder()
              .baseUrl(this.endpoint)
              .addConverterFactory(JacksonConverterFactory.create(objectMapper))
              .build();
  
      this.service = retrofit.create(FuentesRetrofitClient.class);
    }

  @Override
  @SneakyThrows
  public HechoDTO buscarHechoXId(String id) {
    log.info("[FuentesProxy] GET /hechos/{} -> start", id);

    Response<HechoResponseDTO> resp = service.get(id).execute();

    if (resp.isSuccessful()) {
      HechoResponseDTO body = resp.body();
      log.info("[FuentesProxy] GET /hechos/{} <- {} bodyNull={}", id, resp.code(), (body == null));
      if (body == null) {
        return null;
      }
      return new HechoDTO(body.id(), body.titulo(), body.origen());
    }

    int code = resp.code();
    log.warn("[FuentesProxy] GET /hechos/{} <- {} (no exitoso)", id, code);

    if (code == 404) {
      return null;
    }

    throw new RuntimeException("Error conectandose con el componente hechos: HTTP " + code);
  }

  @SneakyThrows
  public HechoDTO actualizarEstado(String id, EstadoHechoEnum estado) {
    EstadoPatchDTO estadoPatch = new EstadoPatchDTO("BORRADO");
    Response<HechoDTO> execute = service.patch(id, estadoPatch).execute();

    if (execute.isSuccessful()) {
      return execute.body() ;
    }
    if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
      return null;
    }
    throw new IllegalArgumentException("Error conectandose con el componente hechos");
  }
}
