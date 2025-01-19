package com.amigo.programador.mstransaction.entity;

import java.time.LocalDateTime;

import com.amigo.programador.library.DebitCard;

import lombok.Data;

@Data
public class Transaction {
	
	private String id;
	
	private String transactionCode;
	
	private Double transactionAmount;
	
	private DebitCard origin;
	
	private DebitCard destination;
	
	private LocalDateTime transactionDate;
	
	private TransactionType transactionType;
	
	public static enum TransactionType {
		DEPOSIT, CASH_OUT, TRANSFER
	}
}
