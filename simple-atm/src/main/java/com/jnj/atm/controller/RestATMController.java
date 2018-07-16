/**
 * This Class serves RESTFULL end points for all the below ATM Services.
 * 1) Greet User
 * 2) Inquire Account Details
 * 3) Withdraw Money from ATM
 * 4) Deposit Money to Bank Account
 * 5) Check ATM Health
 * 6) Load Money to ATM
 */
package com.jnj.atm.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jnj.atm.exceptions.ATMOutOfCashException;
import com.jnj.atm.exceptions.ATMOutOfExpectedCashException;
import com.jnj.atm.exceptions.AccountNotFoundException;
import com.jnj.atm.exceptions.InsufficientFundsException;
import com.jnj.atm.exceptions.InvalidAccountException;
import com.jnj.atm.exceptions.InvalidAmountException;
import com.jnj.atm.model.ATMNotesDispenser;
import com.jnj.atm.model.ATMUserAccount;
import com.jnj.atm.model.AccountBalance;
import com.jnj.atm.model.CurrentAccountBalance;
import com.jnj.atm.model.GreetUser;
import com.jnj.atm.model.SuccessDetails;
import com.jnj.atm.msg.SuccessMessages;
import com.jnj.atm.service.ATMNoteDispenserService;
import com.jnj.atm.service.ATMUserAccountService;

/**
 * @author BALU RAMAMOORTHY
 *
 */
@RestController
public class RestATMController {

	@Autowired
	private ATMUserAccountService atmUserAcctService;

	@Autowired
	private ATMNoteDispenserService atmNoteDispenserService;

	/**
	 * This method serves as '/atm/greetuser/{acctNum}' RESTFULL webservice end
	 * point to greet the ATM user.
	 * 
	 * Sample Output: {"message": "Welcome to ATM, BALU RAMAMOORTHY!"}
	 * 
	 * @param acctNum
	 *            Account Number of ATM user
	 * @return GreetUser
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/atm/greetuser/{acctNum}")
	public GreetUser greetUserWithName(@PathVariable(value = "acctNum") String acctNum) {
		ATMUserAccount usrAcct = atmUserAcctService.getATMUserAccountByAcctNum(acctNum);
		if (null != usrAcct) {
			//Comes here when User Account Exists in our Records.
			GreetUser greetUsr = new GreetUser();
			greetUsr.setMessage(
					String.format(SuccessMessages.WELCOME_TEMPLATE.getDescription(), usrAcct.getAcctName()));
			return greetUsr;
		} else {
			throw new InvalidAccountException();
		}
	}

	/**
	 * This method serves as '/atm/inquirebalance/{acctNum}/{pin}' RESTFULL
	 * webservice end point to check ATM user's Account Details which includes
	 * overdraftBalance, alpha3CurrencyCode, timestamp, transactionID,
	 * accountNumber, accountName and accountBalance.
	 * 
	 * Sample Output : { "overdraftBalance": 200, "alpha3CurrencyCode": "EUR",
	 * "timestamp": "2018-07-16T20:19:08.845+0000", "transactionID":
	 * "929e28e1-5db4-4467-ae1b-d96a90cc8e99", "accountNumber": "123456789",
	 * "accountName": "BALU RAMAMOORTHY", "accountBalance": 800 }
	 * 
	 * @param acctNum
	 *            Account Number of ATM user
	 * @param pin
	 *            PIN Number of ATM user
	 * @return AccountBalance
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/atm/inquirebalance/{acctNum}/{pin}")
	public AccountBalance inquireBalance(@PathVariable(value = "acctNum") String acctNum,
			@PathVariable(value = "pin") String pin) {

		ATMUserAccount usrAcct = atmUserAcctService.getATMUserAccountByAcctNum(acctNum);
		// Throw AccountNotFoundException if the account does not exists and/or user is
		// not an authentic user.
		if (usrAcct == null || !atmUserAcctService.isAuthenticUser(usrAcct, pin)) {
			throw new AccountNotFoundException();
		} else {
			// Comes here if user Account Exists in our records and the user is
			// authenticated Successfully.
			AccountBalance acctBalcheckTxn = new AccountBalance();
			acctBalcheckTxn.setTxnID(UUID.randomUUID().toString());
			acctBalcheckTxn.setBalance(usrAcct.getOpeningBal());
			acctBalcheckTxn.setTimestamp(new Date());
			acctBalcheckTxn.setAcctNum(usrAcct.getAcctNo().toString());
			acctBalcheckTxn.setAcctName(usrAcct.getAcctName());
			acctBalcheckTxn.setAlpha3CurrencyCode(usrAcct.getCurrency().toString());
			acctBalcheckTxn.setOverdraftBalance(usrAcct.getOverdraft());

			return acctBalcheckTxn;
		}
	}

	/**
	 * This method serves as a RESTFULL webservice end point to withdraw money from
	 * the ATM.
	 * 
	 * Sample Output: { "withdrawnAmount": 235, "currentBalance": 565,
	 * "overdraftBalance": 200, "alpha3CurrencyCode": "EUR", "timestamp":
	 * "2018-07-16T19:29:17.437+0000", "transactionID":
	 * "d066e4f9-95e1-4752-aa76-a3e06faaf8ef", "accountNumber": "123456789",
	 * "accountName": "BALU RAMAMOORTHY", "dispensedDenominations": { "50": 4, "20":
	 * 1, "10": 1, "5": 1 } }
	 * 
	 * @param withdrawAmt
	 *            Amount to Withdraw
	 * @param acctNum
	 *            Account Number of ATM User
	 * @param pin
	 *            PIN Number of ATM User
	 * @return CurrentAccountBalance
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/atm/withdrawmoney/{withdrawAmt}/{acctNum}/{pin}")
	public CurrentAccountBalance withdrawMoney(@PathVariable(value = "withdrawAmt") String withdrawAmt,
			@PathVariable(value = "acctNum") String acctNum, @PathVariable(value = "pin") String pin) {

		CurrentAccountBalance currentAccountBalanceTxn = null;
		BigDecimal withdrawAmount = new BigDecimal(withdrawAmt);
		ATMUserAccount usrAcct = atmUserAcctService.getATMUserAccountByAcctNum(acctNum);

		// Throw AccountNotFoundException if the account does not exists and/or user is
		// not an authentic user.
		if (usrAcct == null || !atmUserAcctService.isAuthenticUser(usrAcct, pin)) {
			throw new AccountNotFoundException();
		} else if (withdrawAmount.signum() == -1 || withdrawAmount.signum() == 0) {
			throw new InvalidAmountException();
		} else if (atmNoteDispenserService.getATMNoteDispenser().getTotalAmountInATM().signum() == 0) {
			throw new ATMOutOfCashException();
		} else if (!atmUserAcctService.hasSufficientFunds(usrAcct, withdrawAmt)) {
			// Comes here if user Account has InSufficient Funds.
			throw new InsufficientFundsException();
		} else if (withdrawAmount.compareTo(atmNoteDispenserService.getATMNoteDispenser().getTotalAmountInATM()) > 0) {
			throw new ATMOutOfExpectedCashException();
		} else if (!atmNoteDispenserService.isValidAmountToDispense(withdrawAmount)) {
			throw new InvalidAmountException();
		} else {
			// Comes here if user Account has Sufficient Funds.
			ATMUserAccount updatedAcct = atmUserAcctService.updateATMUserAccount(acctNum, withdrawAmt);
			currentAccountBalanceTxn = new CurrentAccountBalance();
			currentAccountBalanceTxn.setTxnID(UUID.randomUUID().toString());
			currentAccountBalanceTxn.setWithdrawnAmount(withdrawAmount.setScale(2));
			currentAccountBalanceTxn.setCurrentBalance(updatedAcct.getOpeningBal());
			currentAccountBalanceTxn.setOverdraftBalance(updatedAcct.getOverdraft());
			currentAccountBalanceTxn.setTimestamp(new Date());
			currentAccountBalanceTxn.setAcctNum(updatedAcct.getAcctNo().toString());
			currentAccountBalanceTxn.setAcctName(updatedAcct.getAcctName());
			currentAccountBalanceTxn.setAlpha3CurrencyCode(updatedAcct.getCurrency().toString());
			currentAccountBalanceTxn
					.setDenominations(atmNoteDispenserService.getNotesDenominationToDispense(withdrawAmount));
			atmNoteDispenserService.updateATMNoteDispenser(withdrawAmount);
			return currentAccountBalanceTxn;
		}

	}

	/**
	 * This method serves as a RESTFULL webservice end point to deposit money to
	 * Bank Account
	 * 
	 * Sample Output: { "txnID": "cfa90d43-45d3-433e-bd65-996f6b8335f9",
	 * "timestamp": "2018-07-16T19:29:24.069+0000", "successCode": 2002,
	 * "successMessage": "Money Deposited to your Bank account 123456789
	 * Successfully" }.
	 * 
	 * @param depositAmt
	 *            Amount to Deposit
	 * @param acctNum
	 *            Account Number of the ATM User
	 * @return SuccessDetails
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/bank/depositmoney/{depositAmt}/{acctNum}")
	public SuccessDetails depositMoney(@PathVariable(value = "depositAmt") String depositAmt,
			@PathVariable(value = "acctNum") String acctNum) {
		BigDecimal depositAmount = new BigDecimal(depositAmt);
		ATMUserAccount usrAcct = atmUserAcctService.getATMUserAccountByAcctNum(acctNum);
		SuccessDetails succesDetail = null;
		// Throw AccountNotFoundException if the account does not exists.
		if (usrAcct == null) {
			throw new InvalidAccountException();
		} else if (depositAmount.signum() == -1 || depositAmount.signum() == 0) {
			throw new InvalidAmountException();
		} else {
			ATMUserAccount updatedUsrAcct = atmUserAcctService.deposityMoneyToATMUserAccount(acctNum, depositAmount);
			succesDetail = new SuccessDetails();
			succesDetail.setTxnID(UUID.randomUUID().toString());
			succesDetail.setTimestamp(new Date());
			succesDetail.setSuccessCode(SuccessMessages.DEPOSIT_SUCCESS.getCode());
			succesDetail.setSuccessMessage(
					String.format(SuccessMessages.DEPOSIT_SUCCESS.getDescription(), updatedUsrAcct.getAcctNo()));
			return succesDetail;
		}
	}

	/**
	 * 
	 * @param userid
	 *            Admin User ID
	 * @param password
	 *            Admin User Password
	 * @return ATMNotesDispenser
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/atm/healthCheck/{userid}/{password}")
	public ATMNotesDispenser checkATMHealth(@PathVariable(value = "userid") String userid,
			@PathVariable(value = "password") String password) {

		// Throw AccountNotFoundException if the account does not exists and/or user is
		// not an authentic Admin user.
		if (!atmUserAcctService.isAuthenticAdminUser(userid, password)) {
			throw new AccountNotFoundException();
		} else {
			return atmNoteDispenserService.getATMNoteDispenser();
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/atm/loadmoney/{userid}/{password}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ATMNotesDispenser loadMoneyToATM(@PathVariable(value = "userid") String userid,
			@PathVariable(value = "password") String password, @RequestBody Map<Integer, Integer> notesToLoad) {

		// Throw AccountNotFoundException if the account does not exists and/or user is
		// not an authentic Admin user.
		if (!atmUserAcctService.isAuthenticAdminUser(userid, password)) {
			throw new AccountNotFoundException();
		} else {
			return atmNoteDispenserService.loadATMMoney(notesToLoad);
		}
	}
}
