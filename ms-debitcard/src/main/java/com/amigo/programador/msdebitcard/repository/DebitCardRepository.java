package com.amigo.programador.msdebitcard.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.amigo.programador.msdebitcard.entity.DebitCard;

import reactor.core.publisher.Mono;

@Repository
public interface DebitCardRepository extends ReactiveMongoRepository<DebitCard, Long> {
	
	public Mono<DebitCard> findByCardNumber(String cardNumber); 
	
}
