package com.amigo.programador.msdebitcard.controller;

import javax.validation.Valid;

import com.amigo.programador.library.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

import java.util.List;

import static com.amigo.programador.utils.ApiResponseUtils.buildApiResponse;

@RestController
@RequestMapping("/debitcard")
@Slf4j
public class DebitCardController {

	@Value("${properties.config.server}")
	private String configserver;
	
	@Autowired
	private DebitCardService debitCardService;
	
	@GetMapping("/findall")
	public Mono<ResponseEntity<ApiResponse<List<DebitCard>>>> findAll() {
		log.info("Connecting to config server? -> " + configserver);
		return debitCardService.findAll()
						.map(cards -> ResponseEntity.ok(cards));
	}
	
	@GetMapping("/findbyid/{id}")
	public Mono<ResponseEntity<ApiResponse<DebitCard>>> findById(@PathVariable Long id) {
		return debitCardService.findById(id)
						.map(card -> ResponseEntity.ok(card));
	}
	
	@GetMapping("/findbycardnumber/{cardNumber}")
	public Mono<ResponseEntity<ApiResponse<DebitCard>>> findByCardNumber(@PathVariable String cardNumber) {
		return debitCardService.findByCardNumber(cardNumber)
						.map(card -> ResponseEntity.ok(card));
	}
	
	@PostMapping("/create")
	public Mono<ResponseEntity<ApiResponse<DebitCard>>> createDebitCard(@Valid @RequestBody DebitCard debitCard) {
		return debitCardService.createDebitCard(debitCard)
						.map(card -> ResponseEntity.ok(card));
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
