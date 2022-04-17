package fr.ama.bankaccount.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import fr.ama.bankaccount.model.Account;

public class AccountRepositoryTest {

	@Test
	void creating_an_account_should_give_back_an_account_with_balance_0_and_we_should_be_able_to_retrieve_the_account()
			throws Exception {
		AccountRepository repository = new AccountRepository();

		Account createdAccount = repository.createAccount();
		Account retrievedAccount = repository.retrieveAccount(createdAccount.getId());

		assertThat(createdAccount.getBalance()).isEqualTo(0);
		assertThat(createdAccount).usingRecursiveComparison().isEqualTo(retrievedAccount);
	}

	@Test
	void retrieving_an_unknown_account_should_throw_an_UnknownAccountException()
			throws Exception {
		AccountRepository repository = new AccountRepository();

		assertThrows(UnknownAccountException.class, () -> repository.retrieveAccount("id"));
	}

	@Test
	void overriding_an_unknown_account_should_throw_an_UnknownAccountException()
			throws Exception {
		AccountRepository repository = new AccountRepository();

		assertThrows(UnknownAccountException.class, () -> repository.overrideAccount(new Account("id", 2000)));
	}

	@Test
	void overriding_an_account_should_make_retrieving_the_same_account_with_the_balance_of_the_overriding_value()
			throws Exception {
		AccountRepository repository = new AccountRepository();
		Account createdAccount = repository.createAccount();
		repository.overrideAccount(new Account(createdAccount.getId(), 3000));

		Account retrievedAccount = repository.retrieveAccount(createdAccount.getId());

		assertThat(retrievedAccount.getBalance()).isEqualTo(3000);
	}

}
