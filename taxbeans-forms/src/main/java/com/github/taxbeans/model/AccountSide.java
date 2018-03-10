package com.github.taxbeans.model;

public enum AccountSide {

    //debits entries appear on the left, credits on the right
	BALANCE_EFFECT(Position.AUTO),  //+ve amounts increase the balance and -ve amounts decrease the balance
    DEBIT(Position.LEFT),  //-ve amounts disallowed
    CREDIT(Position.RIGHT),  //-ve amounts disallowed
    MEMO(Position.AUTO); //memo entry to associate a memo with an account

    @SuppressWarnings("unused")
	private Position position;
    
    AccountSide(Position position) {
      this.position = position;
    }
    
    public String toString() {
    	return this == DEBIT ? "DR" : "CR";
    }
}
