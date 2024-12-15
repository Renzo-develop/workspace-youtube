package com.amigo.programador.mstransaction.service;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.model.Client;
import com.amigo.programador.library.model.CustomException;
import com.amigo.programador.library.model.CustomExceptionResponse;
import com.amigo.programador.library.model.DebitCard;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.amigo.programador.mstransaction.entity.Transaction;
import com.amigo.programador.mstransaction.kafka.KafkaProducer;
import com.amigo.programador.mstransaction.repository.TransactionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.amigo.programador.library.util.LibraryUtil.getRootCause;

@Service
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
						.map(list -> ApiResponse.builder()
										.message("Listing transactions")
										.response(list)
										.build())
						.switchIfEmpty(Mono.just(ApiResponse.builder()
										.message("No transactions created yet")
										.build()));
	}
	
	public Mono<ApiResponse> createTransactionT(Transaction transaction) {
		
		return debitCardWeb.get().uri("/findbycardnumber/{cardNumber}", transaction.getOrigin().getCardNumber())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(ApiResponse.class)
				.filter(apiResponse -> Objects.nonNull(apiResponse.getResponse()))
				.map(apiResponse -> objectMapper.convertValue(apiResponse.getResponse(), DebitCard.class))
				.flatMap(origin -> debitCardWeb.get().uri("/findbycardnumber/{cardNumber}", transaction.getDestination().getCardNumber())
									.accept(MediaType.APPLICATION_JSON)
									.retrieve()
									.bodyToMono(ApiResponse.class)
									.filter(apiResponse -> Objects.nonNull(apiResponse.getResponse()))
									.map(apiResponse -> objectMapper.convertValue(apiResponse.getResponse(), DebitCard.class))
									.filter(destination -> origin.getBalance() > 0)
									.map(destination -> processTransaction(origin, destination, transaction))									
									.flatMap(tr -> repository.insert(tr)
											.doOnSuccess(trOk -> {
												producer.updateDebitCardBalance(trOk.getOrigin());
												producer.updateDebitCardBalance(trOk.getDestination());
											})
											.map(trOk -> ApiResponse.builder()
															.message("Transaction created successfully")
															.response(trOk)
															.build())
									))
				.switchIfEmpty(Mono.just(ApiResponse.builder()
								.message("Selected client doesn't exists")
								.build()))
				.onErrorResume(error -> Mono.error(
								CustomException.builder()
												.status(HttpStatus.CONFLICT)
												.response(CustomExceptionResponse.builder()
																.error(getRootCause(error))
																.message(error.getMessage())
																.build())
												.build()));
				
	}
	
	public Mono<ApiResponse> createTransactionDC(Transaction transaction) {
		
		return debitCardWeb.get().uri("/findbycardnumber/{cardNumber}", transaction.getOrigin().getCardNumber())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(ApiResponse.class)
				.filter(apiResponse -> Objects.nonNull(apiResponse.getResponse()))
				.map(apiResponse -> objectMapper.convertValue(apiResponse.getResponse(), DebitCard.class))
				.map(origin -> processTransaction(origin, null, transaction))
				.filter(tr -> tr.getOrigin().getBalance() >= 0)
				.flatMap(tr -> repository.insert(tr)
								.doOnSuccess(trOk -> producer.updateDebitCardBalance(trOk.getOrigin())))
								.map(trOk -> ApiResponse.builder()
												.message("Transaction created successfully")
												.response(trOk)
												.build())
				.switchIfEmpty(Mono.just(ApiResponse.builder()
								.message("Card balance is not enough")
								.build()))
				.onErrorResume(error -> Mono.error(
								CustomException.builder()
												.status(HttpStatus.CONFLICT)
												.response(CustomExceptionResponse.builder()
																.error(getRootCause(error))
																.message(error.getMessage())
																.build())
												.build()));
	}
	
	private Transaction processTransaction(DebitCard origin, DebitCard destination, Transaction transaction) {
		
		switch(transaction.getTransactionType()) {
			case DEPOSIT:
				origin.setBalance(origin.getBalance() + transaction.getTransactionAmount());
				transaction.setOrigin(origin);
				break;
				
			case CASH_OUT:
				origin.setBalance(origin.getBalance() - transaction.getTransactionAmount());
				transaction.setOrigin(origin);
				break;
				
			case TRANSFER:
				origin.setBalance(origin.getBalance() - transaction.getTransactionAmount());
				destination.setBalance(destination.getBalance() + transaction.getTransactionAmount());
				transaction.setOrigin(origin);
				transaction.setDestination(destination);
				break;
				
		}
		return transaction;
	}
	
	
}
