package com.amigo.programador.msclient.service;

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
	
	 public Flux<Client> findAll() {
		 return clientRepository.findAll();
	 }
	 
	 public Mono<Client> findById(Long id) {
		 return clientRepository.findById(id);
	 }
	 
	 public Mono<Client> createClient(Client client) {
		 return clientRepository.insert(client);
	 }
	 
	 public Mono<Void> deleteClient(Long id) {
		 return clientRepository.deleteById(id);
	 }
}
