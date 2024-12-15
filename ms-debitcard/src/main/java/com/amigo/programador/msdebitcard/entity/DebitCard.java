package com.amigo.programador.msdebitcard.entity;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.amigo.programador.library.model.Client;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class DebitCard {

	@NotNull
	private Long id;
	
	@NotNull
	private String cardNumber;
	
	private Double balance;
	
	@NotNull
	@DateTimeFormat(iso=ISO.DATE)
	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDate expirationDate;
	
	@NotNull
	private Client client;
}
