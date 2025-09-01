package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.clients.dtos.EstadoPatchDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface FuentesRetrofitClient {
@GET("hecho/{id}")
  Call<HechoDTO> get(@Path("id") String id);

@PATCH("hecho/{id}")
  Call<Void> patch(@Path("id") String id, @Body EstadoPatchDTO estado);
}
