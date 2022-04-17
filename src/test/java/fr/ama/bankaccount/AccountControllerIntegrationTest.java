package fr.ama.bankaccount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ama.bankaccount.model.Account;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

	private MockMvc mockMvc;
	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	public AccountControllerIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper) {
		this.mockMvc = mockMvc;
		this.objectMapper = objectMapper;
	}

	@Test
	void putting_an_account_should_gives_us_an_account_with_a_balance_of_0() throws Exception {
		String putResponseBody = mockMvc.perform(put("/account"))
				.andExpect(status().is2xxSuccessful())
				.andReturn()
				.getResponse().getContentAsString();
		Account account = objectMapper.readValue(putResponseBody, Account.class);

		assertThat(account.getId()).isNotNull();
		assertThat(account.getBalance()).isEqualTo(0);
	}

	@Nested
	class Deposits {
		// TODO Test for int overflow or use BigInteger
		@Test
		void putting_a_deposit_on_an_existing_account_should_give_back_the_new_account_with_the_balance_increased()
				throws Exception {
			String createAccountResponse = mockMvc.perform(MockMvcRequestBuilders.put("/account"))
					.andExpect(status().is2xxSuccessful())
					.andReturn()
					.getResponse().getContentAsString();
			Account initialAccount = objectMapper.readValue(createAccountResponse, Account.class);

			String depositResponse = mockMvc.perform(put("/account/" + initialAccount.getId() + "/deposit/1000"))
					.andExpect(status().is2xxSuccessful())
					.andReturn()
					.getResponse().getContentAsString();
			Account newAccount = objectMapper.readValue(depositResponse, Account.class);

			assertThat(newAccount.getId()).isEqualTo(initialAccount.getId());
			assertThat(newAccount.getBalance()).isEqualTo(1000);
		}

		@Test
		void depositting_multiple_times_on_an_existing_account_should_give_back_the_new_account_with_the_balance_increased_by_the_sum_of_the_deposits()
				throws Exception {
			// TODO : use Property-based testing to generate random amounts (jqwik ?)
			List<Integer> successiveDepositsAmount = List.of(1000, 2000, 35, 76, 236754);
			String createAccountResponse = mockMvc.perform(MockMvcRequestBuilders.put("/account"))
					.andExpect(status().is2xxSuccessful())
					.andReturn()
					.getResponse().getContentAsString();
			Account currentAccount = objectMapper.readValue(createAccountResponse, Account.class);
			String initialId = currentAccount.getId();

			for (Integer amount : successiveDepositsAmount) {
				currentAccount = depositOnAccount(currentAccount, amount);
			}

			assertThat(currentAccount.getId()).isEqualTo(initialId);
			assertThat(currentAccount.getBalance())
					.isEqualTo(successiveDepositsAmount.stream().reduce(Math::addExact).orElse(-1));
		}

		@Test
		void depositting_on_a_NON_existing_account_should_give_back_404()
				throws Exception {
			mockMvc.perform(put("/account/non-existing-account/deposit/10000"))
					.andExpect(status().isNotFound())
					.andReturn();
		}

		@Test
		void depositting_negative_amount_should_give_back_badRequest()
				throws Exception {
			String createAccountResponse = mockMvc.perform(MockMvcRequestBuilders.put("/account"))
					.andExpect(status().is2xxSuccessful())
					.andReturn()
					.getResponse().getContentAsString();
			Account currentAccount = objectMapper.readValue(createAccountResponse, Account.class);

			mockMvc.perform(put("/account/" + currentAccount.getId() + "/deposit/-1200"))
					.andExpect(status().isBadRequest())
					.andReturn();
		}

		private Account depositOnAccount(Account account, Integer amount)
				throws UnsupportedEncodingException, Exception {
			String response = mockMvc.perform(put("/account/" + account.getId() + "/deposit/" + amount))
					.andExpect(status().is2xxSuccessful())
					.andReturn()
					.getResponse().getContentAsString();

			return objectMapper.readValue(response, Account.class);
		}
	}

}
