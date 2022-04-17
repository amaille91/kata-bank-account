package fr.ama.bankaccount.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import fr.ama.bankaccount.model.Account;

@Service
public class AccountService {

	public Account createNewAccount() {
		return new Account(UUID.randomUUID().toString(), 0);
	}

	public Account deposit(String accountId, Integer amount) {
		return new Account(accountId, amount);
	}

}
