package fr.ama.bankaccount.model;

public class Account {

	private String id;
	private int balance; // in cents

	private Account(String id, int balance) {
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
