package com.jnj.atm.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author BALU RAMAMOORTHY
 *
 */
public class CurrentAccountBalance {
	private String txnID;
	private String acctNum;
	private String acctName;
	private BigDecimal withdrawnAmount;
	private BigDecimal currentBalance;
	private BigDecimal overdraftBalance;
	private String alpha3CurrencyCode;
	private Date timestamp;
	private Map<Integer, Integer> denominations;

	@JsonProperty("transactionID")
	public String getTxnID() {
		return txnID;
	}

	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}

	@JsonProperty("accountNumber")
	public String getAcctNum() {
		return acctNum;
	}

	public void setAcctNum(String acctNum) {
		this.acctNum = acctNum;
	}

	@JsonProperty("accountName")
	public String getAcctName() {
		return acctName;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}

	public BigDecimal getWithdrawnAmount() {
		return withdrawnAmount;
	}

	public void setWithdrawnAmount(BigDecimal withdrawnAmount) {
		this.withdrawnAmount = withdrawnAmount;
	}
	@JsonProperty("dispensedDenominations")
	public Map<Integer, Integer> getDenominations() {
		return denominations;
	}

	public void setDenominations(Map<Integer, Integer> denominations) {
		this.denominations = denominations;
	}

	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}

	public Date getTimestamp() {
		return timestamp;
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
