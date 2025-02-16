package com.amigo.programador.msclient.service;

import static com.amigo.programador.library.util.LibraryUtil.buildApiResponse;

import com.amigo.programador.library.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.amigo.programador.msclient.entity.Client;
import com.amigo.programador.msclient.repository.ClientRepository;
import reactor.core.publisher.Mono;

@Service
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
						 .map(c -> buildApiResponse("Client has been created", c));
	 }
	 
	 public Mono<ApiResponse> deleteClient(Long id) {
		 return clientRepository.findById(id)
						 .flatMap(client -> clientRepository.deleteById(id)
										 .then(Mono.just(buildApiResponse("Client has been deleted", client))))
						 .switchIfEmpty(Mono.just(buildApiResponse("Client can't be deleted because doesn't exists", null)));
	 }
}
