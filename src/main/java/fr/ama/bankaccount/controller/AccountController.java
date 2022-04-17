package fr.ama.bankaccount.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ama.bankaccount.model.Account;

@RestController
@RequestMapping("/account")
public class AccountController {

	@PutMapping
	public Account createAccount() {
		return new Account(UUID.randomUUID().toString(), 0);
	}

}
