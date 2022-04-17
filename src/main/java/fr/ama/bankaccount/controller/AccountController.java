package fr.ama.bankaccount.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ama.bankaccount.model.Account;
import fr.ama.bankaccount.service.AccountService;

@RestController
@RequestMapping("/account")
public class AccountController {

	private AccountService accountService;

	public AccountController(AccountService noteService) {
		this.accountService = noteService;
	}

	@PutMapping
	public Account createAccount() {
		return new Account(UUID.randomUUID().toString(), 0);
	}

	@PutMapping("/{accountId}/deposit/{amount}")
	public Account depositInAccount(@PathVariable("accountId") String accountId,
			@PathVariable("amount") Integer amount) {
		return accountService.deposit(accountId, amount);
	}

}
