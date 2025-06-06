package com.amigo.programador.mstransaction.service;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.model.DebitCard;
import com.amigo.programador.mstransaction.third.api.MsDebitCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.amigo.programador.mstransaction.entity.Transaction;
import com.amigo.programador.mstransaction.kafka.KafkaProducer;

import reactor.core.publisher.Mono;
import com.amigo.programador.mstransaction.repository.*;

import java.lang.management.MonitorInfo;
import java.time.LocalDateTime;

import static com.amigo.programador.library.util.LibraryUtil.buildApiResponse;
import static com.amigo.programador.library.util.LibraryUtil.buildCustomException;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository repository;
	
	@Autowired
	private KafkaProducer producer;

	@Autowired
	private MsDebitCard msDebitCard;
		
	public Mono<ApiResponse> findAll() {

		return repository.findAll()
			.collectList()
			.map(list -> buildApiResponse("All transactions", list))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	}
	
	public Mono<ApiResponse> createTransference(Transaction transaction) {
		
		return msDebitCard.findByCardNumber(transaction.getOrigin().getCardNumber())
			.flatMap(origin -> msDebitCard.findByCardNumber(transaction.getDestination().getCardNumber())
								.filter(destination -> origin.getBalance() > 0)
								.map(destination -> processTransaction(origin, destination, transaction))
								.flatMap(tr -> repository.insert(tr)
										.doOnSuccess(trOk -> {
											producer.updateDebitCardBalance(trOk.getOrigin());
											producer.updateDebitCardBalance(trOk.getDestination());
										}))
								.map(trCreated -> buildApiResponse("Transaction completed !!!", trCreated))
								.switchIfEmpty(Mono.just(buildApiResponse("DebitCard destination not found", null))))
			.switchIfEmpty(Mono.just(buildApiResponse("DebitCard origin not found", null)))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	}
	
	public Mono<ApiResponse> createDepositOrCashOut(Transaction transaction) {
		
		return msDebitCard.findByCardNumber(transaction.getOrigin().getCardNumber())
			.map(origin -> processTransaction(origin, null, transaction))
			.filter(tr -> tr.getOrigin().getBalance() >= 0)
			.flatMap(tr -> repository.insert(tr)
							.doOnSuccess(trOk -> producer.updateDebitCardBalance(trOk.getOrigin()))
							.map(trCreated -> buildApiResponse("Transaction completed !!!", trCreated)))
			.switchIfEmpty(Mono.just(buildApiResponse("DebitCard origin not found", null)))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
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

	public Mono<ApiResponse> deleteTransaction(Long id) {
		return repository.findById(id)
			.flatMap(transaction -> repository.delete(transaction)
				.then(Mono.just(buildApiResponse("Transaction deleted", null))))
			.switchIfEmpty(Mono.just(buildApiResponse("Transaction does not exists", null)))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));

	}



	
}
