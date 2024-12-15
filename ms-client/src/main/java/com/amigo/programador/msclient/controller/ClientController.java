package com.amigo.programador.msclient.controller;

import javax.validation.Valid;

import com.amigo.programador.library.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amigo.programador.msclient.entity.Client;
import com.amigo.programador.msclient.service.ClientService;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {

	@Autowired
	private ClientService clientService;
	
	@GetMapping("/findall")
	public Mono<ResponseEntity<ApiResponse>> findAll() {
		return clientService.findAll()
						.map(response -> ResponseEntity.ok().body(response));
	}
	
	@GetMapping("/findbyid/{id}")
	public Mono<ResponseEntity<ApiResponse>> findById(@PathVariable Long id) {
		return clientService.findById(id)
						.map(response -> ResponseEntity.ok(response));
	}
	
	@PostMapping("/create")
	public Mono<ResponseEntity<ApiResponse>> createClient(@Valid @RequestBody Client client) {
		return clientService.createClient(client)
						.map(response -> ResponseEntity.ok().body(response));
	}
	
	@DeleteMapping("/delete/{id}")
	public Mono<ResponseEntity<ApiResponse>> deleteClient(@PathVariable Long id) {
		return clientService.deleteClient(id)
						.map(response -> ResponseEntity.ok().body(response));
	}

}
