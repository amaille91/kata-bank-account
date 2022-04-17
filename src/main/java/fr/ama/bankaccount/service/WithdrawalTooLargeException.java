package fr.ama.bankaccount.service;

import fr.ama.bankaccount.model.Account;

public class WithdrawalTooLargeException extends Exception {
	private static final long serialVersionUID = 3787847396235030410L;

	private Account oldAccount;

	public WithdrawalTooLargeException(Account oldAccount) {
		this.oldAccount = oldAccount;
	}

	public Account getOldAccount() {
		return oldAccount;
	}

}
