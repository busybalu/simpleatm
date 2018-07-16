package com.jnj.atm.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author BALU RAMAMOORTHY
 *
 */
@SpringBootApplication(scanBasePackages = { "com.jnj" })
public class SimpleAtmApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleAtmApplication.class, args);
	}
}
