package com.jnj.atm.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;

/**
 * @author BALU RAMAMOORTHY
 *
 */
public class ATMUserAccount {
	private BigInteger acctNo;
	private int pin;
	private String acctName;
	private BigDecimal openingBal;
	private BigDecimal overdraft;
	private Currency currency;

	public BigInteger getAcctNo() {
		return acctNo;
	}

	public void setAcctNo(BigInteger acctNo) {
		this.acctNo = acctNo;
	}

	public int getPin() {
		return pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public String getAcctName() {
		return acctName;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}

	public BigDecimal getOpeningBal() {
		return openingBal;
	}

	public void setOpeningBal(BigDecimal openingBal) {
		this.openingBal = openingBal;
	}

	public BigDecimal getOverdraft() {
		return overdraft;
	}

	public void setOverdraft(BigDecimal overdraft) {
		this.overdraft = overdraft;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
}