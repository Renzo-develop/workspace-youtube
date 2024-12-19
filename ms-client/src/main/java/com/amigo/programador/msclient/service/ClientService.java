package com.amigo.programador.msclient.service;

import com.amigo.programador.library.model.ApiResponse;
import com.amigo.programador.library.model.CustomException;
import com.amigo.programador.library.model.CustomExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.amigo.programador.msclient.entity.Client;
import com.amigo.programador.msclient.repository.ClientRepository;
import reactor.core.publisher.Mono;

import static com.amigo.programador.library.util.LibraryUtil.buildApiResponse;
import static com.amigo.programador.library.util.LibraryUtil.buildCustomException;
import static com.amigo.programador.library.util.LibraryUtil.getRootCause;

@Service
@Slf4j
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;
	
	 public Mono<ApiResponse> findAll() {

		 return clientRepository.findAll()
						 .collectList()
						 .map(list -> buildApiResponse("Client List", list))
						 .switchIfEmpty(Mono.just(buildApiResponse("No clients created yet", null)));
	 }
	 
	 public Mono<ApiResponse> findById(Long id) {

		 return clientRepository.findById(id)
						 .map(client -> buildApiResponse("Client was found", client))
						 .switchIfEmpty(Mono.just(buildApiResponse("Client doesn't found", null)));
	 }
	 
	 public Mono<ApiResponse> createClient(Client client) {

		 return clientRepository.insert(client)
						 .map(clientCreated -> buildApiResponse("Client created", clientCreated))
						 .doOnError(error -> log.error("Error creating client - {}", error))
						 .onErrorResume(error -> Mono.error(buildCustomException(HttpStatus.CONFLICT, error)));
	}

	 public Mono<ApiResponse> deleteClient(Long id) {
		 return clientRepository.findById(id)
						 .flatMap(client -> clientRepository.deleteById(id)
													 .then(Mono.just(buildApiResponse("Client was deleted", client))))
						 .switchIfEmpty(Mono.just(buildApiResponse("Client doesn't exists", null)));
	 }

}
