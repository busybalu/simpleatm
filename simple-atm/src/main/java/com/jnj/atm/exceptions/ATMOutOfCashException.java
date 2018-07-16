package com.jnj.atm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author BALU RAMAMOORTHY
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ATMOutOfCashException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
