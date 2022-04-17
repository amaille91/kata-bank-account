package fr.ama.bankaccount.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import fr.ama.bankaccount.model.Account;

@Service
public class AccountRepository {

	private List<Account> accounts = new LinkedList<>();

	public Account createAccount() {
		Account newAccount = new Account(UUID.randomUUID().toString(), 0);
		accounts.add(newAccount);
		return newAccount;
	}

	public Account retrieveAccount(String accountId) throws UnknownAccountException {
		Optional<Account> account = accounts.stream().filter(acc -> acc.getId().equals(accountId)).findFirst();
		return account.orElseThrow(() -> new UnknownAccountException(accountId));
	}

	public void overrideAccount(Account newAccount) throws UnknownAccountException {
		if (!accounts.removeIf(account -> account.getId().equals(newAccount.getId()))) {
			throw new UnknownAccountException(newAccount.getId());
		}
		accounts.add(newAccount);
	}

}
