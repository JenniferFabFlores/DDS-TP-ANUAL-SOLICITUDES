package ar.edu.utn.dds.k3003.clients;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.utn.dds.k3003.clients.dtos.EstadoPatchDTO;
import ar.edu.utn.dds.k3003.clients.dtos.HechoResponseDTO;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class FuentesProxy implements FachadaFuente {
    private final String endpoint;
    private final FuentesRetrofitClient service;
  
    public FuentesProxy(ObjectMapper objectMapper) {
      var env = System.getenv();
      this.endpoint = env.getOrDefault("URL_FUENTES", "https://two025-tp-entrega-2-jagrivero.onrender.com/api/");
  
      var retrofit =
          new Retrofit.Builder()
              .baseUrl(this.endpoint)
              .addConverterFactory(JacksonConverterFactory.create(objectMapper))
              .build();
  
      this.service = retrofit.create(FuentesRetrofitClient.class);
    } 

  @SneakyThrows
  public HechoDTO buscarHechoXId(String id) {
    Response<HechoResponseDTO> execute = service.get(id).execute();

    if (execute.isSuccessful()) {
      HechoResponseDTO response = execute.body();
      if (response == null) {
        return null;
      }
      // Convertir HechoResponseDTO a HechoDTO
      return new HechoDTO(
        response.id(),
        response.titulo(),
        response.origen()
      );
    }
    if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
      return null;
    }
    throw new RuntimeException("Error conectandose con el componente hechos");
  }

  @SneakyThrows
  public boolean darBajaHecho(String id) {
    EstadoPatchDTO estadoPatch = new EstadoPatchDTO("borrado");
    Response<Void> execute = service.patch(id, estadoPatch).execute();

    if (execute.isSuccessful()) {
      return true;
    }
    if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
      return false;
    }
    throw new IllegalArgumentException("Error conectandose con el componente hechos");
  }

  @Override
  public HechoDTO agregar(HechoDTO hechoDTO) {
    return null;
  }

  @Override
  public ColeccionDTO buscarColeccionXId(String id) {
    return null;
  }

  @Override
  public List<HechoDTO> buscarHechosXColeccion(String id) {
    return null;
  }

  @Override
  public void setProcesadorPdI(FachadaProcesadorPdI procesadorPdI) {
    return;
  }
  @Override
  public ColeccionDTO agregar(ColeccionDTO coleccionDTO) {
    return null;
  }

  @Override
  public PdIDTO agregar(PdIDTO pdIDTO) {
    return null;
  }
}
