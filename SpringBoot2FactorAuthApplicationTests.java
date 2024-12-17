package se.mikka.fa.springboot2factorauth;

import org.jboss.aerogear.security.otp.api.Base32;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBoot2FactorAuthApplicationTests {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(SpringBoot2FactorAuthApplicationTests.class);

	@Test
	public void contextLoads() {
	}
	
	@Test
	public void generateRandomBase32AsTOPTSecretCode() {
		LOGGER.info("Generated random Base32: " + Base32.random());
	}
	

}
