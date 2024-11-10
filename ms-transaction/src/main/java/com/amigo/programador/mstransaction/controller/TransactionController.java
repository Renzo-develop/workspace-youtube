package com.amigo.programador.mstransaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amigo.programador.library.DebitCard;
import com.amigo.programador.mstransaction.entity.Transaction;
import com.amigo.programador.mstransaction.service.TransactionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	
	@Autowired
	private TransactionService service;
	
	@GetMapping("/findall")
	public Flux<Transaction> findAll() {
		return service.findAll();
	}
	
	@PostMapping("/create")
	public Mono<Transaction> create(@RequestBody Transaction transaction) {
		switch(transaction.getTransactionType()) {
			case DEPOSIT:
			case CASH_OUT:
				return service.createTransactionDC(transaction);
			case TRANSFER:
				return service.createTransactionT(transaction);
			default:
				return Mono.empty();
		}
	}
}
