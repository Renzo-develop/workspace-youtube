package com.amigo.programador.msclient.service;

import com.amigo.programador.library.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amigo.programador.msclient.entity.Client;
import com.amigo.programador.msclient.repository.ClientRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;
	
	 public Mono<ApiResponse> findAll() {

		 return clientRepository.findAll()
						 .collectList()
						 .map(list -> ApiResponse.builder()
										 .message("Listing clients")
										 .response(list)
										 .build());
	 }
	 
	 public Mono<ApiResponse> findById(Long id) {
		 return clientRepository.findById(id)
						 .map(client -> ApiResponse.builder()
										 .message("Client was found")
										 .response(client)
										 .build())
						 .switchIfEmpty(Mono.just(ApiResponse.builder()
										 .message("Client doesn't exists")
										 .build()));
	 }
	 
	 public Mono<ApiResponse> createClient(Client client) {
		 return clientRepository.insert(client)
						 .map(c -> ApiResponse.builder()
										 .message("Client has been created")
										 .response(c)
										 .build());
	 }
	 
	 public Mono<ApiResponse> deleteClient(Long id) {
		 return clientRepository.findById(id)
						 .flatMap(client -> clientRepository.deleteById(id)
										 .then(Mono.just(ApiResponse.builder()
														 .message("Client has been deleted")
														 .response(client)
														 .build())))
						 .switchIfEmpty(Mono.just(ApiResponse.builder()
										 .message("Client can't be deleted because doesn't exists")
										 .build()));
	 }
}
