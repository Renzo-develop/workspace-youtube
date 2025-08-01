package amigo.programador.msdebitcard.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import amigo.programador.msdebitcard.entity.DebitCard;

import reactor.core.publisher.Mono;

@Repository
public interface DebitCardRepository extends ReactiveMongoRepository<DebitCard, Long> {
	
	public Mono<DebitCard> findByCardNumber(String cardNumber); 
	
}
