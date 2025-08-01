package amigo.programador.mstransaction.entity;

import java.time.LocalDateTime;


import amigo.programador.library.model.DebitCard;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class Transaction {

	@NotNull
	private Long id;

	@NotNull
	@Pattern(regexp = "^[0-9]{4}$", message = "Invalid transaction code, example: 4891")
	private String transactionCode;

	@NotNull
	private Double transactionAmount;

	@NotNull
	private DebitCard origin;
	
	private DebitCard destination;
	
	private LocalDateTime transactionDate;

	@NotNull
	private TransactionType transactionType;
	
	public static enum TransactionType {
		DEPOSIT, CASH_OUT, TRANSFER
	}
}
