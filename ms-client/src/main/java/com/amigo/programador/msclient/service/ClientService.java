package com.amigo.programador.msclient.service;

import static com.amigo.programador.library.util.DataValidaton.isUniqueValue;
import static com.amigo.programador.library.util.LibraryUtil.buildApiResponse;
import static com.amigo.programador.library.util.LibraryUtil.buildCustomException;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.util.DataValidaton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.amigo.programador.msclient.entity.Client;
import com.amigo.programador.msclient.repository.ClientRepository;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
			 .doOnError(ex -> buildCustomException(HttpStatus.INTERNAL_SERVER_ERROR, ex));
	 }
	 
	 public Mono<ApiResponse> createClient(Client client) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
		 return isUniqueValue(client.getName(), "findByName", clientRepository)
			 .then(isUniqueValue(client.getName(), "findByName", clientRepository))
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

	 private ApiResponse throwNullPointerException() {
		 throw new NullPointerException();
	 }
}
