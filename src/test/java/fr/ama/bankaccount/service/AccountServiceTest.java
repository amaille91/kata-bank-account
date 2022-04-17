package fr.ama.bankaccount.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
import fr.ama.bankaccount.model.History;
import fr.ama.bankaccount.model.Operation;
import fr.ama.bankaccount.model.Operation.OperationType;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

	@InjectMocks
	private AccountService service;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private HistoryRepository historyRepository;

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
		void depositting_in_an_account_should_notify_history_via_historyRepository()
				throws Exception {
			when(accountRepository.retrieveAccount("id")).thenReturn(new Account("id", 525));

			service.deposit("id", 75);

			verify(historyRepository, times(1)).newDepositOnAccount(Mockito.argThat(new ArgumentMatcher<Account>() {

				@Override
				public boolean matches(Account account) {
					return account.getId().equals("id") && account.getBalance() == 600;
				}
			}), Mockito.eq(75));
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

	@Nested
	class Withdrawals {

		@Test
		void withdrawing_from_an_account_should_retrieve_the_account_and_override_with_an_account_with_the_same_id_and_balance_decreased_with_the_withdrawal_amount()
				throws Exception {
			when(accountRepository.retrieveAccount("id")).thenReturn(new Account("id", 525));

			Account returnedAccount = service.withdraw("id", 25);

			verify(accountRepository, times(1)).overrideAccount(accountCaptor.capture());

			Account overridenAccount = accountCaptor.getValue();
			assertThat(overridenAccount.getId()).isEqualTo("id");
			assertThat(overridenAccount.getBalance()).isEqualTo(500);
			assertThat(returnedAccount).usingRecursiveComparison().isEqualTo(overridenAccount);
		}

		@Test
		void withdrawing_from_an_account_should_update_the_history_via_the_repository()
				throws Exception {
			when(accountRepository.retrieveAccount("id")).thenReturn(new Account("id", 525));

			service.withdraw("id", 25);

			verify(historyRepository).newWithdrawalOnAccount(Mockito.argThat(new ArgumentMatcher<Account>() {

				@Override
				public boolean matches(Account account) {
					return account.getId().equals("id") && account.getBalance() == 500;
				}
			}), Mockito.eq(25));

		}

		@Test
		void withdrawing_the_whole_account_should_leave_the_account_empty()
				throws Exception {
			when(accountRepository.retrieveAccount("id")).thenReturn(new Account("id", 525));

			Account returnedAccount = service.withdraw("id", 525);

			verify(accountRepository, times(1)).overrideAccount(accountCaptor.capture());

			Account overridenAccount = accountCaptor.getValue();
			assertThat(overridenAccount.getId()).isEqualTo("id");
			assertThat(overridenAccount.getBalance()).isEqualTo(0);
			assertThat(returnedAccount).usingRecursiveComparison().isEqualTo(overridenAccount);
		}

		@Test
		void withdrawing_more_than_the_account_balance_throw_a_WithdrawalTooLargeException_and_not_override_the_account()
				throws Exception {
			when(accountRepository.retrieveAccount("id")).thenReturn(new Account("id", 525));

			assertThrows(WithdrawalTooLargeException.class, () -> service.withdraw("id", 526));

			verify(accountRepository, never()).overrideAccount(Mockito.nullable(Account.class));
		}

		@Test
		void withdrawing_in_an_NON_existing_account_should_throw_an_UnknownAccountException()
				throws Exception {
			doThrow(new UnknownAccountException("unknown id")).when(accountRepository).retrieveAccount("id");

			assertThrows(UnknownAccountException.class, () -> service.withdraw("id", 75));

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

			assertThrows(IllegalStateException.class, () -> service.withdraw("id", 75));
		}
	}

	@Nested
	class Histories {

		@Test
		void requesting_history_on_an_NON_existing_account_should_throw_an_UnknownAccountException() throws Exception {
			doThrow(new UnknownAccountException("id")).when(accountRepository).retrieveAccount("id");

			assertThrows(UnknownAccountException.class, () -> service.getHistory("id"));
		}

		@Test
		void requesting_history_on_an_existing_account_should_delegate_to_historyRepository() throws Exception {
			when(accountRepository.retrieveAccount("id")).thenReturn(new Account("id", 40));
			when(historyRepository.getHistory("id")).thenReturn(new History(List.of(
					new Operation(OperationType.DEPOSIT, 20, 20),
					new Operation(OperationType.DEPOSIT, 30, 50),
					new Operation(OperationType.WITHDRAWAL, 10, 40))));

			History retrievedHistory = service.getHistory("id");

			List<Operation> operations = retrievedHistory.getOperations();
			assertThat(operations.get(0))
					.extracting(Operation::getType, Operation::getAmount, Operation::getAccountBalance)
					.containsExactly(OperationType.DEPOSIT, 20, 20);
			assertThat(operations.get(1))
					.extracting(Operation::getType, Operation::getAmount, Operation::getAccountBalance)
					.containsExactly(OperationType.DEPOSIT, 30, 50);
			assertThat(operations.get(2))
					.extracting(Operation::getType, Operation::getAmount, Operation::getAccountBalance)
					.containsExactly(OperationType.WITHDRAWAL, 10, 40);
		}
	}

}
