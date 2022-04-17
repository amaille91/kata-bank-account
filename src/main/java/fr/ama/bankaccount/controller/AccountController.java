package fr.ama.bankaccount.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.ama.bankaccount.model.Account;
import fr.ama.bankaccount.service.AccountService;
import fr.ama.bankaccount.service.UnknownAccountException;

@RestController
@RequestMapping("/account")
public class AccountController {

	private AccountService accountService;

	public AccountController(AccountService noteService) {
		this.accountService = noteService;
	}

	@PutMapping
	public Account createAccount() {
		return accountService.createNewAccount();
	}

	@PutMapping("/{accountId}/deposit/{amount}")
	public @ResponseBody ResponseEntity<Account> depositInAccount(@PathVariable("accountId") String accountId,
			@PathVariable("amount") Integer amount) {
		try {
			return ResponseEntity.ok(accountService.deposit(accountId, amount));
		} catch (UnknownAccountException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
