package com.amigo.programador.msdebitcard.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amigo.programador.msdebitcard.entity.DebitCard;
import com.amigo.programador.msdebitcard.service.DebitCardService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/debitcard")
@Slf4j
public class DebitCardController {

	@Autowired
	private DebitCardService debitCardService;
	
	@GetMapping("/findall")
	public Flux<DebitCard> findAll() {
		return debitCardService.findAll();
	}
	
	@GetMapping("/findbyid/{id}")
	public Flux<DebitCard> findById(@PathVariable Long id) {
		return debitCardService.findAll();
	}
	
	@GetMapping("/findbycardnumber/{cardNumber}")
	public Mono<DebitCard> findByCardNumber(@PathVariable String cardNumber) {
		return debitCardService.findByCardNumber(cardNumber);
	}
	
	@PostMapping("/create")
	public Mono<DebitCard> createDebitCard(@Valid @RequestBody DebitCard debitCard) {
		return debitCardService.createDebitCard(debitCard);
	}
	
	@PutMapping("/update")
	public Mono<DebitCard> updateDebitCard(@RequestBody DebitCard debitCard) {
		return debitCardService.updateDebitCard(debitCard);
	}
	
	@DeleteMapping("/delete/{id}")
	public Mono<Void> deleteDebitCard(@PathVariable Long id) {
		return debitCardService.deleteDebitCard(id);
	}
}
