package com.jnj.atm.controller;

import static org.assertj.core.api.BDDAssertions.then;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.jnj.atm.main.SimpleAtmApplication;
import com.jnj.atm.model.ATMNotesDispenser;
import com.jnj.atm.model.AccountBalance;
import com.jnj.atm.model.CurrentAccountBalance;
import com.jnj.atm.model.ErrorDetails;
import com.jnj.atm.model.GreetUser;
import com.jnj.atm.model.SuccessDetails;
import com.jnj.atm.msg.ErrorMessages;
import com.jnj.atm.msg.SuccessMessages;

/**
 * @author BALU RAMAMOORTHY
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { SimpleAtmApplication.class })
public class RestATMControllerTests {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;
	/**
	 * This method is to test the greet user service shows the appropriate greet message to the ATM User.
	 * @throws Exception
	 */
	@Test
	public void testGreetUserWithName() throws Exception {

		String validAcctNum = "123456789";
		String validPin = "1234";
		
		// Use the /atm/inquirebalance/ service to get the Account Name to Validate the /atm/greetuser/ service
		ResponseEntity<AccountBalance> inquireBalanceResEntity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + "/atm/inquirebalance/" + validAcctNum + "/" + validPin,
				AccountBalance.class);
		then(inquireBalanceResEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		//Get the Account Name
		String expectedUserAcctName = inquireBalanceResEntity.getBody().getAcctName();
		
		// Validate the /atm/greetuser/ service using Valid Account Number 
		ResponseEntity<GreetUser> entity = this.testRestTemplate
				.getForEntity("http://localhost:" + this.port + "/atm/greetuser/" + validAcctNum, GreetUser.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody().getMessage()).isEqualTo(String.format(SuccessMessages.WELCOME_TEMPLATE.getDescription(), expectedUserAcctName));
	}

	@Test
	public void testGreetUserUsingWrongAccountNumber() throws Exception {

		String invalidAcctNum = "5636346456456";
		// Validate the /atm/greetuser/ service using InValid Account Number 
		ResponseEntity<ErrorDetails> entity = this.testRestTemplate
				.getForEntity("http://localhost:" + this.port + "/atm/greetuser/" + invalidAcctNum, ErrorDetails.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		then(entity.getBody().getErrorCode()).isEqualTo(ErrorMessages.INVALID_ACCT_NUMBER.getCode());
		then(entity.getBody().getErrorReason()).isEqualTo(ErrorMessages.INVALID_ACCT_NUMBER.getDescription());
	}

	@Test
	public void testInquireBalanceWithValidAcctNumAndPin() {
		String validAcctNum = "123456789";
		String validPin = "1234";
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + "/atm/inquirebalance/" + validAcctNum + "/" + validPin, Map.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void testInquireBalanceWithWrongAcctNum() {
		String invalidAcctNum = "645634345345";
		String pin = "1234";
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + "/atm/inquirebalance/" + invalidAcctNum + "/" + pin, Map.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testInquireBalanceUsingWrongPin() {
		String validAcctNum = "123456789";
		String wrongPin = "2222";
		@SuppressWarnings("rawtypes")

		ResponseEntity<Map> entity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + "/atm/inquirebalance/" + validAcctNum + "/" + wrongPin, Map.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testWithdrawMoneyUsingValidCredentials() {
		String validAcctNum = "123456789";
		String validPin = "1234";
		String validWithdrawAmount = "10";

		ResponseEntity<CurrentAccountBalance> entity = this.testRestTemplate.postForEntity("http://localhost:"
				+ this.port + "/atm/withdrawmoney/" + validWithdrawAmount + "/" + validAcctNum + "/" + validPin, null,
				CurrentAccountBalance.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody()).isNotNull();
	}

	@Test
	public void testWithdrawMoneyWithinOpeningBalanceLimit() {
		String validAcctNum = "123456789";
		String validPin = "1234";
		String validWithdrawAmount = "100";
		BigDecimal validWithdrawAmountDec = new BigDecimal(validWithdrawAmount);

		ResponseEntity<AccountBalance> checkBalResEntity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + "/atm/inquirebalance/" + validAcctNum + "/" + validPin,
				AccountBalance.class);

		then(checkBalResEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		BigDecimal openingBal = checkBalResEntity.getBody().getBalance();
		BigDecimal overdraftBal = checkBalResEntity.getBody().getOverdraftBalance();

		BigDecimal expectedCurrentBal = openingBal.subtract(validWithdrawAmountDec);
		BigDecimal expectedOverdraftBal = overdraftBal;

		ResponseEntity<CurrentAccountBalance> entity = this.testRestTemplate.postForEntity("http://localhost:"
				+ this.port + "/atm/withdrawmoney/" + validWithdrawAmount + "/" + validAcctNum + "/" + validPin, null,
				CurrentAccountBalance.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody().getCurrentBalance()).isEqualTo(expectedCurrentBal);
		then(entity.getBody().getOverdraftBalance()).isEqualTo(expectedOverdraftBal);
	}

	@Test
	public void testWithdrawMoneyGreaterthanTotalAmountInATM() {
		String validAcctNum = "222222222";
		String validPin = "2222";
		String validAdminUser = "admin";
		String validAdminPassword = "P@55w0rd";

		ResponseEntity<ATMNotesDispenser> atmHealthCheckResEntity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + "/atm/healthCheck/" + validAdminUser + "/" + validAdminPassword,
				ATMNotesDispenser.class);
		then(atmHealthCheckResEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		BigDecimal totalAmountInATM = atmHealthCheckResEntity.getBody().getTotalAmountInATM();
		String totalAmountInATMPlus10 = totalAmountInATM.add(new BigDecimal("10.00")).toString();

		ResponseEntity<ErrorDetails> entity = this.testRestTemplate.postForEntity("http://localhost:" + this.port
				+ "/atm/withdrawmoney/" + totalAmountInATMPlus10 + "/" + validAcctNum + "/" + validPin, null,
				ErrorDetails.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		then(entity.getBody().getErrorCode()).isEqualTo(ErrorMessages.ATM_OUT_OF_EXPECTED_CASH.getCode());
		then(entity.getBody().getErrorReason()).isEqualTo(ErrorMessages.ATM_OUT_OF_EXPECTED_CASH.getDescription());
	}

	@Test
	public void testWithdrawMoneyUsingInValidAccount() {
		String invalidAcctNum = "3523543234";
		String invalidPin = "3433";
		String validWithdrawAmount = "100";
		ResponseEntity<ErrorDetails> entity = this.testRestTemplate.postForEntity("http://localhost:" + this.port
				+ "/atm/withdrawmoney/" + validWithdrawAmount + "/" + invalidAcctNum + "/" + invalidPin, null,
				ErrorDetails.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		then(entity.getBody().getErrorCode()).isEqualTo(ErrorMessages.INVALID_ACCT.getCode());
		then(entity.getBody().getErrorReason()).isEqualTo(ErrorMessages.INVALID_ACCT.getDescription());
	}

	@Test
	public void testWithdrawMoneyUsingWrongPin() {
		String validAcctNum = "123456789";
		String invalidPin = "0000";
		String validWithdrawAmount = "100";
		ResponseEntity<ErrorDetails> entity = this.testRestTemplate.postForEntity("http://localhost:" + this.port
				+ "/atm/withdrawmoney/" + validWithdrawAmount + "/" + validAcctNum + "/" + invalidPin, null,
				ErrorDetails.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		then(entity.getBody().getErrorCode()).isEqualTo(ErrorMessages.INVALID_ACCT.getCode());
		then(entity.getBody().getErrorReason()).isEqualTo(ErrorMessages.INVALID_ACCT.getDescription());
	}

	@Test
	public void testWithdrawMoneyUsingNegativeWithdrawAmount() {
		String validAcctNum = "123456789";
		String validPin = "1234";
		String invalidWithdrawAmount = "-100";
		ResponseEntity<ErrorDetails> entity = this.testRestTemplate.postForEntity("http://localhost:" + this.port
				+ "/atm/withdrawmoney/" + invalidWithdrawAmount + "/" + validAcctNum + "/" + validPin, null,
				ErrorDetails.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		then(entity.getBody().getErrorCode()).isEqualTo(ErrorMessages.INVALID_AMOUNT.getCode());
		then(entity.getBody().getErrorReason()).isEqualTo(ErrorMessages.INVALID_AMOUNT.getDescription());
	}

	@Test
	public void testWithdrawMoneyUsingZeroWithdrawAmount() {
		String validAcctNum = "123456789";
		String validPin = "1234";
		String invalidWithdrawAmount = "0";
		ResponseEntity<ErrorDetails> entity = this.testRestTemplate.postForEntity("http://localhost:" + this.port
				+ "/atm/withdrawmoney/" + invalidWithdrawAmount + "/" + validAcctNum + "/" + validPin, null,
				ErrorDetails.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		then(entity.getBody().getErrorCode()).isEqualTo(ErrorMessages.INVALID_AMOUNT.getCode());
		then(entity.getBody().getErrorReason()).isEqualTo(ErrorMessages.INVALID_AMOUNT.getDescription());
	}

	@Test
	public void testWithdrawMoneyUsingWrongAmountToDispense() {
		String validAcctNum = "123456789";
		String validPin = "1234";
		String invalidWithdrawAmount = "117";
		ResponseEntity<ErrorDetails> entity = this.testRestTemplate.postForEntity("http://localhost:" + this.port
				+ "/atm/withdrawmoney/" + invalidWithdrawAmount + "/" + validAcctNum + "/" + validPin, null,
				ErrorDetails.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		then(entity.getBody().getErrorCode()).isEqualTo(ErrorMessages.INVALID_AMOUNT.getCode());
		then(entity.getBody().getErrorReason()).isEqualTo(ErrorMessages.INVALID_AMOUNT.getDescription());
	}

	@Test
	public void testWithdrawMoneyGreaterThanOpeningBalButCanbeCoveredByOverdraft() {
		String validAcctNum = "111111111";
		String validPin = "1111";
		String validWithdrawAmount = "1115"; // Amount here is greater than Opening Balance but Can be Withdrawn using
												// Overdraft Amount
		ResponseEntity<ErrorDetails> entity = this.testRestTemplate.postForEntity("http://localhost:" + this.port
				+ "/atm/withdrawmoney/" + validWithdrawAmount + "/" + validAcctNum + "/" + validPin, null,
				ErrorDetails.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void testWithdrawMoneyGreaterThanAmountInTheAccount() {
		String validAcctNum = "123456789";
		String validPin = "1234";

		ResponseEntity<AccountBalance> inquireBalanceResEntity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + "/atm/inquirebalance/" + validAcctNum + "/" + validPin,
				AccountBalance.class);

		BigDecimal currentBal = inquireBalanceResEntity.getBody().getBalance();
		BigDecimal overdraftBal = inquireBalanceResEntity.getBody().getOverdraftBalance();

		BigDecimal totalWithdrawableBalPlus10 = currentBal.add(overdraftBal).add(new BigDecimal("10.00"));

		ResponseEntity<ErrorDetails> entity = this.testRestTemplate.postForEntity("http://localhost:" + this.port
				+ "/atm/withdrawmoney/" + totalWithdrawableBalPlus10 + "/" + validAcctNum + "/" + validPin, null,
				ErrorDetails.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		then(entity.getBody().getErrorCode()).isEqualTo(ErrorMessages.INSUFFICIENT_FUNDS.getCode());
		then(entity.getBody().getErrorReason()).isEqualTo(ErrorMessages.INSUFFICIENT_FUNDS.getDescription());
	}

	@Test
	public void testWithdrawMoneyWhenATMIsOutOfCash() {
		String validAcctNum = "444444444";
		String validPin = "4444";
		String validAdminUser = "admin";
		String validAdminPassword = "P@55w0rd";

		ResponseEntity<ATMNotesDispenser> atmHealthResEntity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + "/atm/healthCheck/" + validAdminUser + "/" + validAdminPassword,
				ATMNotesDispenser.class);

		BigDecimal totalMoneyInATM = atmHealthResEntity.getBody().getTotalAmountInATM();

		then(atmHealthResEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		ResponseEntity<CurrentAccountBalance> currentAcctBalResEntity = this.testRestTemplate
				.postForEntity("http://localhost:" + this.port + "/atm/withdrawmoney/" + totalMoneyInATM + "/"
						+ validAcctNum + "/" + validPin, null, CurrentAccountBalance.class);

		then(currentAcctBalResEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(currentAcctBalResEntity.getBody()).isNotNull();

		String withdrawAmt = "10";
		ResponseEntity<ErrorDetails> currentAcctBalResEntity1 = this.testRestTemplate.postForEntity("http://localhost:"
				+ this.port + "/atm/withdrawmoney/" + withdrawAmt + "/" + validAcctNum + "/" + validPin, null,
				ErrorDetails.class);

		then(currentAcctBalResEntity1.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		then(currentAcctBalResEntity1.getBody().getErrorCode()).isEqualTo(ErrorMessages.ATM_OUT_OF_CASH.getCode());
		then(currentAcctBalResEntity1.getBody().getErrorReason())
				.isEqualTo(ErrorMessages.ATM_OUT_OF_CASH.getDescription());
		
		Map<Integer, Integer> notesToLoad = new HashMap<>();
		notesToLoad.put(50, atmHealthResEntity.getBody().getNote50Counter());
		notesToLoad.put(20, atmHealthResEntity.getBody().getNote20Counter());
		notesToLoad.put(10, atmHealthResEntity.getBody().getNote10Counter());
		notesToLoad.put(5, atmHealthResEntity.getBody().getNote5Counter());
		ResponseEntity<ATMNotesDispenser> entity = this.testRestTemplate.postForEntity(
				"http://localhost:" + this.port + "/atm/loadmoney/" + validAdminUser + "/" + validAdminPassword,
				notesToLoad, ATMNotesDispenser.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(entity.getBody().getNote50Counter()).isEqualTo(notesToLoad.get(50));
		then(entity.getBody().getNote20Counter()).isEqualTo(notesToLoad.get(20));
		then(entity.getBody().getNote10Counter()).isEqualTo(notesToLoad.get(10));
		then(entity.getBody().getNote5Counter()).isEqualTo(notesToLoad.get(5));
	}

	@Test
	public void testDepositMoneyToValidAccount() {
		String validAcctNum = "123456789";
		String validDepositAmount = "100";
		ResponseEntity<SuccessDetails> entity = this.testRestTemplate.postForEntity(
				"http://localhost:" + this.port + "/bank/depositmoney/" + validDepositAmount + "/" + validAcctNum, null,
				SuccessDetails.class);
		then(entity.getBody().getSuccessCode()).isEqualTo(SuccessMessages.DEPOSIT_SUCCESS.getCode());
		then(entity.getBody().getSuccessMessage())
				.isEqualTo(String.format(SuccessMessages.DEPOSIT_SUCCESS.getDescription(), validAcctNum));
		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void testDepositMoneyToInValidAccount() {
		String invalidAcctNum = "9877655443287";
		String validDepositAmount = "100";
		ResponseEntity<ErrorDetails> entity = this.testRestTemplate.postForEntity(
				"http://localhost:" + this.port + "/bank/depositmoney/" + validDepositAmount + "/" + invalidAcctNum,
				null, ErrorDetails.class);
		then(entity.getBody().getErrorCode()).isEqualTo(ErrorMessages.INVALID_ACCT_NUMBER.getCode());
		then(entity.getBody().getErrorReason()).isEqualTo(ErrorMessages.INVALID_ACCT_NUMBER.getDescription());
		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDepositMoneyUsinngNegativeAmount() {
		String validAcctNum = "123456789";
		String invalidDepositAmount = "-100";
		ResponseEntity<SuccessDetails> entity = this.testRestTemplate.postForEntity(
				"http://localhost:" + this.port + "/bank/depositmoney/" + invalidDepositAmount + "/" + validAcctNum,
				null, SuccessDetails.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDepositMoneyUsinngZeroAmount() {
		String validAcctNum = "123456789";
		String invalidDepositAmount = "0";
		ResponseEntity<SuccessDetails> entity = this.testRestTemplate.postForEntity(
				"http://localhost:" + this.port + "/bank/depositmoney/" + invalidDepositAmount + "/" + validAcctNum,
				null, SuccessDetails.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testCheckATMHealthUsingValidAdminCredentials() {
		String validAdminUser = "admin";
		String validAdminPassword = "P@55w0rd";
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + "/atm/healthCheck/" + validAdminUser + "/" + validAdminPassword,
				Map.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void testCheckATMHealthUsingInValidAdminCredentials() {
		String inValidAdminUser = "admin233";
		String inValidAdminPassword = "P@44";
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + "/atm/healthCheck/" + inValidAdminUser + "/" + inValidAdminPassword,
				Map.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testCheckATMHealthUsingEmptyCredentials() {
		String inValidAdminUser = "";
		String inValidAdminPassword = "";
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = this.testRestTemplate.getForEntity(
				"http://localhost:" + this.port + "/atm/healthCheck/" + inValidAdminUser + "/" + inValidAdminPassword,
				Map.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testLoadMoneyToATMUsingValidCredential() {
		String validAdminUser = "admin";
		String validAdminPassword = "P@55w0rd";
		Map<Integer, Integer> notesToLoad = new HashMap<>();
		notesToLoad.put(50, 2);
		notesToLoad.put(20, 2);
		notesToLoad.put(10, 1);
		notesToLoad.put(5, 2);
		ResponseEntity<ATMNotesDispenser> entity = this.testRestTemplate.postForEntity(
				"http://localhost:" + this.port + "/atm/loadmoney/" + validAdminUser + "/" + validAdminPassword,
				notesToLoad, ATMNotesDispenser.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void testLoadMoneyToATMwithoutFewDenominations() {
		String validAdminUser = "admin";
		String validAdminPassword = "P@55w0rd";
		Map<Integer, Integer> notesToLoad = new HashMap<>();
		notesToLoad.put(10, 1);
		notesToLoad.put(5, 2);
		ResponseEntity<ATMNotesDispenser> entity = this.testRestTemplate.postForEntity(
				"http://localhost:" + this.port + "/atm/loadmoney/" + validAdminUser + "/" + validAdminPassword,
				notesToLoad, ATMNotesDispenser.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void testLoadMoneyToATMwithEmptyDenominations() {
		String validAdminUser = "admin";
		String validAdminPassword = "P@55w0rd";
		Map<Integer, Integer> notesToLoad = new HashMap<>();
		ResponseEntity<ATMNotesDispenser> entity = this.testRestTemplate.postForEntity(
				"http://localhost:" + this.port + "/atm/loadmoney/" + validAdminUser + "/" + validAdminPassword,
				notesToLoad, ATMNotesDispenser.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void testLoadMoneyToATMUsingInValidCredential() {
		String invalidAdminUser = "admin24234";
		String invalidAdminPassword = "P@43245452";
		Map<Integer, Integer> notesToLoad = new HashMap<>();
		notesToLoad.put(50, 2);
		notesToLoad.put(20, 2);
		notesToLoad.put(10, 1);
		notesToLoad.put(5, 2);
		ResponseEntity<ErrorDetails> entity = this.testRestTemplate.postForEntity(
				"http://localhost:" + this.port + "/atm/loadmoney/" + invalidAdminUser + "/" + invalidAdminPassword,
				notesToLoad, ErrorDetails.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
