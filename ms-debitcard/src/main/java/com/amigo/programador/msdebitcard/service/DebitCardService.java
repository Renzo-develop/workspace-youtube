package com.amigo.programador.msdebitcard.service;

import static com.amigo.programador.library.util.LibraryUtil.buildApiResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.model.Client;
import com.amigo.programador.msdebitcard.entity.DebitCard;
import com.amigo.programador.msdebitcard.repository.DebitCardRepository;

import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class DebitCardService {

	@Autowired
	private WebClient webMsClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private DebitCardRepository debitCardRepository;
	
	public Mono<ApiResponse> findAll() {
		return debitCardRepository.findAll()
						.collectList()
						.map(list -> buildApiResponse("Listing DebitCards", list))
						.switchIfEmpty(Mono.just(buildApiResponse("No debit cards found", null)));
	}
	
	public Mono<ApiResponse> findById(Long id) {
		return debitCardRepository.findById(id)
						.map(debitCard -> buildApiResponse("DebitCard was found", debitCard))
						.switchIfEmpty(Mono.just(buildApiResponse("DebitCard doesn't exists", null)));
	}
	
	public Mono<ApiResponse> findByCardNumber(String cardNumber) {
		return debitCardRepository.findByCardNumber(cardNumber)
						.map(debitCard -> buildApiResponse("DebitCard was found", debitCard))
						.switchIfEmpty(Mono.just(buildApiResponse("DebitCard doesn't exists", null)));
	}
	
	public Mono<ApiResponse> updateDebitCard(DebitCard debitCard) {
		return debitCardRepository.save(debitCard)
						.map(dbUpdated -> buildApiResponse("DebitCard was updated", dbUpdated));
	}
	
	public Mono<ApiResponse> createDebitCard(DebitCard debitCard) {
		return findClientById(debitCard.getClient().getId())
			.flatMap(client -> {
				debitCard.setBalance(0.0);
				debitCard.setClient(client);
				return debitCardRepository.insert(debitCard)
								.map(card -> buildApiResponse("DebitCard created successfully", card));
			})
			.switchIfEmpty(Mono.just(buildApiResponse("Client doesn't exists", null)));
	}

	public Mono<ApiResponse> deleteDebitCard(Long id) {
		return debitCardRepository.findById(id)
						.flatMap(client -> debitCardRepository.deleteById(id)
							.then(Mono.just(buildApiResponse("DebitCard was deleted", client))))
							.switchIfEmpty(Mono.just(buildApiResponse("DebitCard not found", null)));


	}

	private Mono<Client> findClientById(Long id) {
		return webMsClient.get().uri("/findbyid/{id}", id)
						.accept(MediaType.APPLICATION_JSON)
						.retrieve()
						.bodyToMono(ApiResponse.class)
						.filter(apiResponse -> Objects.nonNull(apiResponse.getResponse()))
						.map(apiResponse -> objectMapper.convertValue(apiResponse.getResponse(), Client.class));
	}

}
