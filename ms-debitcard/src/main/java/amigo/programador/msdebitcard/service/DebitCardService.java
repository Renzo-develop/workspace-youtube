package amigo.programador.msdebitcard.service;

import static amigo.programador.library.util.LibraryUtil.buildApiResponse;
import static amigo.programador.library.util.LibraryUtil.buildCustomException;
import static amigo.programador.library.util.ValidateUtil.validateDuplicate;

import amigo.programador.msdebitcard.third.api.MsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import amigo.programador.library.model.ApiResponse;
import amigo.programador.msdebitcard.entity.DebitCard;
import amigo.programador.msdebitcard.repository.DebitCardRepository;

import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;

@Service
public class DebitCardService {

	@Autowired
	private DebitCardRepository debitCardRepository;

	@Autowired
	private MsClient msClient;
	
	public Mono<ApiResponse> findAll() {
		return debitCardRepository.findAll()
			.collectList()
			.map(list -> buildApiResponse("Listing DebitCards", list))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	}
	
	public Mono<ApiResponse> findById(Long id) {
		return debitCardRepository.findById(id)
			.map(debitCard -> buildApiResponse("DebitCard was found", debitCard))
			.switchIfEmpty(Mono.just(buildApiResponse("DebitCard doesn't exists", null)))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	}
	
	public Mono<ApiResponse> findByCardNumber(String cardNumber) {
		return debitCardRepository.findByCardNumber(cardNumber)
			.map(debitCard -> buildApiResponse("DebitCard was found", debitCard))
			.switchIfEmpty(Mono.just(buildApiResponse("DebitCard doesn't exists", null)))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	}
	
	public Mono<ApiResponse> updateDebitCard(DebitCard debitCard) {
		return debitCardRepository.save(debitCard)
			.map(dbUpdated -> buildApiResponse("DebitCard was updated", dbUpdated))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	}
	
	public Mono<ApiResponse> createDebitCard(DebitCard debitCard) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		return validateDuplicate(debitCard.getCardNumber(), "findByCardNumber", debitCardRepository, DebitCard.class)
			.then(msClient.findClientById(debitCard.getClient().getId()))
			.flatMap(client -> {
				debitCard.setBalance(0.0);
				debitCard.setClient(client);
				return debitCardRepository.insert(debitCard)
								.map(card -> buildApiResponse("DebitCard created successfully", card));
			})
			.switchIfEmpty(Mono.just(buildApiResponse("Client doesn't exists", null)))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	}

	public Mono<ApiResponse> deleteDebitCard(Long id) {
		return debitCardRepository.findById(id)
			.flatMap(client -> debitCardRepository.deleteById(id)
				.then(Mono.just(buildApiResponse("DebitCard was deleted", client))))
			.switchIfEmpty(Mono.just(buildApiResponse("DebitCard not found", null)))
			.onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	}

}
