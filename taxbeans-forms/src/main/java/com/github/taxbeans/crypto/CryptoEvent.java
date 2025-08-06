package com.github.taxbeans.crypto;

import java.time.ZonedDateTime;

public interface CryptoEvent extends Comparable<CryptoEvent> {
	
	public ZonedDateTime getWhen();

	public String getGroup();

	public void setGroup(String group);

	public int getRowNum();

	public void setRowNum(int rowNum);

	public CurrencyExchange getCurrencyExchange();

	public void setCurrencyExchange(CurrencyExchange exchange);

	public void setComment(String comment);

	String getComment();

	public void setFundsIntroduced(boolean b);

	public void setBusinessExpense(boolean b);

	public CryptoEventType getCryptoEventType();

	public String getSourceCSVLine();

	public void setSourceCSVLine(String s);

	public boolean isLeveraged();

}
