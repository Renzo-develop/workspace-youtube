package com.amigo.programador.mstransaction.service;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.model.CustomException;
import com.amigo.programador.library.model.CustomExceptionResponse;
import com.amigo.programador.library.model.DebitCard;
import com.amigo.programador.library.util.LibraryUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.amigo.programador.mstransaction.entity.Transaction;
import com.amigo.programador.mstransaction.kafka.KafkaProducer;
import com.amigo.programador.mstransaction.repository.TransactionRepository;

import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.amigo.programador.library.util.LibraryUtil.buildApiResponse;
import static com.amigo.programador.library.util.LibraryUtil.buildCustomException;
import static com.amigo.programador.library.util.LibraryUtil.getRootCause;

@Service
@Slf4j
public class TransactionService {

	@Autowired
	private WebClient debitCardWeb;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TransactionRepository repository;
	
	@Autowired
	private KafkaProducer producer;
		
	public Mono<ApiResponse> findAll() {
		return repository.findAll()
						.collectList()
						.map(list -> buildApiResponse("Listing transactions", list))
						.switchIfEmpty(Mono.just(buildApiResponse("No transactions created yet", null)));
	}
	
	public Mono<ApiResponse> createTransaction(Transaction transaction) {
		
		return findDebitCard(transaction.getOrigin())
					.flatMap(origin ->
							findDebitCard(transaction.getDestination())
							.map(destination -> processTransaction(origin, destination, transaction))
							.flatMap(tr ->
									repository.insert(tr)
									.doOnSuccess(trOk -> {
										producer.updateDebitCardBalance(trOk.getOrigin());
										producer.updateDebitCardBalance(trOk.getDestination());
									})
									.map(trOk -> buildApiResponse(
											StringUtils.capitalize(transaction.getTransactionType().name()).concat(" created successfully"), trOk))))
					.switchIfEmpty(Mono.just(buildApiResponse(" Card Number doesn't exists", null)))
					.doOnError(ex -> log.error("Error transaction service - {}", ex.toString()))
					.onErrorResume(error -> Mono.error(buildCustomException(HttpStatus.CONFLICT, error)));
				
	}

	public Mono<ApiResponse> createDepositOrCashOut(Transaction transaction) {
		
		return findDebitCard(transaction.getOrigin())
				.map(origin -> processTransaction(origin, null, transaction))
				.flatMap(tr ->
								repository.insert(tr)
								.doOnSuccess(trOk -> producer.updateDebitCardBalance(trOk.getOrigin())))
								.map(trOk -> buildApiResponse(
									StringUtils.capitalize(transaction.getTransactionType().name()).concat(" created successfully"), trOk))
				.switchIfEmpty(Mono.just(buildApiResponse("Card balance is not enough", null)))
				.doOnError(error -> log.error("Error transaction service - {}", error.toString()))
				.onErrorResume(error -> Mono.error(buildCustomException(HttpStatus.CONFLICT, error)));
	}
	
	private Transaction processTransaction(DebitCard origin, DebitCard destination, Transaction transaction) {
		
		switch(transaction.getTransactionType()) {
			case DEPOSIT:
				origin.setBalance(origin.getBalance() + transaction.getTransactionAmount());
				transaction.setOrigin(origin);
				break;
				
			case CASH_OUT:
				validateOriginBalance(origin, transaction);
				origin.setBalance(origin.getBalance() - transaction.getTransactionAmount());
				transaction.setOrigin(origin);
				break;
				
			case TRANSFER:
				validateOriginBalance(origin, transaction);
				origin.setBalance(origin.getBalance() - transaction.getTransactionAmount());
				destination.setBalance(destination.getBalance() + transaction.getTransactionAmount());
				transaction.setOrigin(origin);
				transaction.setDestination(destination);
				break;
				
		}
		return transaction;
	}

	private static void validateOriginBalance(DebitCard origin, Transaction transaction) {
		if(origin.getBalance() < transaction.getTransactionAmount()) {
			throw new IllegalArgumentException("Debit card balance is not enough");
		}
	}

	private Mono<DebitCard> findDebitCard(DebitCard transaction) {
		return debitCardWeb.get().uri("/findbycardnumber/{cardNumber}", transaction.getCardNumber())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(ApiResponse.class)
				.filter(apiResponse -> Objects.nonNull(apiResponse.getResponse()))
				.map(apiResponse -> objectMapper.convertValue(apiResponse.getResponse(), DebitCard.class));
	}

}
