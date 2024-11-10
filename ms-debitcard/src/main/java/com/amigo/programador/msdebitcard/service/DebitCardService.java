package com.amigo.programador.msdebitcard.service;

import com.amigo.programador.library.ApiResponse;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.amigo.programador.library.Client;
import com.amigo.programador.msdebitcard.entity.DebitCard;
import com.amigo.programador.msdebitcard.repository.DebitCardRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.amigo.programador.utils.ApiResponseUtils.buildApiResponse;

@Service
public class DebitCardService {

	WebClient webMsClient = WebClient.create("http://msclient:8080/client");

	Gson gson = new Gson();

	@Autowired
	private DebitCardRepository debitCardRepository;
	
	public Mono<ApiResponse<List<DebitCard>>> findAll() {
		return debitCardRepository.findAll()
						.collectList()
						.map(cards -> buildApiResponse("DebitCard List", cards));
	}
	
	public Mono<ApiResponse<DebitCard>> findById(Long id) {
		return debitCardRepository.findById(id)
						.map(card -> buildApiResponse("Card found", card))
						.switchIfEmpty(Mono.just(buildApiResponse("Card does not exist", null)));
	}
	
	public Mono<ApiResponse<DebitCard>> findByCardNumber(String cardNumber) {
		return debitCardRepository.findByCardNumber(cardNumber)
						.map(card -> buildApiResponse("Card found", card))
						.switchIfEmpty(Mono.just(buildApiResponse("Card does not exist", null)));
	}
	
	public Mono<DebitCard> updateDebitCard(DebitCard debitCard) {
		return debitCardRepository.save(debitCard);
	}
	
	public Mono<ApiResponse<DebitCard>> createDebitCard(DebitCard debitCard) {
		return webMsClient.get().uri("/findbyid/{id}", debitCard.getClient().getId())
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToMono(ApiResponse.class)
			.map(client -> parseClient(client.getResponse()))
			.flatMap(client -> {
				debitCard.setBalance(0.0);
				debitCard.setClient(client);
				return debitCardRepository.insert(debitCard)
								.map(card -> buildApiResponse("Card Created", card));
			});
	}
	
	public Mono<Void> deleteDebitCard(Long id) {
		return debitCardRepository.deleteById(id);
	}

	private Client parseClient(Object obj) {
		return gson.fromJson(obj.toString(), Client.class);
	}
}
