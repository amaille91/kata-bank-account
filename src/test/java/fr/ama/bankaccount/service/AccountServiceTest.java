package fr.ama.bankaccount.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.ama.bankaccount.model.Account;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

	@InjectMocks
	private AccountService service;

	@Mock
	private AccountRepository accountRepository;

	@Captor
	private ArgumentCaptor<Account> accountCaptor;

	@Test
	void creating_a_new_account_should_delegate_to_repository() throws Exception {
		when(accountRepository.createAccount()).thenReturn(new Account("id", 0));

		service.createNewAccount();

		verify(accountRepository, times(1)).createAccount();
	}

	@Nested
	class Deposits {

		@Test
		void depositting_in_an_account_should_retrieve_the_account_and_override_with_an_account_with_the_same_id_and_balance_increased_with_the_deposit_amount()
				throws Exception {
			when(accountRepository.retrieveAccount("id")).thenReturn(new Account("id", 525));

			Account returnedAccount = service.deposit("id", 75);

			verify(accountRepository, times(1)).overrideAccount(accountCaptor.capture());

			Account overridenAccount = accountCaptor.getValue();
			assertThat(overridenAccount.getId()).isEqualTo("id");
			assertThat(overridenAccount.getBalance()).isEqualTo(600);
			assertThat(returnedAccount).usingRecursiveComparison().isEqualTo(overridenAccount);
		}

		@Test
		void depositting_in_an_NON_existing_account_should_throw_an_UnknownAccountException()
				throws Exception {
			doThrow(new UnknownAccountException("unknown id")).when(accountRepository).retrieveAccount("id");

			assertThrows(UnknownAccountException.class, () -> service.deposit("id", 75));

			verify(accountRepository, never()).overrideAccount(Mockito.nullable(Account.class));
		}

		@Test
		void should_throw_IllegalStateException_if_account_is_not_found_during_overriding()
				throws Exception {
			when(accountRepository.retrieveAccount("id")).thenReturn(new Account("id", 525));
			doThrow(new UnknownAccountException("id")).when(accountRepository)
					.overrideAccount(Mockito.argThat(new ArgumentMatcher<Account>() {
						@Override
						public boolean matches(Account account) {
							return account.getId().equals("id");
						}
					}));

			assertThrows(IllegalStateException.class, () -> service.deposit("id", 75));
		}
	}

}
