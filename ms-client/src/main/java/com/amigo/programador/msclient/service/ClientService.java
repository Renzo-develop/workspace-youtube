package com.amigo.programador.msclient.service;

import static com.amigo.programador.library.util.LibraryUtil.buildApiResponse;

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

@Service
@Slf4j
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;
	
	 public Mono<ApiResponse> findAll() {

		 return clientRepository.findAll()
						 .collectList()
						 .map(list -> buildApiResponse("Listing clients", list));
	 }
	 
	 public Mono<ApiResponse> findById(Long id) {
		 return clientRepository.findById(id)
						 .map(client -> buildApiResponse("Client was found", client))
						 .switchIfEmpty(Mono.just(buildApiResponse("Client doesn't exists", null)));
	 }
	 
	 public Mono<ApiResponse> createClient(Client client) {
		 return clientRepository.insert(client)
						 .map(c -> buildApiResponse("Client has been created", c))
						 .doOnError(ex -> log.error("Error creating client - {}", ex.getMessage()))
						 .onErrorResume(ex -> {
							 var cer = CustomExceptionResponse.builder()
											 .error(ex.getClass().getCanonicalName())
											 .message(ex.getMessage())
											 .build();

							 var ce = CustomException.builder()
											 .httpStatus(HttpStatus.CONFLICT)
											 .response(cer)
											 .build();

							 return Mono.error(ce);
							});
	 }
	 
	 public Mono<ApiResponse> deleteClient(Long id) {
		 return clientRepository.findById(id)
						 .flatMap(client -> clientRepository.deleteById(id)
										 .then(Mono.just(buildApiResponse("Client has been deleted", client))))
						 .switchIfEmpty(Mono.just(buildApiResponse("Client can't be deleted because doesn't exists", null)));
	 }
}
