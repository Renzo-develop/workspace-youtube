package amigo.programador.msclient.service;

import static amigo.programador.library.util.LibraryUtil.buildApiResponse;
import static amigo.programador.library.util.LibraryUtil.buildCustomException;
import static amigo.programador.library.util.ValidateUtil.validateDuplicate;

import amigo.programador.library.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import amigo.programador.msclient.entity.Client;
import amigo.programador.msclient.repository.ClientRepository;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;

@Service
@Slf4j
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;
	
	 public Mono<ApiResponse> findAll() {

		 return clientRepository.findAll()
			 .collectList()
			 .map(list -> buildApiResponse("Listing clients", list))
			 .onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	 }
	 
	 public Mono<ApiResponse> findById(Long id) {
		 return clientRepository.findById(id)
			 .map(client -> buildApiResponse("Client was found", client))
			 .switchIfEmpty(Mono.just(buildApiResponse("Client doesn't exists", null)))
			 .onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	 }
	 
	 public Mono<ApiResponse> createClient(Client client) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		 return validateDuplicate(client.getDni(), "findByDni", clientRepository, Client.class)
			 .then(clientRepository.insert(client))
			 .map(c -> buildApiResponse("Client has been created", c))
			 .doOnError(ex -> log.error("Error creating client - {}", ex.getMessage()))
			 .onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	 }
	 
	 public Mono<ApiResponse> deleteClient(Long id) {
		 return clientRepository.findById(id)
			 .flatMap(client -> clientRepository.deleteById(id)
							 .then(Mono.just(buildApiResponse("Client has been deleted", client))))
			 .switchIfEmpty(Mono.just(buildApiResponse("Client can't be deleted because doesn't exists", null)))
			 .onErrorResume(ex -> Mono.error(buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex)));
	 }
}
