package cz.mail_manager.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import cz.mail_manager.beans.Email;
import cz.mail_manager.beans.User;
import cz.mail_manager.server_connection.ReceivedEmails;

@Controller
@SessionAttributes("user")
public class LoginPageController {
	
	/**
	 * 	Úvodní přihlašovací stránka
	 * 
	 * 	@param model
	 * 	@return - vrací přihlašovací stránku
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showLoginPage(Model model) {
		
		model.addAttribute("user", new User());
		
		return "loginPage";
	}

	/**
	 * 	Hlavní stránka přehledů emailů
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param model
	 * 	@return - vrací hlavní stránku, chybovou stránku
	 */
	@RequestMapping(value = "/dorucene", method = RequestMethod.POST)
	public String submit(@ModelAttribute("user") User user, Model model) {
		
		ReceivedEmails serverConnection = new ReceivedEmails(user);
		
		List<Email> emails = serverConnection.getEmailsHeader("INBOX", 0);
		model.addAttribute("emails", emails);
		
		model.addAttribute("messageCount", serverConnection.getMessageCount());
		
		// Přesměrování na chybovou stránku
		if (emails.isEmpty()) {
			
			return "errorPage";
			
		} else return "emailViewPage";
	}
	
}
