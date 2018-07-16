package com.jnj.atm.msg;

/**
 * @author BALU RAMAMOORTHY
 *
 */
public enum ErrorMessages {
	INVALID_ACCT(1001, "Invalid Account Number and/or PIN"), 
	INSUFFICIENT_FUNDS(1002, "Insufficient Funds in your Account"), 
	INVALID_AMOUNT(1003, "Invalid Amount"), 
	ATM_OUT_OF_EXPECTED_CASH(1004, "Sorry For the Inconvinience! ATM is out of expected cash. Please try with lesser Amount"), 
	ATM_OUT_OF_CASH(1005, "Sorry For the Inconvinience! ATM is out of cash."), 
	INVALID_ACCT_NUMBER(1001, "Invalid Account Number");

	private final int code;
	private final String description;

	private ErrorMessages(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getCode() {
		return code;
	}

}