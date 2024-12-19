package com.amigo.programador.mstransaction.entity;

import java.time.LocalDateTime;


import com.amigo.programador.library.annotation.UniqueField;
import com.amigo.programador.library.model.DebitCard;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class Transaction {
	
	private String id;

	@NotNull
	@NotBlank
	@UniqueField(fieldName = "transactionCode", entityClass = Transaction.class, message = "Transaction code must be unique")
	private String transactionCode;

	@NotNull
	@Positive(message = "Transaction amount must by greater than or equal to zero")
	private Double transactionAmount;

	@NotNull
	private DebitCard origin;

	@NotNull
	private DebitCard destination;

	private LocalDateTime transactionDate;

	@NotNull
	private TransactionType transactionType;
	
	public static enum TransactionType {
		DEPOSIT, CASH_OUT, TRANSFER
	}
}
