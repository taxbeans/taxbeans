package com.github.taxbeans.model;

public enum AccountSide {

    //debits entries appear on the left, credits on the right
    DEBIT(Position.LEFT),
    CREDIT(Position.RIGHT);

    private Position position;
    
    AccountSide(Position position) {
      this.position = position;
    }
    
    public String toString() {
    	return this == DEBIT ? "DR" : "CR";
    }
}
