// CHECKSTYLE:OFF
/**
 * Source code generated by Fluent Builders Generator
 * Do not modify this file
 * See generator home page at: http://code.google.com/p/fluent-builders-generator-eclipse-plugin/
 */

package com.github.taxbeans.model.builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.github.taxbeans.model.Account;
import com.github.taxbeans.model.AccountType;
import com.github.taxbeans.model.Transaction;
import com.github.taxbeans.model.assertions.BalanceAssertion;

public class AccountBuilder extends AccountBuilderBase<AccountBuilder> {
	public static AccountBuilder account() {
		return new AccountBuilder();
	}

	public AccountBuilder() {
		super(new Account());
	}

	public Account build() {
		return getInstance();
	}
}

class AccountBuilderBase<GeneratorT extends AccountBuilderBase<GeneratorT>> {
	private Account instance;

	protected AccountBuilderBase(Account aInstance) {
		instance = aInstance;
	}

	protected Account getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withAccountType(AccountType aValue) {
		instance.setAccountType(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withName(String aValue) {
		instance.setName(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withBalanceAssertions(List<BalanceAssertion> aValue) {
		instance.setBalanceAssertions(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withAddedBalanceAssertion(BalanceAssertion aValue) {
		if (instance.getBalanceAssertions() == null) {
			instance.setBalanceAssertions(new ArrayList<BalanceAssertion>());
		}

		((ArrayList<BalanceAssertion>) instance.getBalanceAssertions())
		.add(aValue);

		return (GeneratorT) this;
	}

	public AddedBalanceAssertionBalanceAssertionBuilder withAddedBalanceAssertion() {
		BalanceAssertion obj = new BalanceAssertion();

		withAddedBalanceAssertion(obj);

		return new AddedBalanceAssertionBalanceAssertionBuilder(obj);
	}

	public class AddedBalanceAssertionBalanceAssertionBuilder
	extends
	BalanceAssertionBuilderBase<AddedBalanceAssertionBalanceAssertionBuilder> {
		public AddedBalanceAssertionBalanceAssertionBuilder(
				BalanceAssertion aInstance) {
			super(aInstance);
		}

		@SuppressWarnings("unchecked")
		public GeneratorT endBalanceAssertion() {
			return (GeneratorT) AccountBuilderBase.this;
		}
	}

	public class AddedCreditTransactionTransactionBuilder extends
	TransactionBuilderBase<AddedCreditTransactionTransactionBuilder> {
		public AddedCreditTransactionTransactionBuilder(Transaction aInstance) {
			super(aInstance);
		}

		@SuppressWarnings("unchecked")
		public GeneratorT endCreditTransaction() {
			return (GeneratorT) AccountBuilderBase.this;
		}
	}

	public class AddedDebitTransactionTransactionBuilder extends
	TransactionBuilderBase<AddedDebitTransactionTransactionBuilder> {
		public AddedDebitTransactionTransactionBuilder(Transaction aInstance) {
			super(aInstance);
		}

		@SuppressWarnings("unchecked")
		public GeneratorT endDebitTransaction() {
			return (GeneratorT) AccountBuilderBase.this;
		}
	}

	public static class TransactionBuilderBase<GeneratorT extends TransactionBuilderBase<GeneratorT>> {
		private Transaction instance;

		protected TransactionBuilderBase(Transaction aInstance) {
			instance = aInstance;
		}

		protected Transaction getInstance() {
			return instance;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withDate(ZonedDateTime aValue) {
			instance.setDate(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withDescription(String aValue) {
			instance.setDescription(aValue);

			return (GeneratorT) this;
		}

		public class CreditAccountAccountBuilder extends
		AccountBuilderBase<CreditAccountAccountBuilder> {
			public CreditAccountAccountBuilder(Account aInstance) {
				super(aInstance);
			}

			@SuppressWarnings("unchecked")
			public GeneratorT endCreditAccount() {
				return (GeneratorT) TransactionBuilderBase.this;
			}
		}

		public class DebitAccountAccountBuilder extends
		AccountBuilderBase<DebitAccountAccountBuilder> {
			public DebitAccountAccountBuilder(Account aInstance) {
				super(aInstance);
			}

			@SuppressWarnings("unchecked")
			public GeneratorT endDebitAccount() {
				return (GeneratorT) TransactionBuilderBase.this;
			}
		}
	}

	public static class BalanceAssertionBuilderBase<GeneratorT extends BalanceAssertionBuilderBase<GeneratorT>> {
		private BalanceAssertion instance;

		protected BalanceAssertionBuilderBase(BalanceAssertion aInstance) {
			instance = aInstance;
		}

		protected BalanceAssertion getInstance() {
			return instance;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withDate(LocalDate aValue) {
			instance.setDate(aValue);

			return (GeneratorT) this;
		}

		@SuppressWarnings("unchecked")
		public GeneratorT withBalance(BigDecimal aValue) {
			instance.setBalance(aValue);

			return (GeneratorT) this;
		}
	}
}
