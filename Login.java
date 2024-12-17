package se.mikka.fa.springboot2factorauth.controller.restdata;

public class Login {
	
	private String username;
	private String password;
	private String verificationCode;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getVerificationCode() {
		return verificationCode;
	}
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	
	@Override
	public String toString() {
		return "Login [username=" + username + ", password=" + password + ", verificationCode=" + verificationCode
				+ "]";
	}
	
}
