package com.amigo.programador.mstransaction.controller;

import com.amigo.programador.library.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amigo.programador.mstransaction.entity.Transaction;
import com.amigo.programador.mstransaction.service.TransactionService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	private TransactionService service;
	
	@GetMapping("/findall")
	public Mono<ResponseEntity<ApiResponse>> findAll() {
		return service.findAll()
						.map(apiResponse -> ResponseEntity.ok(apiResponse));
	}
	
	@PostMapping("/create")
	public Mono<ResponseEntity<ApiResponse>> create(@RequestBody Transaction transaction) {
		switch(transaction.getTransactionType()) {
			case DEPOSIT:
			case CASH_OUT:
				return service.createTransactionDC(transaction)
								.map(apiResponse -> ResponseEntity.ok(apiResponse));
			case TRANSFER:
				return service.createTransactionT(transaction)
								.map(apiResponse -> ResponseEntity.ok(apiResponse));
			default:
				return Mono.empty();
		}
	}
}
