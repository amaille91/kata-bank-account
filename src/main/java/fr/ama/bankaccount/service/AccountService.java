package fr.ama.bankaccount.service;

import org.springframework.stereotype.Service;

import fr.ama.bankaccount.model.Account;

@Service
public class AccountService {

	public Account deposit(String accountId, Integer amount) {
		return new Account(accountId, amount);
	}

}
