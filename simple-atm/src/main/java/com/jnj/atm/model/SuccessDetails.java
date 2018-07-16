package com.jnj.atm.model;

import java.util.Date;

/**
 * @author BALU RAMAMOORTHY
 *
 */
public class SuccessDetails {
	private String txnID;
	private Date timestamp;
	private int successCode;
	private String successMessage;

	public String getTxnID() {
		return txnID;
	}

	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getSuccessCode() {
		return successCode;
	}

	public void setSuccessCode(int successCode) {
		this.successCode = successCode;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

}
