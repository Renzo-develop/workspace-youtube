package com.amigo.programador.msdebitcard.third.api;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.model.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class MsClient {

  @Autowired
  private WebClient webMsClient;

  @Autowired
  private ObjectMapper objectMapper;

  public Mono<Client> findClientById(Long id) {
    return webMsClient.get().uri("/findbyid/{id}", id)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(ApiResponse.class)
      .filter(apiResponse -> Objects.nonNull(apiResponse.getResponse()))
      .map(apiResponse -> objectMapper.convertValue(apiResponse.getResponse(), Client.class));
  }

}
