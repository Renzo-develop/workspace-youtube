package com.amigo.programador.msdebitcard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.amigo.programador.library.Client;
import com.amigo.programador.msdebitcard.entity.DebitCard;
import com.amigo.programador.msdebitcard.repository.DebitCardRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DebitCardService {

	WebClient webMsClient = WebClient.create("http://msclient:8080/client");
	
	@Autowired
	private DebitCardRepository debitCardRepository;
	
	public Flux<DebitCard> findAll() {
		return debitCardRepository.findAll();
	}
	
	public Mono<DebitCard> findById(Long id) {
		return debitCardRepository.findById(id);
	}
	
	public Mono<DebitCard> findByCardNumber(String cardNumber) {
		return debitCardRepository.findByCardNumber(cardNumber);
	}
	
	public Mono<DebitCard> updateDebitCard(DebitCard debitCard) {
		return debitCardRepository.save(debitCard);
	}
	
	public Mono<DebitCard> createDebitCard(DebitCard debitCard) {
		return webMsClient.get().uri("/findbyid/{id}", debitCard.getClient().getId())
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToMono(Client.class)
			.flatMap(client -> {
				debitCard.setBalance(0.0);
				debitCard.setClient(client);
				return debitCardRepository.insert(debitCard);
			});
	}
	
	public Mono<Void> deleteDebitCard(Long id) {
		return debitCardRepository.deleteById(id);
	}
}
