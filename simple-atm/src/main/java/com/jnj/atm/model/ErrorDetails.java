package com.jnj.atm.model;

import java.util.Date;

/**
 * @author BALU RAMAMOORTHY
 *
 */
public class ErrorDetails {
	private String txnID;
	private Date timestamp;
	private int errorCode;
	private String errorReason;

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

	public String getTxnID() {
		return txnID;
	}

	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}
}
