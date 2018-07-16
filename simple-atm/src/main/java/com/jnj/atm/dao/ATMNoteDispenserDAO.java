package com.jnj.atm.dao;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.jnj.atm.model.ATMNotesDispenser;

/**
 * @author BALU RAMAMOORTHY
 *
 */
@Repository
public class ATMNoteDispenserDAO {

	private static final ATMNotesDispenser atmNoteDispenser = new ATMNotesDispenser();
	private static final int FIFTY = 50;
	private static final int TWENTY = 20;
	private static final int TEN = 10;
	private static final int FIVE = 5;

	static {
		initATMNotesDispenser();
	}

	private static void initATMNotesDispenser() {
		atmNoteDispenser.setNote50Counter(20);
		atmNoteDispenser.setNote20Counter(30);
		atmNoteDispenser.setNote10Counter(30);
		atmNoteDispenser.setNote5Counter(20);
		atmNoteDispenser.setTotalAmountInATM(calculateTotalAmountInATM());
	}

	public ATMNotesDispenser getATMNoteDispenser() {
		return atmNoteDispenser;
	}

	public synchronized ATMNotesDispenser updateATMNoteDispenser(Map<Integer, Integer> dispensedNoteCount) {
			atmNoteDispenser.setNote50Counter(
					atmNoteDispenser.getNote50Counter() - getDenominationCount(dispensedNoteCount, FIFTY));
			atmNoteDispenser.setNote20Counter(
					atmNoteDispenser.getNote20Counter() - getDenominationCount(dispensedNoteCount, TWENTY));
			atmNoteDispenser.setNote10Counter(
					atmNoteDispenser.getNote10Counter() - getDenominationCount(dispensedNoteCount, TEN));
			atmNoteDispenser.setNote5Counter(
					atmNoteDispenser.getNote5Counter() - getDenominationCount(dispensedNoteCount, FIVE));
			atmNoteDispenser.setTotalAmountInATM(calculateTotalAmountInATM());
		return atmNoteDispenser;
	}

	public synchronized ATMNotesDispenser loadATMMoney(Map<Integer, Integer> loadingNoteCount) {
		if (!loadingNoteCount.isEmpty()) {
			atmNoteDispenser.setNote50Counter(
					atmNoteDispenser.getNote50Counter() + getDenominationCount(loadingNoteCount, FIFTY));
			atmNoteDispenser.setNote20Counter(
					atmNoteDispenser.getNote20Counter() + getDenominationCount(loadingNoteCount, TWENTY));
			atmNoteDispenser.setNote10Counter(
					atmNoteDispenser.getNote10Counter() + getDenominationCount(loadingNoteCount, TEN));
			atmNoteDispenser
					.setNote5Counter(atmNoteDispenser.getNote5Counter() + getDenominationCount(loadingNoteCount, FIVE));
			atmNoteDispenser.setTotalAmountInATM(calculateTotalAmountInATM());
		}
		return atmNoteDispenser;
	}

	private int getDenominationCount(Map<Integer, Integer> loadingNoteCount, Integer denomination) {
		return null != loadingNoteCount.get(denomination) ? loadingNoteCount.get(denomination) : 0;
	}

	private static BigDecimal calculateTotalAmountInATM() {
		return new BigDecimal(atmNoteDispenser.getNote50Counter() * FIFTY)
				.add(new BigDecimal(atmNoteDispenser.getNote20Counter() * TWENTY))
				.add(new BigDecimal(atmNoteDispenser.getNote10Counter() * TEN))
				.add(new BigDecimal(atmNoteDispenser.getNote5Counter() * FIVE));
	}
}
