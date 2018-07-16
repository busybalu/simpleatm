package com.jnj.atm.model;

import java.math.BigDecimal;

/**
 * @author BALU RAMAMOORTHY
 *
 */
public class ATMNotesDispenser {
	private int note50Counter;
	private int note20Counter;
	private int note10Counter;
	private int note5Counter;
	private BigDecimal totalAmountInATM;

	public int getNote50Counter() {
		return note50Counter;
	}

	public void setNote50Counter(int note50Counter) {
		this.note50Counter = note50Counter;
	}

	public int getNote20Counter() {
		return note20Counter;
	}

	public void setNote20Counter(int note20Counter) {
		this.note20Counter = note20Counter;
	}

	public int getNote10Counter() {
		return note10Counter;
	}

	public void setNote10Counter(int note10Counter) {
		this.note10Counter = note10Counter;
	}

	public int getNote5Counter() {
		return note5Counter;
	}

	public void setNote5Counter(int note5Counter) {
		this.note5Counter = note5Counter;
	}

	public BigDecimal getTotalAmountInATM() {
		return totalAmountInATM;
	}

	public void setTotalAmountInATM(BigDecimal totalAmountInATM) {
		this.totalAmountInATM = totalAmountInATM;
	}

}
