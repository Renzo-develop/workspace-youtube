package com.amigo.programador.mstransaction.service;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.model.Client;
import com.amigo.programador.library.model.DebitCard;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.amigo.programador.mstransaction.entity.Transaction;
import com.amigo.programador.mstransaction.kafka.KafkaProducer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.amigo.programador.mstransaction.repository.*;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.amigo.programador.library.util.LibraryUtil.buildApiResponse;

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
						.map(list -> buildApiResponse("All transactions", list))
						.switchIfEmpty(Mono.just(buildApiResponse("No transaction registered", null)));
	}
	
	public Mono<ApiResponse> createTransactionT(Transaction transaction) {
		
		return findByCardNumber(transaction.getOrigin().getCardNumber())
				.flatMap(origin -> findByCardNumber(transaction.getDestination().getCardNumber())
									.filter(destination -> origin.getBalance() > 0)
									.map(destination -> processTransaction(origin, destination, transaction))									
									.flatMap(tr -> repository.insert(tr)
											.doOnSuccess(trOk -> {
												producer.updateDebitCardBalance(trOk.getOrigin());
												producer.updateDebitCardBalance(trOk.getDestination());
											}))
								  .map(trCreated -> buildApiResponse("Transaction completed !!!", trCreated))
									.switchIfEmpty(Mono.just(buildApiResponse("DebitCard destination not found", null))))
				.switchIfEmpty(Mono.just(buildApiResponse("DebitCard origin not found", null)));
	}
	
	public Mono<ApiResponse> createTransactionDC(Transaction transaction) {
		
		return findByCardNumber(transaction.getOrigin().getCardNumber())
				.map(origin -> processTransaction(origin, null, transaction))
				.filter(tr -> tr.getOrigin().getBalance() >= 0)
				.flatMap(tr -> repository.insert(tr)
								.doOnSuccess(trOk -> producer.updateDebitCardBalance(trOk.getOrigin()))
								.map(trCreated -> buildApiResponse("Transaction completed !!!", trCreated)))
				.switchIfEmpty(Mono.just(buildApiResponse("DebitCard origin not found", null)));
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

		transaction.setTransactionDate(LocalDateTime.now());
		return transaction;
	}

	private Mono<DebitCard> findByCardNumber(String cardNumber) {
		return debitCardWeb.get().uri("/findbycardnumber/{cardNumber}", cardNumber)
						.accept(MediaType.APPLICATION_JSON)
						.retrieve()
						.bodyToMono(ApiResponse.class)
						.filter(apiResponse -> Objects.nonNull(apiResponse.getResponse()))
						.map(apiResponse -> objectMapper.convertValue(apiResponse.getResponse(), DebitCard.class));
	}

	
}
