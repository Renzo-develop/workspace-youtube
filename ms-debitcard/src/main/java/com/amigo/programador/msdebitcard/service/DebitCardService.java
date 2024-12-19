package com.amigo.programador.msdebitcard.service;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.model.Client;
import com.amigo.programador.library.model.CustomException;
import com.amigo.programador.library.model.CustomExceptionResponse;
import com.amigo.programador.library.util.LibraryUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.amigo.programador.msdebitcard.entity.DebitCard;
import com.amigo.programador.msdebitcard.repository.DebitCardRepository;

import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.amigo.programador.library.util.LibraryUtil.buildApiResponse;
import static com.amigo.programador.library.util.LibraryUtil.buildCustomException;
import static com.amigo.programador.library.util.LibraryUtil.getRootCause;

@Service
@Slf4j
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
						.map(list -> buildApiResponse("Listing Debit cards", list))
						.switchIfEmpty(Mono.just(buildApiResponse("No Debit cards created yet", null)));
	}
	
	public Mono<ApiResponse> findById(Long id) {
		return debitCardRepository.findById(id)
						.map(card -> buildApiResponse("Debit card was found", card))
						.switchIfEmpty(Mono.just(buildApiResponse("Debit card doesn't exists", null)));
	}
	
	public Mono<ApiResponse> findByCardNumber(String cardNumber) {
		return debitCardRepository.findByCardNumber(cardNumber)
						.map(card -> buildApiResponse("Debit card was found", card))
						.switchIfEmpty(Mono.just(buildApiResponse("Debit card doesn't exists", null)));
	}

	public Mono<DebitCard> updateDebitCard(DebitCard debitCard) {
		return debitCardRepository.save(debitCard);
	}

	public Mono<ApiResponse> createDebitCard(DebitCard debitCard) {
		return findClientById(debitCard)
			.flatMap(client -> {
				debitCard.setBalance(0.0);
				debitCard.setClient(client);
				return debitCardRepository.insert(debitCard)
								.map(card -> buildApiResponse("Debit card was created", card));
			})
			.doOnError(error -> log.error("Error creating client - {}", error))
			.onErrorResume(error -> Mono.error(buildCustomException(HttpStatus.CONFLICT, error)))
			.switchIfEmpty(Mono.just(buildApiResponse("Selected client doesn't exists", null)));
	}
	
	public Mono<ApiResponse> deleteDebitCard(Long id) {
		return debitCardRepository.findById(id)
						.flatMap(client -> debitCardRepository.deleteById(id)
										.then(Mono.just(buildApiResponse("Debit card was deleted", client))))
						.switchIfEmpty(Mono.just(buildApiResponse("Debit card doesn't exists", null)));
	}

	private Mono<Client> findClientById(DebitCard debitCard) {
		return webMsClient.get().uri("/findbyid/{id}", debitCard.getClient().getId())
						.accept(MediaType.APPLICATION_JSON)
						.retrieve()
						.bodyToMono(ApiResponse.class)
						.filter(apiResponse -> Objects.nonNull(apiResponse.getResponse()))
						.map(apiResponse -> objectMapper.convertValue(apiResponse.getResponse(), Client.class));
	}

	private void validateCardNumberUnique(DebitCard debitCard) {
		debitCardRepository.countByCardNumber(debitCard.getCardNumber())
						.filter(count -> count > 0)
						.map(count -> Mono.error(
										CustomException.builder()
														.status(HttpStatus.PRECONDITION_FAILED)
														.response(CustomExceptionResponse.builder()
																		.error("Duplicate card number")
																		.message("Card cannot by register using the card number " + debitCard.getCardNumber())
																		.build())
														.build()))
						.subscribe();
	}

}
