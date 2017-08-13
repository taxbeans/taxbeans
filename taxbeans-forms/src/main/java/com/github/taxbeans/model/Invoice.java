package com.github.taxbeans.model;

import java.time.LocalDate;
import java.util.List;

import org.javamoney.moneta.Money;

public class Invoice { 

	private LocalDate date;
	
	private Money amount;
	
	private List<Payment> payments;
	
	private ReceiptReference receiptReference;
	
	private TaxReference taxReference;
	

}
