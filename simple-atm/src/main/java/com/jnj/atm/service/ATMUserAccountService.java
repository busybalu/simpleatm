package com.jnj.atm.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.jnj.atm.dao.ATMUserAccountDAO;
import com.jnj.atm.model.ATMUserAccount;

/**
 * @author BALU RAMAMOORTHY
 *
 */
@Repository
public class ATMUserAccountService {

	@Autowired
	private ATMUserAccountDAO atmUserAcctDAO;

	public boolean isAuthenticUser(ATMUserAccount usrAcct, String pin) {
		return pin.equals(String.valueOf(usrAcct.getPin()));
	}

	public ATMUserAccount getATMUserAccountByAcctNum(String acctNum) {
		return atmUserAcctDAO.getATMUserAccount(new BigInteger(acctNum));
	}

	public ATMUserAccount updateATMUserAccount(String acctNum, String withdrawAmt) {
		return atmUserAcctDAO.updateATMUserAccount(getATMUserAccountByAcctNum(acctNum), withdrawAmt);
	}

	public ATMUserAccount deposityMoneyToATMUserAccount(String acctNum, BigDecimal depositAmt) {
		return atmUserAcctDAO.deposityMoneyToATMUserAccount(getATMUserAccountByAcctNum(acctNum), depositAmt);
	}

	public boolean hasSufficientFunds(ATMUserAccount usrAcct, String withdrawAmt) {
		return new BigDecimal(withdrawAmt).compareTo(usrAcct.getOpeningBal().add(usrAcct.getOverdraft())) <= 0;
	}

	public boolean isAuthenticAdminUser(String userid, String password) {
		return userid.equals("admin") && password.equals("P@55w0rd");
	}

}
