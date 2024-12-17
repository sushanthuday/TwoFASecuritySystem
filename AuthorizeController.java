package se.mikka.fa.springboot2factorauth.controller;

import org.jboss.aerogear.security.otp.Totp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.mikka.fa.springboot2factorauth.controller.restdata.Login;

@RestController
@RequestMapping(
        path = "api/v1",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class AuthorizeController {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeController.class);
	
	private static final String USERNAME = "hello@mikka.se";
	private static final String PASSWORD = "asdfgh";
	private static final String SECRET_CODE = "AHLOJ6UVMPRFTLJW"; //Safely save in the server without exposing it
	
	@CrossOrigin(origins = "http://localhost:3000")
	@RequestMapping(
			path = "/authorize",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
	public ResponseEntity<Void> authorize(@RequestBody Login login) {
		LOGGER.info("Login = {}", login);
		
		if (!USERNAME.equals(login.getUsername()) || !PASSWORD.equals(login.getPassword())) {
			LOGGER.info("Wrong username or password");
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
	
		//Get verification code based on SECRET_CODE
		Totp totp = new Totp(SECRET_CODE);
		String verificationCode = login.getVerificationCode();
		if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
			LOGGER.info("Wrong verification code");
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		
		LOGGER.info("Logged in sucessfully!");
		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
	
}