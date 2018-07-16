package com.jnj.atm.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.jnj.atm.model.ATMUserAccount;

/**
 * @author BALU RAMAMOORTHY
 *
 */
@Repository
public class ATMUserAccountDAO {
	private static final Map<BigInteger, ATMUserAccount> acctMap = new HashMap();
	static {
		initATMUserAccounts();
	}

	private static void initATMUserAccounts() {

		ATMUserAccount acct1 = new ATMUserAccount();
		acct1.setAcctNo(BigInteger.valueOf(123456789));
		acct1.setAcctName("BALU RAMAMOORTHY");
		acct1.setCurrency(Currency.getInstance("EUR"));
		acct1.setPin(1234);
		acct1.setOpeningBal(BigDecimal.valueOf(800.00));
		acct1.setOverdraft(BigDecimal.valueOf(200.00));

		ATMUserAccount acct2 = new ATMUserAccount();
		acct2.setAcctNo(BigInteger.valueOf(987654321));
		acct2.setAcctName("VARUN BALU");
		acct2.setCurrency(Currency.getInstance("EUR"));
		acct2.setPin(4321);
		acct2.setOpeningBal(BigDecimal.valueOf(1230.00));
		acct2.setOverdraft(BigDecimal.valueOf(150.00));

		ATMUserAccount acct3 = new ATMUserAccount();
		acct3.setAcctNo(BigInteger.valueOf(111111111));
		acct3.setAcctName("USER ONE");
		acct3.setCurrency(Currency.getInstance("EUR"));
		acct3.setPin(1111);
		acct3.setOpeningBal(BigDecimal.valueOf(1110));
		acct3.setOverdraft(BigDecimal.valueOf(110));

		ATMUserAccount acct4 = new ATMUserAccount();
		acct4.setAcctNo(BigInteger.valueOf(222222222));
		acct4.setAcctName("USER TWO");
		acct4.setCurrency(Currency.getInstance("EUR"));
		acct4.setPin(2222);
		acct4.setOpeningBal(BigDecimal.valueOf(2222.22));
		acct4.setOverdraft(BigDecimal.valueOf(222.22));

		ATMUserAccount acct5 = new ATMUserAccount();
		acct5.setAcctNo(BigInteger.valueOf(333333333));
		acct5.setAcctName("USER THREE");
		acct5.setCurrency(Currency.getInstance("EUR"));
		acct5.setPin(3333);
		acct5.setOpeningBal(BigDecimal.valueOf(3333.33));
		acct5.setOverdraft(BigDecimal.valueOf(333.33));

		ATMUserAccount acct6 = new ATMUserAccount();
		acct6.setAcctNo(BigInteger.valueOf(444444444));
		acct6.setAcctName("USER FOUR");
		acct6.setCurrency(Currency.getInstance("EUR"));
		acct6.setPin(4444);
		acct6.setOpeningBal(BigDecimal.valueOf(4444.44));
		acct6.setOverdraft(BigDecimal.valueOf(444.44));

		acctMap.put(acct1.getAcctNo(), acct1);
		acctMap.put(acct2.getAcctNo(), acct2);
		acctMap.put(acct3.getAcctNo(), acct3);
		acctMap.put(acct4.getAcctNo(), acct4);
		acctMap.put(acct5.getAcctNo(), acct5);
		acctMap.put(acct6.getAcctNo(), acct6);
	}

	public ATMUserAccount getATMUserAccount(BigInteger acctNo) {
		return acctMap.get(acctNo);
	}

	public synchronized ATMUserAccount updateATMUserAccount(ATMUserAccount acct, String withdrawAmt) {
		ATMUserAccount accountToUpdate = acct;
		BigDecimal withdrawAmount = new BigDecimal(withdrawAmt);
		if (accountToUpdate.getOpeningBal().compareTo(withdrawAmount) >= 0) {
			BigDecimal currentOpeningBalance = accountToUpdate.getOpeningBal().subtract(withdrawAmount);
			accountToUpdate.setOpeningBal(currentOpeningBalance);
		} else {
			BigDecimal overdraftWithdrawAmt = withdrawAmount.subtract(accountToUpdate.getOpeningBal());
			BigDecimal currentOverdraftBalance = accountToUpdate.getOverdraft().subtract(overdraftWithdrawAmt);
			accountToUpdate.setOpeningBal(new BigDecimal(0));
			accountToUpdate.setOverdraft(currentOverdraftBalance);
		}
		acctMap.put(acct.getAcctNo(), acct);
		return acctMap.get(acct.getAcctNo());
	}

	public synchronized ATMUserAccount deposityMoneyToATMUserAccount(ATMUserAccount acct, BigDecimal depositAmt) {
		BigDecimal newOpeningBalance = acct.getOpeningBal().add(depositAmt);
		acct.setOpeningBal(newOpeningBalance);
		acctMap.put(acct.getAcctNo(), acct);
		return acctMap.get(acct.getAcctNo());
	}
}
