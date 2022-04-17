package fr.ama.bankaccount.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import fr.ama.bankaccount.model.Account;
import fr.ama.bankaccount.model.History;
import fr.ama.bankaccount.model.Operation;
import fr.ama.bankaccount.model.Operation.OperationType;

@Service
public class HistoryRepository {

	private Map<String, List<Operation>> histories = new HashMap<>();

	public History getHistory(String accountId) {
		return new History(histories.getOrDefault(accountId, new LinkedList<>()));
	}

	public void newDepositOnAccount(Account newAccount, int amount) {
		List<Operation> oldHistory = histories.getOrDefault(newAccount.getId(), new LinkedList<>());

		oldHistory.add(new Operation(OperationType.DEPOSIT, amount, newAccount.getBalance()));
		histories.put(newAccount.getId(), oldHistory);
	}

	public void newWithdrawalOnAccount(Account newAccount, int amount) {
		List<Operation> oldHistory = histories.getOrDefault(newAccount.getId(), new LinkedList<>());

		oldHistory.add(new Operation(OperationType.WITHDRAWAL, amount, newAccount.getBalance()));
		histories.put(newAccount.getId(), oldHistory);
	}

}
