package fr.ama.bankaccount.model;

import java.util.List;

public class History {

	public List<Operation> operations;

	public History(List<Operation> operations) {
		this.operations = operations;
	}

	public List<Operation> getOperations() {
		return operations;
	}

}
