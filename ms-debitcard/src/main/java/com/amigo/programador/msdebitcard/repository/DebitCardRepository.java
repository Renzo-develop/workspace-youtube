package com.amigo.programador.msdebitcard.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.amigo.programador.msdebitcard.entity.DebitCard;

@Repository
public interface DebitCardRepository extends ReactiveMongoRepository<DebitCard, Long> {

}
