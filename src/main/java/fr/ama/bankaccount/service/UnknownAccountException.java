package fr.ama.bankaccount.service;

public class UnknownAccountException extends Exception {

	private static final long serialVersionUID = -2659589024770271132L;
	private String accountId;

	public UnknownAccountException(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

}
