package cz.mail_manager.server_connection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.multipart.MultipartFile;

import cz.mail_manager.beans.Email;
import cz.mail_manager.beans.User;

public class NewEmail {

	private String email;
	private String password;
	private String smtpServer;
	private Session emailSession;
	
// Konstruktor //////////////////////////////////////////////////////////////////////////////////////
	
	public NewEmail(User user) {
		
		// Nastavení parametrů připojení
		email = user.getEmail() + user.getEmailServer();
		password = user.getPassword();

		// Seznam.cz
		smtpServer = "smtp.seznam.cz";
		
		try {
			
// Autorizace uživatele /////////////////////////////////////////////////////////////////////////////
			
			Properties properties = new Properties();
			properties.put("mail.smtp.host", smtpServer);
		    properties.put("mail.smtp.auth", "true");
			
		    emailSession = Session.getInstance(properties, new Authenticator() {
		    	
		    	@Override
		    	protected PasswordAuthentication getPasswordAuthentication() {
		    		
		    		return new PasswordAuthentication(email, password);
		    	}
			});

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 	Odeslání emailu
	 * 
	 * 	@param email - email k odeslání
	 * 	@param files - List souborů k odeslání
	 */
	public void send(Email email, List<MultipartFile> files) {
		
		try {
			
			MimeMessage message = new MimeMessage(emailSession);
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
			
// Adresy odesilatelů ///////////////////////////////////////////////////////////////////////////////
			
			String[] splitAdressFrom = email.getFrom().split(" ");
			String personalFrom = "";
			String addressFrom = "";
			
			for (int i = 0; i < splitAdressFrom.length; i++) {
				
				// Vybrání řetězců obsahující symbol '@'
				if (!splitAdressFrom[i].contains("@")) {
					
					// Přidání mezery mezi jméno a adresu
					if (!personalFrom.isEmpty()) {
						
						personalFrom = personalFrom + " " + splitAdressFrom[i];
						
					} else personalFrom = personalFrom + splitAdressFrom[i];
					
				} else addressFrom = splitAdressFrom[i];
			}
			
			messageHelper.setFrom(new InternetAddress(addressFrom, personalFrom));
			
// Adresy příjemců //////////////////////////////////////////////////////////////////////////////////
			
			String[] addressesTO = email.getRecipientsTO().split(";|,");
			
			for (int i = 0; i < addressesTO.length; i++) {
				
				String[] splitAdressTO = addressesTO[i].split(" ");
				String personalTO = "";
				String addressTO = "";
				
				for (int j = 0; j < splitAdressTO.length; j++) {
					
					// Vybrání řetězců obsahující symbol '@'
					if (!splitAdressTO[j].contains("@")) {
						
						// Přidání mezery mezi jméno a adresu
						if (!personalTO.isEmpty()) {
							
							personalTO = personalTO + " " + splitAdressTO[j];
							
						} else personalTO = personalTO + splitAdressTO[j];
						
					} else addressTO = splitAdressTO[j];
				}
				
				if (!addressTO.isEmpty()) {
					
					messageHelper.setTo(new InternetAddress(addressTO, personalTO));
				}
			}
			
// Adresy v copii ///////////////////////////////////////////////////////////////////////////////////
			
			if (!email.getRecipientsCC().isEmpty()) {
				
				String[] addressesCC = email.getRecipientsCC().split(";|,");
				
				for (int i = 0; i < addressesCC.length; i++) {
					
					String[] splitAdressCC = addressesCC[i].split(" ");
					String personalCC = "";
					String addressCC = "";
					
					for (int j = 0; j < splitAdressCC.length; j++) {
						
						// Vybrání řetězců obsahující symbol '@'
						if (!splitAdressCC[j].contains("@")) {
							
							// Přidání mezery mezi jméno a adresu
							if (!personalCC.isEmpty()) {
								
								personalCC = personalCC + " " + splitAdressCC[j];
								
							} else personalCC = personalCC + splitAdressCC[j];
							
						} else addressCC = splitAdressCC[j];
					}
					
					if (!addressCC.isEmpty()) {
						
						messageHelper.setCc(new InternetAddress(addressCC, personalCC));
					}
				}
			}

// Předmět //////////////////////////////////////////////////////////////////////////////////////////
			
			messageHelper.setSubject(email.getSubject());
			
// Obsah emailu /////////////////////////////////////////////////////////////////////////////////////
			
			messageHelper.setText(email.getContent(), true);
			
// Příloha //////////////////////////////////////////////////////////////////////////////////////////

			for (MultipartFile file : files) {
				
				if (!file.isEmpty()) {
					
					InputStreamSource inputStreamSource = new ByteArrayResource(file.getBytes());
					messageHelper.addAttachment(new String(file.getOriginalFilename().getBytes("iso-8859-1"), ("UTF-8")), inputStreamSource, file.getContentType());
				}
			}
			
			// Odeslání emailu
			Transport.send(message);
			
		} catch (MessagingException e) {
			e.printStackTrace();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
