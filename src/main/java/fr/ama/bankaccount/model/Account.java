package fr.ama.bankaccount.model;

import java.beans.ConstructorProperties;

public class Account {

	private String id;
	private int balance; // in cents

	@ConstructorProperties({ "id", "balance" })
	public Account(String id, int balance) {
		this.id = id;
		this.balance = balance;
	}

	public String getId() {
		return id;
	}

	public int getBalance() {
		return balance;
	}

}
