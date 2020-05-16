package cz.mail_manager.beans;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// Atributy uživatele
	private String email;
	private String emailServer;
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
