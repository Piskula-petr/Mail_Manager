package cz.mail_manager.server_connection;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.stereotype.Component;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.SortTerm;

import cz.mail_manager.pojo.AttachedFile;
import cz.mail_manager.pojo.Email;
import cz.mail_manager.pojo.User;

@Component
public class ReceivedEmails {

	// Přihlašovací parametry
	private String email;
	private String password;
	
	// Celkový počet emailů
	private int messageCount;
	
	private Session emailSession;
	
	
	/**
	 * Konstruktor
	 */
	public ReceivedEmails() {
		
		Properties properties = new Properties();
		properties.put("mail.store.protocol", "imap");
		
		emailSession = Session.getDefaultInstance(properties);
	}
	
	
	/**
	 * 	Získání základních informací o emailech
	 * 
	 * 	@param folderType - typ složky ( INBOX, sent, drafts, newsletters, archive, spam, trash )
	 *  @param lastIndex - index posledního načteného emailu
	 *  
	 * 	@return - vrací List základních informácí o emailech
	 */
	public List<Email> getEmailsHeader(String folderType, int lastIndex) {
		
		List<Email> emails = new ArrayList<>();
		
		try {

			Store emailStore = emailSession.getStore("imap");
			emailStore.connect("imap.seznam.cz", email, password);
			
			IMAPFolder emailFolder = (IMAPFolder) emailStore.getFolder(folderType);
			emailFolder.open(Folder.READ_ONLY);
			
			// List seřazených emailů od nejnovějších
			List<Message> messages = Arrays.asList(emailFolder.getSortedMessages(new SortTerm[] {SortTerm.ARRIVAL}));
			Collections.reverse(messages);
			
			// Celkový počet emailů
			messageCount = messages.size();
			
			// koncový index emailu
			int endIndex = lastIndex + 15;
			
			if (endIndex > messageCount) {
				
				endIndex = messageCount;
			}
			
			for (Message message : messages.subList(lastIndex, endIndex)) {
				
				Email email = new Email();
				
// Status přečtení /////////////////////////////////////////////////////////////////////////////////
				
				email.setIsSeen(message.isSet(Flag.SEEN));

// Odesilatel //////////////////////////////////////////////////////////////////////////////////////
				
				InternetAddress internetAddress = new InternetAddress(message.getFrom()[0].toString());
				
				if (internetAddress.getPersonal() == null) {
					
					email.setFrom(internetAddress.getAddress());
					
				} else email.setFrom(internetAddress.getPersonal());
				
// Předmět /////////////////////////////////////////////////////////////////////////////////////////
				
				email.setSubject(message.getSubject());
				
// Datum + čas odeslání ////////////////////////////////////////////////////////////////////////////
					
				LocalDateTime localDateTime = message.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				email.setSentDate(localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
				email.setSentTime(localDateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
			
				emails.add(email);
			}
			
			emailFolder.close(false);
			emailStore.close();
			
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		
		} catch (MessagingException e) {
			e.printStackTrace();
		
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return emails;
	}
	
	
	/**
	 * 	Obnovení nenačtených emailů
	 * 
	 * 	@param folderType - typ složky ( INBOX, sent, drafts, newsletters, archive, spam, trash )
	 * 	@param sentDate - datum odeslání emailu
	 * 
	 * 	@return vrací List nových emailů
	 */
	public List<Email> refreshEmailsHeader(String folderType, String sentDate) {
		
		List<Email> emails = new ArrayList<>();
		
		try {
			
			Store emailStore = emailSession.getStore("imap");
			emailStore.connect("imap.seznam.cz", email, password);
			
			IMAPFolder emailFolder = (IMAPFolder) emailStore.getFolder(folderType);
			emailFolder.open(Folder.READ_ONLY);
			
			// List seřazených emailů od nejnovějších
			List<Message> messages = Arrays.asList(emailFolder.getSortedMessages(new SortTerm[] {SortTerm.ARRIVAL}));
			Collections.reverse(messages);

			for (Message message : messages) {
				
				LocalDateTime oldMessageSentDate = LocalDateTime.parse(sentDate, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
				LocalDateTime newMessageSentDate = message.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().withSecond(0);
				
				if (oldMessageSentDate.isBefore(newMessageSentDate)) {
					
					Email email = new Email();

// Přečtený ////////////////////////////////////////////////////////////////////////////////////////
				
					email.setIsSeen(message.isSet(Flag.SEEN));

// Odesilatel //////////////////////////////////////////////////////////////////////////////////////
				
					InternetAddress internetAddress = new InternetAddress(message.getFrom()[0].toString());
					
					if (internetAddress.getPersonal() == null) {
						
						email.setFrom(internetAddress.getAddress());
						
					} else email.setFrom(internetAddress.getPersonal());
				
// Předmět /////////////////////////////////////////////////////////////////////////////////////////
				
					email.setSubject(message.getSubject());
				
// Datum + čas odeslání ////////////////////////////////////////////////////////////////////////////
				
					email.setSentDate(newMessageSentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
					email.setSentTime(newMessageSentDate.format(DateTimeFormatter.ofPattern("HH:mm")));
					
					emails.add(email);
					
				} else break;
			}
			
			emailFolder.close(false);
			emailStore.close();
			
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		
		} catch (MessagingException e) {
			e.printStackTrace();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return emails;
	}
	
	
	/**
	 * 	Získání detailu o emailu
	 * 
	 * 	@param folderType - typ složky ( INBOX, sent, drafts, newsletters, archive, spam, trash )
	 * 	@param detailIndex - index emailu
	 * 
	 * 	@return - vrací detailní informace o emailu
	 */
	public Email getEmailDetail(String folderType, int detailIndex) {
		
		Email email = new Email();
		
		try {

			Store emailStore = emailSession.getStore("imap");
			emailStore.connect("imap.seznam.cz", this.email, password);
			
			IMAPFolder emailFolder = (IMAPFolder) emailStore.getFolder(folderType);
			emailFolder.open(Folder.READ_WRITE);
			
			// List seřazených emailů od nejnovějších
			List<Message> messages = Arrays.asList(emailFolder.getSortedMessages(new SortTerm[] {SortTerm.ARRIVAL}));
			Collections.reverse(messages);
			
			// Vybraný email
			MimeMessage message = (MimeMessage) messages.get(detailIndex);
			
			// Změní status emailu na přečtený
			if (!message.isSet(Flag.SEEN)) {
				
				message.setFlag(Flag.SEEN, true);
			}
			
// Předmět /////////////////////////////////////////////////////////////////////////////////////////
			
			email.setSubject(message.getSubject());
			
			MimeMessageParser mimeMessageParser = new MimeMessageParser((MimeMessage) message);
			mimeMessageParser.parse();
			
// Odesilatel //////////////////////////////////////////////////////////////////////////////////////
			
			String addressFrom = "";
			InternetAddress internetAddressFrom = new InternetAddress(message.getFrom()[0].toString());
			
			if (internetAddressFrom.getPersonal() != null) {
				addressFrom = addressFrom + internetAddressFrom.getPersonal() + " ";
			}
			
			addressFrom = addressFrom + "&lt;" + internetAddressFrom.getAddress().toLowerCase() + "&gt;; ";
			email.setFrom(addressFrom);
			
// Adresy příjemců //////////////////////////////////////////////////////////////////////////////////
			
			String addressTO = "";
			
			for (Address address : mimeMessageParser.getTo()) {
				
				InternetAddress internetAddressTO = new InternetAddress(address.toString());
				
				if (internetAddressTO.getPersonal() != null) {
					addressTO = addressTO + internetAddressTO.getPersonal() + " ";
				}
				
				addressTO = addressTO + "&lt;" + internetAddressTO.getAddress().toLowerCase() + "&gt;; ";
				
			}
			email.setRecipientsTO(addressTO);
			
// Adresy kopií /////////////////////////////////////////////////////////////////////////////////////
			
			String addressCC = "";
			
			for (Address address : mimeMessageParser.getCc()) {
				
				InternetAddress internetAddressCC = new InternetAddress(address.toString());
				
				if (internetAddressCC.getPersonal() != null) {
					addressCC = addressCC + internetAddressCC.getPersonal() + " ";
				}
				
				addressCC = addressCC + "&lt;" + internetAddressCC.getAddress().toLowerCase() + "&gt;; ";
			}
			email.setRecipientsCC(addressCC);
			
// Datum + čas odeslání ////////////////////////////////////////////////////////////////////////////

			LocalDateTime localDateTime = message.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			email.setSentDate(localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
			email.setSentTime(localDateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
		
// Obsah ///////////////////////////////////////////////////////////////////////////////////////////
			
			String contentHTML = contentProcessing(mimeMessageParser);
			email.setContent(contentHTML);
			
// List přiložených souborů ////////////////////////////////////////////////////////////////////////
			
			if (mimeMessageParser.hasAttachments()) {
				
				List<AttachedFile> attachedFiles = new ArrayList<>();
				
				int index = 1;
				for (DataSource dataSource : mimeMessageParser.getAttachmentList()) {
					
					// Nahrazení Content ID za URI schema
					if (contentHTML.contains("cid:")) {
						
						// Index začátku atributu CID
						int startIndexCID = contentHTML.indexOf("cid:");
						
						// Typ uvozovek ('', "")
						String quotationMarksType = String.valueOf(contentHTML.charAt(startIndexCID - 1));
						
						// Index konce atributu CID (koncových uvozovek)
						int endIndexCID = startIndexCID + contentHTML.substring(startIndexCID).indexOf(quotationMarksType);
						
						String CID = contentHTML.substring(startIndexCID, endIndexCID);
						
						// Vytvoření kódování Base64
						byte[] bytes = IOUtils.toByteArray(dataSource.getInputStream());
						String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
						
						String dataURI = "";
						dataURI = "data:" + dataSource.getContentType() + ";base64," + base64;
						
						contentHTML = contentHTML.replace(CID, dataURI);
					}
					
					AttachedFile attachedFile = new AttachedFile();
					
					// Nastavení defaultního jména souboru, pokud není uvedené
					if (dataSource.getName() != null) {
						
						attachedFile.setFileName(dataSource.getName().toLowerCase());
						
					} else attachedFile.setFileName("soubor_" + index);
					
					attachedFile.setFileInputStream(dataSource.getInputStream());
					
					// Velikost souboru
					float sizeKB = dataSource.getInputStream().available();
					sizeKB = Math.round(sizeKB / 1024);
					
					// Přidání jednotky k velikosti souboru
					if (sizeKB < 1000) {
						
						attachedFile.setFileSize(String.valueOf((int) sizeKB) + " kB");
						
					} else attachedFile.setFileSize(String.valueOf((int) sizeKB / 1024) + " MB");
					
					attachedFiles.add(attachedFile);
					index++;
				}
				
				email.setContent(contentHTML);
				email.setAttachedFiles(attachedFiles);
			}
			
			emailFolder.close(false);
			emailStore.close();
			
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		
		} catch (MessagingException e) {
			e.printStackTrace();
		
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return email;
	}

	
	/**
	 * 	Přesunutí emailu do jiné složky
	 * 
	 * 	@param folderType - typ složky ( INBOX, sent, drafts, newsletters, archive, spam, trash )
	 * 	@param detailIndex - index emailu
	 * 	@param folder - název nové složky
	 */
	public void move(String folderType, int detailIndex, String folder) {
		
		try {
			
			// Změna složky, pokud není stejná jako aktuální
			if (folderType != folder) {
				
				Store emailStore = emailSession.getStore("imap");
				emailStore.connect("imap.seznam.cz", email, password);
				
				// Aktuální složka
				IMAPFolder emailFolder = (IMAPFolder) emailStore.getFolder(folderType);
				emailFolder.open(Folder.READ_WRITE);
				
				// Nová složka
				IMAPFolder newFolder = (IMAPFolder) emailStore.getFolder(folder);
				newFolder.open(Folder.HOLDS_MESSAGES);
				
				// List seřazených emailů od nejnovějších
				List<Message> messages = Arrays.asList(emailFolder.getSortedMessages(new SortTerm[] {SortTerm.ARRIVAL}));
				Collections.reverse(messages);
				
				// Email k přesunutí
				Message message = messages.get(detailIndex);
				
				// Nakopírování zprávy do nové složky
				emailFolder.copyMessages(new Message[] {message}, newFolder);
				
				// Smazání původní zprávy
				message.setFlag(Flag.DELETED, true);
				
				newFolder.close(true);
				emailFolder.close(true);
				emailStore.close();
			} 
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 	Smazání emailu (přesun do koše)
	 * 
	 * 	@param folderType - typ složky ( INBOX, sent, drafts, newsletters, archive, spam, trash )
	 * 	@param detailIndex - index emailu
	 */
	public void delete(String folderType, int detailIndex) {
		
		try {
			
			Store emailStore = emailSession.getStore("imap");
			emailStore.connect("imap.seznam.cz", email, password);
			
			// Aktuální složka
			IMAPFolder emailFolder = (IMAPFolder) emailStore.getFolder(folderType);
			emailFolder.open(Folder.READ_WRITE);
			
			// List seřazených emailů od nejnovějších
			List<Message> messages = Arrays.asList(emailFolder.getSortedMessages(new SortTerm[] {SortTerm.ARRIVAL}));
			Collections.reverse(messages);
			
			// Email ke smazání (přesunu do koše)
			Message message = messages.get(detailIndex);
			
			// Složka koše
			IMAPFolder trashFolder = (IMAPFolder) emailStore.getFolder("trash");
			trashFolder.open(Folder.HOLDS_MESSAGES);
			
			// Přesunutí emailu, pokud email není ve složce spam, nebo koš
			if (!folderType.equals("spam") || !folderType.equals("trash")) {
				
				// Nakopírování emailu do složky koše
				emailFolder.copyMessages(new Message[] {message}, trashFolder);
			}
			
			// Smazání původního emailu
			message.setFlag(Flag.DELETED, true);
			
			emailFolder.close(true);
			trashFolder.close(true);
			emailStore.close();
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 	Přednastavení emailu (odpověď, odpověď všem, přeposlat)
	 * 
	 * 	@param folderType - typ složky ( INBOX, sent, drafts, newsletters, archive, spam, trash )
	 * 	@param detailIndex - index emailu
	 * 	@param replyType - typ odpovědi ( REPLY, REPLY_ALL, RESEND )
	 * 
	 * 	@return - vrací předchystaný email
	 */
	public Email reply(String folderType, int detailIndex, ReplyType replyType) {
		
		Email email = new Email();
		
		try {
			
			Store emailStore = emailSession.getStore("imap");
			emailStore.connect("imap.seznam.cz", this.email, password);
			
			Folder emailFolder = emailStore.getFolder(folderType);
			emailFolder.open(Folder.READ_ONLY);
			
			Message message = emailFolder.getMessage(emailFolder.getMessageCount() - detailIndex);
			
			MimeMessageParser mimeMessageParser = new MimeMessageParser((MimeMessage) message);
			mimeMessageParser.parse();
			
// Odesilatel ///////////////////////////////////////////////////////////////////////////////////////
			
			String addressFrom = "";
			
			InternetAddress internetAddressFrom = new InternetAddress(message.getFrom()[0].toString());
			
			if (internetAddressFrom.getPersonal() != null) {
				addressFrom = addressFrom + internetAddressFrom.getPersonal() + " ";
			}
			
			addressFrom = addressFrom + "<" + internetAddressFrom.getAddress().toLowerCase() + ">;";
			email.setFrom(addressFrom);
			
// Příjemce /////////////////////////////////////////////////////////////////////////////////////////
			
			String addressTO = "";
			
			for (Address address : mimeMessageParser.getTo()) {
				
				InternetAddress internetAddressTO = new InternetAddress(address.toString());
				
				if (internetAddressTO.getPersonal() != null) {
					addressTO = addressTO + internetAddressTO.getPersonal() + " ";
				}
				
				addressTO = addressTO + "<" + internetAddressTO.getAddress().toLowerCase() + ">; ";
			}
			email.setRecipientsTO(addressTO);
			
// Kopie ////////////////////////////////////////////////////////////////////////////////////////////
			
			String addressCC = "";
			
			for (Address address : mimeMessageParser.getCc()) {
				
				InternetAddress internetAddressCC = new InternetAddress(address.toString());
				
				if (internetAddressCC.getPersonal() != null) {
					addressCC = addressCC + internetAddressCC.getPersonal() + " ";
				}
				
				addressCC = addressCC + "<" + internetAddressCC.getAddress().toLowerCase() + ">; ";
			}
			email.setRecipientsCC(addressCC);
			
// Předmět //////////////////////////////////////////////////////////////////////////////////////////
			
			if (replyType.equals(ReplyType.REPLY) || replyType.equals(ReplyType.REPLY_ALL)) {
				
				email.setSubject("RE: " + message.getSubject());
				
			} else if (replyType.equals(ReplyType.RESEND)) {
				
				email.setSubject("Fwd: " + message.getSubject());
			}
			
// Obsah ////////////////////////////////////////////////////////////////////////////////////////////
			
			String contentHTML = contentProcessing(mimeMessageParser);
			
			// Datum odeslání
			LocalDateTime localDateTime = message.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			
// Změna na HTML entity /////////////////////////////////////////////////////////////////////////////
			
			String from = "Odesilatel: " + email.getFrom().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			
			String to = "";
			
			if (!email.getRecipientsTO().isEmpty()) {
				
				to = "Příjemce: " + email.getRecipientsTO().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			}

			String cc = "";
			
			if (!email.getRecipientsCC().isEmpty()) {
				
				cc = "Kopie: " + email.getRecipientsCC().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			}
			
// Zpráva o původním emailu /////////////////////////////////////////////////////////////////////////
			
			String content = "<br><br>"
						   + "────────── Původní email ────────── <br>"
						   + from + "<br>"
						   + to + "<br>"
						   + cc + "<br>"
						   + "Datum: " + localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + "<br>"
						   + "Předmět: " + message.getSubject() + "<br>"
						   + "─────────────────────────────── <br>"
						   + "<br>"
						   + contentHTML;
			
			email.setContent(content);
			
		} catch (MessagingException e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return email;
	}
	
	
	/**
	 * 	@return - vrací celkový počet emailů
	 */
	public int getMessageCount() {
		
		return messageCount;
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
	 * 	Zpracování obsahu emailu, podle jeho typu
	 * 
	 * 	@param mimeMessageParser
	 * 
	 * 	@return -  vrací upravený obsah emailu
	 */
	private String contentProcessing(MimeMessageParser mimeMessageParser) throws MessagingException {
		
		String contentHTML = "";
		
		// Zpracování textových emailů
		if (mimeMessageParser.getMimeMessage().isMimeType("text/plain")) {
		
			String[] lines = mimeMessageParser.getPlainContent().split("\n");
			
			for (int i = 0; i < lines.length; i++) {
				
				// Odřádkování
				if (lines[i].isEmpty()) {
					
					contentHTML = contentHTML + "<br><br>";
					
				// Vložení řádku do div tagu
				} else contentHTML = contentHTML + "<div>" + lines[i] + "</div>";
			}
			
		// Zpracování kombinovaných emailů + emailů s HTML tagy
		} else if (mimeMessageParser.getMimeMessage().isMimeType("multipart/*") || 
				   mimeMessageParser.getMimeMessage().isMimeType("text/html")) {
			
			// Obsah emailu jen v tagu <body>
			if (mimeMessageParser.getHtmlContent().contains("<body") && mimeMessageParser.getHtmlContent().contains("/body>") ) {
				
				int starIndextBody = mimeMessageParser.getHtmlContent().indexOf("<body");
				int endIndexBody = mimeMessageParser.getHtmlContent().lastIndexOf("/body>");
				
				// začátek obsahu emailu <body>, konec obsahu emailu včetně </body>
				// + 6 = délka koncového tagu /body>
				contentHTML = mimeMessageParser.getHtmlContent().substring(starIndextBody, endIndexBody + 6);
				
			} else contentHTML = mimeMessageParser.getHtmlContent().toString();
		}
		
		return contentHTML;
	}
	
}
