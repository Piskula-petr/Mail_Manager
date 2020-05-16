package cz.mail_manager.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import cz.mail_manager.beans.Email;
import cz.mail_manager.beans.User;
import cz.mail_manager.server_connection.ReceivedEmails;

@Controller
@SessionAttributes({"user", "email"})
public class InboxPageController {
	
	/**
	 * 	Doručené emaily
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param model
	 * 	@return - vrací přehled doručených emailů
	 */
	@RequestMapping(value = "/dorucene", method = RequestMethod.GET)
	public String submit(@ModelAttribute("user") User user, Model model) {
		
		ReceivedEmails serverConnection = new ReceivedEmails(user);
		
		List<Email> emails = serverConnection.getEmailsHeader("INBOX", 0);
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
	@RequestMapping(value = "/dorucene/detail", method = RequestMethod.POST)
	public @ResponseBody Email getEmailContent (@ModelAttribute("user") User user, Model model, int detailIndex) {
		
		Email email = new ReceivedEmails(user).getEmailDetail("INBOX", detailIndex);
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
	@RequestMapping(value = "/dorucene/priloha${index}/*", method = RequestMethod.GET)
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
	@RequestMapping(value = "/dorucene/presun", method = RequestMethod.POST)
	public @ResponseBody void moveEmail(@ModelAttribute("user") User user, int detailIndex, String folder) {
		
		new ReceivedEmails(user).move("INBOX", detailIndex, folder);
	}
	
	/**
	 * 	Ajax smazání emailu	
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param detailIndex - index vybraného emailu
	 */
	@RequestMapping(value = "/dorucene/smazat", method = RequestMethod.POST)
	public @ResponseBody void deleteEmail(@ModelAttribute("user") User user, int detailIndex) {
		
		new ReceivedEmails(user).delete("INBOX", detailIndex);
	}
	
	/**
	 * 	Ajax načtení dalších emailů
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param lastIndex - index posledního emailu
	 * 	@return vrací List dalších emailů
	 */
	@RequestMapping(value = "/dorucene/nacteni", method = RequestMethod.POST)
	public @ResponseBody List<Email> getMoreEmails(@ModelAttribute("user") User user, int lastIndex) {
		
		List<Email> emails = new ReceivedEmails(user).getEmailsHeader("INBOX", lastIndex);
		
		return emails;
	}
	
	/**
	 * 	Ajax obnovení nových emailů
	 * 	
	 * 	@param user - session přihlášeného uživatele
	 * 	@param sentDate - datum odeslání emailu
	 * 	@return vrací List nových emailů
	 */
	@RequestMapping(value = "/dorucene/obnoveni", method = RequestMethod.POST)
	public @ResponseBody List<Email> refresh(@ModelAttribute("user") User user, String sentDate) {
		
		List<Email> emails = new ReceivedEmails(user).refreshEmailsHeader("INBOX", sentDate);
		
		return emails;
	}
	
}
