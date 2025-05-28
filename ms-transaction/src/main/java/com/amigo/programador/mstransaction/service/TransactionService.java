package com.amigo.programador.mstransaction.service;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.model.DebitCard;
import com.amigo.programador.mstransaction.external.api.MsDebitCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.amigo.programador.mstransaction.entity.Transaction;
import com.amigo.programador.mstransaction.kafka.KafkaProducer;

import reactor.core.publisher.Mono;
import com.amigo.programador.mstransaction.repository.*;

import java.time.LocalDateTime;

import static com.amigo.programador.library.util.LibraryUtil.buildApiResponse;
import static com.amigo.programador.library.util.LibraryUtil.buildCustomException;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private KafkaProducer kakfaProducer;

	@Autowired
	private MsDebitCard msDebitCard;
		
	public Mono<ApiResponse> findAll() {

		return transactionRepository.findAll()
			.collectList()
			.map(list -> buildApiResponse("All transactions", list))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	}
	
	public Mono<ApiResponse> createTransactionTransference(Transaction transaction) {
		
		return msDebitCard.findByCardNumber(transaction.getOrigin().getCardNumber())
			.flatMap(origin -> msDebitCard.findByCardNumber(transaction.getDestination().getCardNumber())
								.filter(destination -> origin.getBalance() >= transaction.getTransactionAmount())
								.map(destination -> processTransaction(origin, destination, transaction))
								.flatMap(tr -> transactionRepository.insert(tr)
										.doOnSuccess(trOk -> {
											kakfaProducer.updateDebitCardBalance(trOk.getOrigin());
											kakfaProducer.updateDebitCardBalance(trOk.getDestination());
										}))
								.map(trCreated -> buildApiResponse("Transaction complete !!!", trCreated))
								.switchIfEmpty(Mono.just(buildApiResponse("Destination Card  not found", null))))
			.switchIfEmpty(Mono.just(buildApiResponse("Origin Card  not found", null)))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	}
	
	public Mono<ApiResponse> createTransactionDepositOrCashOut(Transaction transaction) {
		
		return msDebitCard.findByCardNumber(transaction.getOrigin().getCardNumber())
			.map(origin -> processTransaction(origin, null, transaction))
			.filter(tr -> tr.getOrigin().getBalance() >= 0)
			.flatMap(tr -> transactionRepository.insert(tr)
							.doOnSuccess(trOk -> kakfaProducer.updateDebitCardBalance(trOk.getOrigin()))
							.map(trCreated -> buildApiResponse("Transaction complete !!!", trCreated)))
			.switchIfEmpty(Mono.just(buildApiResponse("Origin Card  not found", null)))
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

	public Mono<ApiResponse> deleteClient(String id) {
		return transactionRepository.findById(id)
			.flatMap(transaction -> transactionRepository.deleteById(id)
							.then(Mono.just(buildApiResponse("Transaction has been deleted", transaction))))
			.switchIfEmpty(Mono.just(buildApiResponse("Transaction doesn't exists", null)))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	}

}
