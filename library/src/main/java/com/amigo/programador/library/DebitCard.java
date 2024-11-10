package com.amigo.programador.library;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class DebitCard {
	private Long id;
	
	private String cardNumber;
	
	private Double balance;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDate expirationDate;
	
	private Client client;
}
