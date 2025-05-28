package com.amigo.programador.mstransaction.external.api;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.model.DebitCard;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class MsDebitCard {

  @Autowired
  private WebClient debitCardWeb;

  @Autowired
  private ObjectMapper objectMapper;

  public Mono<DebitCard> findByCardNumber(String cardNumber) {
    return debitCardWeb.get().uri("/findbycardnumber/{cardNumber}", cardNumber)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(ApiResponse.class)
      .filter(apiResponse -> Objects.nonNull(apiResponse.getResponse()))
      .map(apiResponse -> objectMapper.convertValue(apiResponse.getResponse(), DebitCard.class));
  }

}
