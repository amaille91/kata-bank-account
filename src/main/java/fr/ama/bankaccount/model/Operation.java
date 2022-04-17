package fr.ama.bankaccount.model;

import java.util.Date;

public class Operation {

	private OperationType type;
	private Date date;
	private int amount;
	private int accountBalance;

	public Operation(OperationType type, int amount, int accountBalance) {
		this.type = type;
		// TODO introduce indirection for testing
		this.date = new Date();
		this.amount = amount;
		this.accountBalance = accountBalance;
	}

	public OperationType getType() {
		return type;
	}

	public Date getDate() {
		return date;
	}

	public int getAmount() {
		return amount;
	}

	public int getAccountBalance() {
		return accountBalance;
	}

	public static enum OperationType {
		DEPOSIT, WITHDRAWAL;
	}
}
