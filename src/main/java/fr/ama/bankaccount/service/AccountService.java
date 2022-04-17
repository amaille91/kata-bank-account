package fr.ama.bankaccount.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.ama.bankaccount.model.Account;

@Service
public class AccountService {

	private AccountRepository accountRepository;

	@Autowired
	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public Account createNewAccount() {
		return accountRepository.createAccount();
	}

	public Account deposit(String accountId, Integer amount) throws UnknownAccountException {
		Account oldAccount = accountRepository.retrieveAccount(accountId);
		Account newAccount = new Account(oldAccount.getId(), oldAccount.getBalance() + amount);

		try {
			accountRepository.overrideAccount(newAccount);
		} catch (UnknownAccountException e) {
			throw new IllegalStateException(
					"The account " + newAccount.getId() + " has disappeared between getting it and overriding it");
		}
		return newAccount;
	}

	public Account withdraw(String accountId, Integer amount)
			throws UnknownAccountException, WithdrawalTooLargeException {
		Account oldAccount = accountRepository.retrieveAccount(accountId);
		int newBalance = oldAccount.getBalance() - amount;

		if (newBalance < 0) {
			throw new WithdrawalTooLargeException(oldAccount);
		}
		Account newAccount = new Account(oldAccount.getId(), newBalance);

		try {
			accountRepository.overrideAccount(newAccount);
		} catch (UnknownAccountException e) {
			throw new IllegalStateException(
					"The account " + newAccount.getId() + " has disappeared between getting it and overriding it");
		}
		return newAccount;
	}

}
