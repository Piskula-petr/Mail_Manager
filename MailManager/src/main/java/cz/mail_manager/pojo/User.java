package cz.mail_manager.pojo;

import javax.validation.constraints.NotBlank;

public class User {
	
	@NotBlank
	private String email;
	private String emailServer;
	
	@NotBlank
	private String password;
	
// Bezparametrový konstruktor ////////////////////////////////////////////////////////////////////////
	
	public User() {
		
	}

// Gettery + Settery /////////////////////////////////////////////////////////////////////////////////
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailServer() {
		return emailServer;
	}

	public void setEmailServer(String emailServer) {
		this.emailServer = emailServer;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
