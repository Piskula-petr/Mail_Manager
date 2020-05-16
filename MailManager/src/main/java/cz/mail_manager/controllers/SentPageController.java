package cz.mail_manager.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import cz.mail_manager.beans.Email;
import cz.mail_manager.beans.User;
import cz.mail_manager.server_connection.NewEmail;
import cz.mail_manager.server_connection.ReceivedEmails;
import cz.mail_manager.server_connection.ReplyType;

@Controller
@SessionAttributes({"user", "email"})
public class SentPageController {

	/**
	 * 	Odeslané emaily
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param model
	 * 	@return - vrací přehled odeslaných emailů
	 */
	@RequestMapping(value = "/odeslane", method = RequestMethod.GET)
	public String submit(@ModelAttribute("user") User user, Model model) {

		ReceivedEmails serverConnection = new ReceivedEmails(user);
		
		List<Email> emails = serverConnection.getEmailsHeader("sent", 0);
		model.addAttribute("emails", emails);
		
		model.addAttribute("messageCount", serverConnection.getMessageCount());
		
		return "emailViewPage";
	}
	
	/**
	 * 	Ajax zobrazení detailu emailů
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param detailIndex - index vybraného emailu
	 *  @param model
	 * 	@return vrací kompletní informace o emailu
	 */
	@RequestMapping(value = "/odeslane/detail", method = RequestMethod.POST)
	public @ResponseBody Email getEmailContent (@ModelAttribute("user") User user, Model model, int detailIndex) {
		
		Email email = new ReceivedEmails(user).getEmailDetail("sent", detailIndex);
		model.addAttribute("email", email);
		
		return email;
	}
	
	/**
	 * 	Zobrazení přiloženého souboru
	 * 
	 * 	@param index - index přílohy
	 * 	@param email - vybraný email
	 * 	@param response
	 */
	@RequestMapping(value = "/edeslane/priloha${index}/*", method = RequestMethod.GET)
	public @ResponseBody void getAttachedFile(@PathVariable("index") String index, @ModelAttribute("email") Email email, HttpServletResponse response) throws IOException {
		
		InputStream inputStream = email.getAttachedFiles().get(Integer.parseInt(index)).getFileInputStream();
		IOUtils.copy(inputStream, response.getOutputStream());
	}
	
	/**
	 * 	Ajax přesunutí emailu do jiné složky
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param detailIndex - index vybraného emailu
	 * 	@param folder - nová složka
	 */
	@RequestMapping(value = "/odeslane/presun", method = RequestMethod.POST)
	public @ResponseBody void moveEmail(@ModelAttribute("user") User user, int detailIndex, String folder) {
		
		new ReceivedEmails(user).move("sent", detailIndex, folder);
	}
	
	/**
	 * 	Ajax smazání emailu	
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param detailIndex - index vybraného emailu
	 */
	@RequestMapping(value = "/odeslane/smazat", method = RequestMethod.POST)
	public @ResponseBody void deleteEmail(@ModelAttribute("user") User user, int detailIndex) {
		
		new ReceivedEmails(user).delete("sent", detailIndex);
	}
	
//	/**
//	 * 	Přednastavení formůláře na odpověď na email
//	 * 
//	 * 	@param user - session přihlášeného uživatele
//	 * 	@param detailIndex - index vybraného emailu
//	 * 	@param model
//	 * 	@return vrací formulář, odpověď na email
//	 */
//	@RequestMapping(value = "/odpoved", method = RequestMethod.POST)
//	public String reply(@ModelAttribute("user") User user, @RequestParam("detailIndex") int detailIndex, Model model) {
//		
//		model.addAttribute("replyType", "Odpověď");
//		
//		Email email = new ReceivedEmails(user).reply("INBOX", detailIndex, ReplyType.REPLY);
//		model.addAttribute("from", email.getFrom());
//		model.addAttribute("to", email.getRecipientsTO());
//		model.addAttribute("cc", email.getRecipientsCC());
//		model.addAttribute("subject", email.getSubject());
//		model.addAttribute("content", email.getContent());
//
//		return "emailReply";
//	}
//	
//	/**
//	 * 	Přednastavení formuláře na odpověď všem
//	 * 
//	 * 	@param user - session přihlášeného uživatele
//	 * 	@param detailIndex - index vybraného emailu
//	 * 	@param model
//	 * 	@return vrací formulář, odpověď na email
//	 */
//	@RequestMapping(value = "/odpoved-vsem", method = RequestMethod.POST)
//	public String replyAll(@ModelAttribute("user") User user, @RequestParam("detailIndex") int detailIndex, Model model) {
//		
//		model.addAttribute("replyType", "Odpověď všem");
//		
//		Email email = new ReceivedEmails(user).reply("INBOX", detailIndex, ReplyType.REPLY_ALL);
//		model.addAttribute("from", email.getFrom());
//		model.addAttribute("to", email.getRecipientsTO());
//		model.addAttribute("cc", email.getRecipientsCC());
//		model.addAttribute("subject", email.getSubject());
//		model.addAttribute("content", email.getContent());
//		
//		return "emailReply";
//	}
//	
//	/**
//	 * 	Přednastavení formuláře na přeposlání emailu
//	 * 
//	 * 	@param user - session přihlášeného uživatele
//	 * 	@param detailIndex - index vybraného emailu
//	 * 	@param model
//	 * 	@return vrací formulář, odpověď na email
//	 */
//	@RequestMapping(value = "/preposlat", method = RequestMethod.POST)
//	public String resend(@ModelAttribute("user") User user, @RequestParam("detailIndex") int detailIndex, Model model) {
//		
//		model.addAttribute("replyType", "Přeposlat");
//		
//		Email email = new ReceivedEmails(user).reply("INBOX", detailIndex, ReplyType.RESEND);
//		model.addAttribute("from", email.getFrom());
//		model.addAttribute("to", email.getRecipientsTO());
//		model.addAttribute("cc", email.getRecipientsCC());
//		model.addAttribute("subject", email.getSubject());
//		model.addAttribute("content", email.getContent());
//		
//		return "emailReply";
//	}
//	
//	/**
//	 * 	Odeslání emailu
//	 * 
//	 * 	@param user - session přihlášeného uživatele
//	 * 	@param email - parametry emailu z formuláře
//	 * 	@param files - List přiložených souborů
//	 * 	@return přesměrování zpět na doručené emaily
//	 */
//	@RequestMapping(value = "/odeslat", method = RequestMethod.POST)
//	public String sendReply(@ModelAttribute("user") User user, @ModelAttribute("email") Email email, @ModelAttribute("files") List<MultipartFile> files) {
//		
//		// Změna kódování na UTF-8
//		try {
//			
//			email.setFrom(new String(email.getFrom().getBytes("iso-8859-1"), "UTF-8").replace(";", "").replaceAll("<", "").replaceAll(">", ""));
//			email.setRecipientsTO(new String(email.getRecipientsTO().getBytes("iso-8859-1"), "UTF-8").replaceAll("<", "").replaceAll(">", ""));
//			email.setRecipientsCC(new String(email.getRecipientsCC().getBytes("iso-8859-1"), "UTF-8").replaceAll("<", "").replaceAll(">", ""));
//			email.setSubject(new String(email.getSubject().getBytes("iso-8859-1"), "UTF-8"));
//			
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		
//		new NewEmail(user).send(email, files);
//		
//		return "redirect:/dorucene";
//	}
	
	/**
	 * 	Ajax načtení dalších emailů
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param lastIndex - index posledního emailu
	 * 	@return vrací List dalších emailů
	 */
	@RequestMapping(value = "/odeslane/nacteni", method = RequestMethod.POST)
	public @ResponseBody List<Email> getMoreEmails(@ModelAttribute("user") User user, int lastIndex) {
		
		List<Email> emails = new ReceivedEmails(user).getEmailsHeader("sent", lastIndex);
		
		return emails;
	}
	
	/**
	 * 	Ajax obnovení nových emailů
	 * 	
	 * 	@param user - session přihlášeného uživatele
	 * 	@param sentDate - datum odeslání emailu
	 * 	@return vrací List nových emailů
	 */
	@RequestMapping(value = "/odeslane/obnoveni", method = RequestMethod.POST)
	public @ResponseBody List<Email> refresh(@ModelAttribute("user") User user, String sentDate) {
		
		List<Email> emails = new ReceivedEmails(user).refreshEmailsHeader("sent", sentDate);
		
		return emails;
	}
	
}
