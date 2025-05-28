package com.amigo.programador.mstransaction.entity;

import java.time.LocalDateTime;


import com.amigo.programador.library.model.DebitCard;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class Transaction {

	@NotNull
	private Long id;

	@NotNull
	private String transactionCode;

	@NotNull
	@Positive
	private Double transactionAmount;
	
	private DebitCard origin;
	
	private DebitCard destination;

	private LocalDateTime transactionDate;

	@NotNull
	private TransactionType transactionType;
	
	public static enum TransactionType {
		DEPOSIT, CASH_OUT, TRANSFER
	}
}
