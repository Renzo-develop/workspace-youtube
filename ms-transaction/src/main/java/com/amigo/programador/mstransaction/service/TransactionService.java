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

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

import static com.amigo.programador.library.util.DataValidaton.isUniqueValue;
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

  public Mono<ApiResponse> createTransactionTransference(Transaction transaction) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

    return isUniqueValue(transaction.getTransactionCode(), "findByTransactionCode", transactionRepository)
      .then(msDebitCard.findByCardNumber(transaction.getOrigin().getCardNumber()))// VALIDATE ORIGIN
      .flatMap(origin -> msDebitCard.findByCardNumber(transaction.getDestination().getCardNumber()) // VALIDATE DESTINY
        .map(destination -> processTransaction(origin, destination, transaction))
        .flatMap(tr -> transactionRepository.insert(tr)
          .doOnSuccess(trOk -> {
            kakfaProducer.updateDebitCardBalance(trOk.getOrigin());
            kakfaProducer.updateDebitCardBalance(trOk.getDestination());
          }))
        .map(trCreated -> buildApiResponse("Transaction complete !!!", trCreated))
        .switchIfEmpty(Mono.just(buildApiResponse("Destination DebitCard  not found", null))))
      .switchIfEmpty(Mono.just(buildApiResponse("Origin DebitCard  not found", null)))
      .onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
  }

  public Mono<ApiResponse> createTransactionDepositOrCashOut(Transaction transaction) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

    return isUniqueValue(transaction.getTransactionCode(), "findByTransactionCode", transactionRepository)
			.then(msDebitCard.findByCardNumber(transaction.getOrigin().getCardNumber()))
      .map(origin -> processTransaction(origin, null, transaction))
      .flatMap(tr -> transactionRepository.insert(tr)
        .doOnSuccess(trOk -> kakfaProducer.updateDebitCardBalance(trOk.getOrigin()))
        .map(trCreated -> buildApiResponse("Transaction complete !!!", trCreated)))
      .switchIfEmpty(Mono.just(buildApiResponse("Origin DebitCard  not found", null)))
      .onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
  }

  private Transaction processTransaction(DebitCard origin, DebitCard destination, Transaction transaction) {

    switch (transaction.getTransactionType()) {
      case DEPOSIT:
        origin.setBalance(origin.getBalance() + transaction.getTransactionAmount());
        transaction.setOrigin(origin);
				transaction.setDestination(null);
        break;

      case CASH_OUT:
        origin.setBalance(origin.getBalance() - transaction.getTransactionAmount());
        validateFunds(origin);

        transaction.setOrigin(origin);
				transaction.setDestination(null);
        break;

      case TRANSFER:
        origin.setBalance(origin.getBalance() - transaction.getTransactionAmount());
        destination.setBalance(destination.getBalance() + transaction.getTransactionAmount());
        validateFunds(origin);

        transaction.setOrigin(origin);
        transaction.setDestination(destination);
        break;
    }

    transaction.setTransactionDate(LocalDateTime.now());
    return transaction;
  }

  public Mono<ApiResponse> deleteClient(Long id) {
    return transactionRepository.findById(id)
      .flatMap(transaction -> transactionRepository.deleteById(id)
        .then(Mono.just(buildApiResponse("Transaction has been deleted", transaction))))
      .switchIfEmpty(Mono.just(buildApiResponse("Transaction doesn't exists", null)))
      .onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
  }

  private void validateFunds(DebitCard debitCard) {
    if (debitCard.getBalance() < 0) {
      throw new IllegalArgumentException("Transaction declined: insufficient funds");
    }
  }


}
