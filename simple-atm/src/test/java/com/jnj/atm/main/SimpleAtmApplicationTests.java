package com.jnj.atm.main;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author BALU RAMAMOORTHY
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleAtmApplicationTests {

	@LocalServerPort
	private int port;

	@Value("${local.management.port}")
	private int mgt;
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Test
    public void contextLoads() {
		
    }
	
	@Test
	public void shouldReturn200WhenSendingRequestToController() throws Exception {
		String testAcctNum = "4567890987";
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = this.testRestTemplate.getForEntity("http://localhost:" + this.port + "/atm/greetuser/"+ testAcctNum ,
				Map.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}
