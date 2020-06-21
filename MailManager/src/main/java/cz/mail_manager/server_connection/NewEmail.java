package cz.mail_manager.server_connection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.sun.mail.imap.IMAPFolder;

import cz.mail_manager.pojo.Email;
import cz.mail_manager.pojo.User;

@Component
public class NewEmail {

	// Přihlašovací parametry
	private String email;
	private String password;
	
	private Session emailSession;
	
// Konstruktor //////////////////////////////////////////////////////////////////////////////////////
	
	public NewEmail() {
		
		try {
			
			Properties properties = new Properties();
			properties.put("mail.smtp.host", "smtp.seznam.cz");
		    properties.put("mail.smtp.auth", "true");
			
		    emailSession = Session.getInstance(properties, new Authenticator() {
		    	
		    	// Autorizace uživatele
		    	
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
			
// Adresa odesilatele ///////////////////////////////////////////////////////////////////////////////
			
			messageHelper.setFrom(emailAddressToInternetAddress(email.getFrom()));
			
// Adresy příjemců //////////////////////////////////////////////////////////////////////////////////
			
			String[] addressesTO = email.getRecipientsTO().split(";|,");
			
			for (int i = 0; i < addressesTO.length; i++) {
				
				messageHelper.addTo(emailAddressToInternetAddress(addressesTO[i]));
			}
			
// Adresy v copii ///////////////////////////////////////////////////////////////////////////////////
			
			if (!email.getRecipientsCC().isEmpty()) {
				
				String[] addressesCC = email.getRecipientsCC().split(";|,");
				
				for (int i = 0; i < addressesCC.length; i++) {

					messageHelper.addCc(emailAddressToInternetAddress(addressesCC[i]));
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
	
	/**
	 * 	Přesunutí emailu do složky rozepsané
	 * 
	 * 	@param email - email k přesunu
	 */
	public void moveToDraft(Email email) {
		
		try {
			
			Store emailStore = emailSession.getStore("imap");
			emailStore.connect("imap.seznam.cz", this.email, password);
			
			MimeMessage message = new MimeMessage(emailSession);
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			
// Adresa odesilatele ///////////////////////////////////////////////////////////////////////////////

			messageHelper.setFrom(emailAddressToInternetAddress(email.getFrom()));
			
// Adresy příjemců //////////////////////////////////////////////////////////////////////////////////

			String[] addressesTO = email.getRecipientsTO().split(";|,");
			
			for (int i = 0; i < addressesTO.length; i++) {
				
				messageHelper.addTo(emailAddressToInternetAddress(addressesTO[i]));
			}
			
// Adresy v copii ///////////////////////////////////////////////////////////////////////////////////

			if (email.getRecipientsCC() != null) {
				
				String[] addressesCC = email.getRecipientsCC().split(";|,");
				
				for (int i = 0; i < addressesCC.length; i++) {

					messageHelper.addCc(emailAddressToInternetAddress(addressesCC[i]));
				}
			}

// Předmět //////////////////////////////////////////////////////////////////////////////////////////
			
			messageHelper.setSubject(email.getSubject());
			
// Obsah emailu /////////////////////////////////////////////////////////////////////////////////////
			
			messageHelper.setText(email.getContent(), true);
			
			// Složka rozepsané
			IMAPFolder draftsFolder = (IMAPFolder) emailStore.getFolder("drafts");
			draftsFolder.open(Folder.HOLDS_MESSAGES);
			
			// Přidání emailu do složky
			draftsFolder.addMessages(new Message[] {message});
			
			draftsFolder.close(true);
			emailStore.close();
			
		} catch (MessagingException e) {
			e.printStackTrace();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
		}
	}
	
	/**
	 * 	Nastavení přihlašovacích parametrů uživatele
	 * 
	 * 	@param user - aktuálně přihlášený uživatel
	 */
	public void setUser(User user) {
		
		email = user.getEmail() + user.getEmailServer();
		password = user.getPassword();
	}

	/**
	 * 	Rozložení emailové adresy na jméno + adresu
	 * 
	 * @param emailAddress - emailová adresa ( Name Surname email@email.com; )
	 * 
	 * @return - vrací rozloženou adresu 
	 */
	private InternetAddress emailAddressToInternetAddress(String emailAddress) throws UnsupportedEncodingException {
		
		String[] splitAdress = emailAddress.split(" ");
		String personal = "";
		String address = "";
		
		for (int i = 0; i < splitAdress.length; i++) {
			
			// Vybrání řetězců obsahující symbol '@'
			if (!splitAdress[i].contains("@")) {
				
				// Přidání mezery mezi jméno a adresu
				if (!personal.isEmpty()) {
					
					personal = personal + " " + splitAdress[i];
					
				} else personal = personal + splitAdress[i];
				
			} else address = splitAdress[i];
		}
		
		return new InternetAddress(address, personal);
	}
	
	/**
	 * 	@return - vrací emailovou adresu uživatele
	 */
	public String getEmail() {
		return email;
	}
	
}
