package com.amigo.programador.mstransaction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.amigo.programador.library.DebitCard;
import com.amigo.programador.mstransaction.entity.Transaction;
import com.amigo.programador.mstransaction.kafka.KafkaProducer;
import com.amigo.programador.mstransaction.repository.TransactionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {

	@Autowired
	private WebClient debitCardWeb;
	
	@Autowired
	private TransactionRepository repository;
	
	@Autowired
	private KafkaProducer producer;
		
	public Flux<Transaction> findAll() {
		return repository.findAll();
	}
	
	public Mono<Transaction> createTransactionT(Transaction transaction) {
		
		return debitCardWeb.get().uri("/findbycardnumber/{cardNumber}", transaction.getOrigin().getCardNumber())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(DebitCard.class)
				.flatMap(origin -> debitCardWeb.get().uri("/findbycardnumber/{cardNumber}", transaction.getDestination().getCardNumber())
									.accept(MediaType.APPLICATION_JSON)
									.retrieve()
									.bodyToMono(DebitCard.class)
									.filter(destination -> origin.getBalance() > 0)
									.map(destination -> processTransaction(origin, destination, transaction))									
									.flatMap(tr -> repository.insert(tr)
											.doOnSuccess(trOk -> {
												producer.updateDebitCardBalance(trOk.getOrigin());
												producer.updateDebitCardBalance(trOk.getDestination());
											}))
									
						);
				
	}
	
	public Mono<Transaction> createTransactionDC(Transaction transaction) {
		
		return debitCardWeb.get().uri("/findbycardnumber/{cardNumber}", transaction.getOrigin().getCardNumber())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(DebitCard.class)
				.map(origin -> processTransaction(origin, null, transaction))
				.filter(tr -> tr.getOrigin().getBalance() >= 0)
				.flatMap(tr -> repository.insert(tr)
								.doOnSuccess(trOk -> producer.updateDebitCardBalance(trOk.getOrigin())));
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
