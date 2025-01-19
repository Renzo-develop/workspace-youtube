package com.amigo.programador.mstransaction.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.amigo.programador.mstransaction.entity.Transaction;

@Repository
public interface TransactionRepository  extends ReactiveMongoRepository<Transaction, String>{

}
