package com.jnj.atm.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jnj.atm.dao.ATMNoteDispenserDAO;
import com.jnj.atm.model.ATMNotesDispenser;

/**
 * @author BALU RAMAMOORTHY
 *
 */
@Repository
public class ATMNoteDispenserService {

	private static final int FIFTY = 50;
	private static final int TWENTY = 20;
	private static final int TEN = 10;
	private static final int FIVE = 5;

	@Autowired
	private ATMNoteDispenserDAO atmNoteDispenserDAO;

	public Map<Integer, Integer> getNotesDenominationToDispense(BigDecimal withdrawAmount) {

		Map<Integer, Integer> currentNoteCountMap = new LinkedHashMap<>();
		currentNoteCountMap.put(FIFTY, atmNoteDispenserDAO.getATMNoteDispenser().getNote50Counter());
		currentNoteCountMap.put(TWENTY, atmNoteDispenserDAO.getATMNoteDispenser().getNote20Counter());
		currentNoteCountMap.put(TEN, atmNoteDispenserDAO.getATMNoteDispenser().getNote10Counter());
		currentNoteCountMap.put(FIVE, atmNoteDispenserDAO.getATMNoteDispenser().getNote5Counter());

		Map<Integer, Integer> dispenseNoteCountMap = new LinkedHashMap<>();
		dispenseNoteCountMap.put(FIFTY, 0);
		dispenseNoteCountMap.put(TWENTY, 0);
		dispenseNoteCountMap.put(TEN, 0);
		dispenseNoteCountMap.put(FIVE, 0);

		for (Map.Entry<Integer, Integer> entry : currentNoteCountMap.entrySet()) {
			if (withdrawAmount.compareTo(new BigDecimal(entry.getKey())) >= 0 && entry.getValue() > 0) {
				if (withdrawAmount.divide(new BigDecimal(entry.getKey())).intValue() >= entry.getValue()) {
					dispenseNoteCountMap.put(entry.getKey(), entry.getValue());
				} else {
					dispenseNoteCountMap.put(entry.getKey(),
							withdrawAmount.divide(new BigDecimal(entry.getKey())).intValue());
				}
				BigDecimal availedNotesAmount = new BigDecimal(
						dispenseNoteCountMap.get(entry.getKey()) * entry.getKey());
				withdrawAmount = withdrawAmount.subtract(availedNotesAmount);
			}
		}
		return dispenseNoteCountMap;
	}

	public boolean isValidAmountToDispense(BigDecimal withdrawAmount) {
		boolean isValidAmount = false;
		Map<Integer, Integer> denominationToDispense = getNotesDenominationToDispense(withdrawAmount);
		BigDecimal nearestAmtATMCanDispense = new BigDecimal("0.00");
		for (Map.Entry<Integer, Integer> entry : denominationToDispense.entrySet()) {
			nearestAmtATMCanDispense = nearestAmtATMCanDispense.add(new BigDecimal(entry.getKey() * entry.getValue()));
		}
		if (nearestAmtATMCanDispense.compareTo(withdrawAmount) == 0) {
			isValidAmount = true;
		}

		return isValidAmount;
	}

	public ATMNotesDispenser getATMNoteDispenser() {
		return atmNoteDispenserDAO.getATMNoteDispenser();
	}

	public ATMNotesDispenser updateATMNoteDispenser(BigDecimal withdrawAmount) {
		Map<Integer, Integer> denominationToDispense = getNotesDenominationToDispense(withdrawAmount);
		return atmNoteDispenserDAO.updateATMNoteDispenser(denominationToDispense);
	}

	public ATMNotesDispenser loadATMMoney(Map<Integer, Integer> notesToLoad) {
		return atmNoteDispenserDAO.loadATMMoney(notesToLoad);
	}

}
