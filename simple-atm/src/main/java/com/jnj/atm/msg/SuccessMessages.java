package com.jnj.atm.msg;

/**
 * @author BALU RAMAMOORTHY
 *
 */
public enum SuccessMessages {
	WELCOME_TEMPLATE(2001, "Welcome to ATM, %s!"), 
	DEPOSIT_SUCCESS(2002, "Money Deposited to your Bank account %s Successfully");

	private final int code;
	private final String description;

	private SuccessMessages(int code, String description) {
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