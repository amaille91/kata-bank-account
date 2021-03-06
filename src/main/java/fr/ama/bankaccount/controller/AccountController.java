package fr.ama.bankaccount.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.ama.bankaccount.model.Account;
import fr.ama.bankaccount.model.History;
import fr.ama.bankaccount.service.AccountService;
import fr.ama.bankaccount.service.UnknownAccountException;
import fr.ama.bankaccount.service.WithdrawalTooLargeException;

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
		if (amount <= 0) {
			return ResponseEntity.badRequest().build();
		}

		try {
			return ResponseEntity.ok(accountService.deposit(accountId, amount));
		} catch (UnknownAccountException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{accountId}/withdrawal/{amount}")
	public @ResponseBody ResponseEntity<Account> withdrawFromAccount(@PathVariable("accountId") String accountId,
			@PathVariable("amount") Integer amount) {
		if (amount <= 0) {
			return ResponseEntity.badRequest().build();
		}

		try {
			return ResponseEntity.ok(accountService.withdraw(accountId, amount));
		} catch (UnknownAccountException e) {
			return ResponseEntity.notFound().build();
		} catch (WithdrawalTooLargeException e) {
			return ResponseEntity.badRequest().body(e.getOldAccount());
		}
	}

	@GetMapping("/{accountId}/history")
	public ResponseEntity<History> getHistory(@PathVariable("accountId") String accountId) {
		try {
			return ResponseEntity.ok(accountService.getHistory(accountId));
		} catch (UnknownAccountException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
