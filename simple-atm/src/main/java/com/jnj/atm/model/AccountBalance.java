package com.jnj.atm.model;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BALU RAMAMOORTHY
 *
 */
public class AccountBalance {
	private String txnID;
	private String acctNum;
	private String acctName;
	private BigDecimal balance;
	private BigDecimal overdraftBalance;
	private String alpha3CurrencyCode;
	private Date timestamp;

	@JsonProperty("accountNumber")
	public String getAcctNum() {
		return acctNum;
	}

	@JsonProperty("accountName")
	public String getAcctName() {
		return acctName;
	}

	@JsonProperty("transactionID")
	public String getTxnID() {
		return txnID;
	}

	@JsonProperty("accountBalance")
	public BigDecimal getBalance() {
		return balance;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}

	public void setAcctNum(String acctNum) {
		this.acctNum = acctNum;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getAlpha3CurrencyCode() {
		return alpha3CurrencyCode;
	}

	public void setAlpha3CurrencyCode(String alpha3CurrencyCode) {
		this.alpha3CurrencyCode = alpha3CurrencyCode;
	}

	public BigDecimal getOverdraftBalance() {
		return overdraftBalance;
	}

	public void setOverdraftBalance(BigDecimal overdraftBalance) {
		this.overdraftBalance = overdraftBalance;
	}

}
