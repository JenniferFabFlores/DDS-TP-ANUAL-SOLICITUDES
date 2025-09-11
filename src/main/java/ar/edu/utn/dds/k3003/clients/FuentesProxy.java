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
  public HechoDTO buscarHechoXId(String id) {
    long t0 = System.nanoTime();
    try {
      var call = service.get(id);
      var req = call.request();
      log.info("[FuentesProxy] -> {} {}", req.method(), req.url());

      var resp = call.execute();
      long ms = (System.nanoTime() - t0) / 1_000_000;
      log.info("[FuentesProxy] <- {} {} ({} ms)", resp.code(), resp.message(), ms);

      if (resp.isSuccessful()) {
        var body = resp.body();
        if (body == null) {
          log.warn("[FuentesProxy] body null para id={}", id);
          return null;
        }
        return new HechoDTO(body.id(), body.titulo(), body.origen());
      }

      if (resp.code() == 404) {
        log.info("[FuentesProxy] Hecho no encontrado (404) id={}", id);
        return null;
      }

      String errBody;
      try { errBody = resp.errorBody() != null ? resp.errorBody().string() : "<empty>"; }
      catch (Exception ignore) { errBody = "<unreadable>"; }

      log.error("[FuentesProxy] HTTP {} {}. body={}", resp.code(), resp.message(), errBody);
      throw new RuntimeException("Error conectándose con Hechos: HTTP " + resp.code());

    } catch (java.io.IOException ioe) {
      long ms = (System.nanoTime() - t0) / 1_000_000;
      log.error("[FuentesProxy] IOException llamando Hechos id={} ({} ms): {}", id, ms, ioe.toString(), ioe);
      throw new RuntimeException("Fallo de red llamando a Hechos", ioe);
    } catch (Exception e) {
      log.error("[FuentesProxy] Error inesperado llamando Hechos id={}: {}", id, e.toString(), e);
      throw e;
    }
  }


  @SneakyThrows
  public HechoDTO actualizarEstado(String id, EstadoHechoEnum estado) {
    long t0 = System.currentTimeMillis();
    EstadoPatchDTO body = new EstadoPatchDTO(estado.name());

    log.info("[FuentesProxy] -> PATCH /api/hecho/{} body={{estado:{}}}", id, body.getEstado());
    Response<HechoDTO> execute;
    try {
      execute = service.patch(id, body).execute();
    } catch (Exception e) {
      long dt = System.currentTimeMillis() - t0;
      log.error("[FuentesProxy] IOException PATCH Hechos id={} ({} ms): {}", id, dt, e.toString());
      throw new RuntimeException("Fallo de red llamando a Hechos", e);
    }

    long dt = System.currentTimeMillis() - t0;
    if (execute.isSuccessful()) {
      log.info("[FuentesProxy] <- {} ({} ms) id={}", execute.code(), dt, id);
      return execute.body();
    }

    if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
      log.warn("[FuentesProxy] <- 404 ({} ms) id={}", dt, id);
      return null;
    }

    log.error("[FuentesProxy] <- {} ({} ms) id={} Error conectándose con Hechos", execute.code(), dt, id);
    throw new IllegalArgumentException("Error conectandose con el componente hechos (status " + execute.code() + ")");
  }
}
