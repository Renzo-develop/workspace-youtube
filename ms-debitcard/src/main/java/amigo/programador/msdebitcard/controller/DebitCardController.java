package amigo.programador.msdebitcard.controller;

import javax.validation.Valid;

import amigo.programador.library.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import amigo.programador.msdebitcard.entity.DebitCard;
import amigo.programador.msdebitcard.service.DebitCardService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;

@RestController
@RequestMapping("/debitcard")
@Slf4j
public class DebitCardController {

	@Autowired
	private DebitCardService debitCardService;
	
	@GetMapping("/findall")
	public Mono<ResponseEntity<ApiResponse>> findAll() {
		return debitCardService.findAll()
						.map(response -> ResponseEntity.ok().body(response));
	}
	
	@GetMapping("/findbyid/{id}")
	public Mono<ResponseEntity<ApiResponse>> findById(@PathVariable Long id) {
		return debitCardService.findById(id)
						.map(response -> ResponseEntity.ok().body(response));
	}
	
	@GetMapping("/findbycardnumber/{cardNumber}")
	public Mono<ResponseEntity<ApiResponse>> findByCardNumber(@PathVariable String cardNumber) {
		return debitCardService.findByCardNumber(cardNumber)
						.map(response -> ResponseEntity.ok().body(response));
	}
	
	@PostMapping("/create")
	public Mono<ResponseEntity<ApiResponse>> createDebitCard(@Valid @RequestBody DebitCard debitCard) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		return debitCardService.createDebitCard(debitCard)
						.map(response -> ResponseEntity.ok().body(response));
	}
	
	@PutMapping("/update")
	public Mono<ResponseEntity<ApiResponse>> updateDebitCard(@RequestBody DebitCard debitCard) {
		return debitCardService.updateDebitCard(debitCard)
						.map(response -> ResponseEntity.ok().body(response));
	}
	
	@DeleteMapping("/delete/{id}")
	public Mono<ResponseEntity<ApiResponse>> deleteDebitCard(@PathVariable Long id) {
		return debitCardService.deleteDebitCard(id)
						.map(response -> ResponseEntity.ok().body(response));
	}
}
