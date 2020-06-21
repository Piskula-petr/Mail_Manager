package cz.mail_manager.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cz.mail_manager.pojo.Email;
import cz.mail_manager.pojo.User;
import cz.mail_manager.server_connection.NewEmail;
import cz.mail_manager.server_connection.ReceivedEmails;

@Controller
public class LoginPageController {
	
	@Autowired
	private ReceivedEmails receivedEmails;
	
	@Autowired
	private NewEmail newEmail;
	
	/**
	 * 	Úvodní přihlašovací stránka
	 * 
	 * 	@param model
	 * 
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
	 *  @param result
	 * 	@param model
	 * 
	 * 	@return - vrací hlavní stránku, chybovou stránku
	 */
	@RequestMapping(value = "/dorucene", method = RequestMethod.POST)
	public String submitLoginPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
		
		// Přesměrování zpět na přihlašovací stránku
		if (result.hasErrors()) {
			
			return "loginPage";
		}
		
		// Nastavení přihlašovacích parametrů uživatele
		receivedEmails.setUser(user);
		newEmail.setUser(user);
		
		List<Email> emails = receivedEmails.getEmailsHeader("INBOX", 0);
		
		// Přesměrování na chybovou stránku
		if (emails.isEmpty()) {
			
			return "errorPage";
		}
		
		model.addAttribute("emails", emails);
		model.addAttribute("messageCount", receivedEmails.getMessageCount());
		
		return "emailViewPage";
	}
	
}
