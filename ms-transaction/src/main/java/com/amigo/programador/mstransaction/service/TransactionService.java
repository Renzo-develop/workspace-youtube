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

	WebClient dcWebClient = WebClient.create("http://msdebitcard:8081/debitcard");
	
	@Autowired
	private TransactionRepository repository;
	
	@Autowired
	private KafkaProducer producer;
		
	public Flux<Transaction> findAll() {
		return repository.findAll();
	}
	
	public Mono<Transaction> createTransaction(Transaction transaction) {
		
		return dcWebClient.get().uri("/findbycardnumber/{cardNumber}", transaction.getOrigin().getCardNumber())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(DebitCard.class)
				.map(origin -> {
					origin.setBalance(origin.getBalance() + transaction.getTransactionAmount());
					transaction.setOrigin(origin);
					return transaction;
				})
				.flatMap(tr -> repository.insert(tr)
								.doOnSuccess(trOk -> producer.updateDebitCardBalance(trOk.getOrigin())));
	}
	
	
}
