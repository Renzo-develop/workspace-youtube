package com.amigo.programador.mstransaction.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.amigo.programador.mstransaction.entity.Transaction;
import reactor.core.publisher.Mono;

@Repository
public interface TransactionRepository  extends ReactiveMongoRepository<Transaction, Long>{

  public Mono<Transaction> findByTransactionCode(String transactionCode);

}
