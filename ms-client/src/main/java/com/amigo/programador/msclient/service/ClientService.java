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

import static com.amigo.programador.library.util.LibraryUtil.getRootCause;

@Service
@Slf4j
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;
	
	 public Mono<ApiResponse> findAll() {

		 return clientRepository.findAll()
						 .collectList()
						 .map(list -> ApiResponse.builder()
												 .message("Client List")
												 .response(list)
												 .build())
						 .switchIfEmpty(Mono.just(ApiResponse.builder()
										 .message("No clients created yet")
										 .build()));
	 }
	 
	 public Mono<ApiResponse> findById(Long id) {

		 return clientRepository.findById(id)
						 .map(client -> ApiResponse.builder()
										 .message("Client was found")
										 .response(client)
										 .build())
						 .switchIfEmpty(Mono.just(ApiResponse.builder()
										 .message("Client doesn't found")
										 .build()));
	 }
	 
	 public Mono<ApiResponse> createClient(Client client) {

		 return clientRepository.insert(client)
						 .map(Client -> ApiResponse.builder()
										 .message("Client created")
										 .response(client)
										 .build())
						 .doOnError(error -> log.error("Error creating client - {}", error.getMessage()))
						 .onErrorResume(error -> Mono.error(
										 CustomException.builder()
														 .status(HttpStatus.CONFLICT)
														 .response(CustomExceptionResponse.builder()
																		 .error(getRootCause(error))
																		 .message(error.getMessage())
																		 .build())
														 .build()));
	}

	 public Mono<ApiResponse> deleteClient(Long id) {
		 return clientRepository.findById(id)
						 .flatMap(client -> clientRepository.deleteById(id)
													 .then(Mono.just(ApiResponse.builder()
																					 .message("Client was deleted")
																					 .response(client)
																					 .build())))
						 .switchIfEmpty(Mono.just(ApiResponse.builder()
										 .message("Client doesn't exists")
										 .build()));
	 }

}
