package fr.ama.bankaccount.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.ama.bankaccount.model.Account;
import fr.ama.bankaccount.model.History;

@Service
public class AccountService {

	private AccountRepository accountRepository;
	private HistoryRepository historyRepository;

	@Autowired
	public AccountService(AccountRepository accountRepository, HistoryRepository historyRepository) {
		this.accountRepository = accountRepository;
		this.historyRepository = historyRepository;
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
		historyRepository.newDepositOnAccount(newAccount, amount);
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
		historyRepository.newWithdrawalOnAccount(newAccount, amount);
		return newAccount;
	}

	public History getHistory(String accountId) throws UnknownAccountException {
		accountRepository.retrieveAccount(accountId);
		return historyRepository.getHistory(accountId);
	}

}
