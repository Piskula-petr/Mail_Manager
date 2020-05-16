package cz.mail_manager.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import cz.mail_manager.beans.Email;
import cz.mail_manager.beans.User;
import cz.mail_manager.server_connection.NewEmail;

@Controller
@SessionAttributes("user")
public class NewEmailController {

	/**
	 * 	Formulář pro odeslání nového emailu
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param model
	 * 	@return vrací formulář pro odeslání nového emailu
	 */
	@RequestMapping(value = "/novy", method = RequestMethod.GET)
	public String showNewEmailPage(@ModelAttribute("user") User user, Model model) {
		
		// Přednastavení odesilatele
		model.addAttribute("from", user.getEmail() + user.getEmailServer());
		model.addAttribute("email", new Email());
		
		// List pro přiložené soubory k emailu
		List<MultipartFile> files = new ArrayList<>();
		model.addAttribute("files", files);
		
		return "newEmail";
	}
	
	/**
	 * 	Odeslání nového emailu
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param email - parametry emailu z formuláře
	 * 	@param files - List přiložených souborů
	 * 	@return přesměrování zpět na doručené emaily
	 */
	@RequestMapping(value = "/novy/odeslat", method = RequestMethod.POST)
	public String sendEmail(@ModelAttribute("user") User user, @ModelAttribute("email") Email email, @ModelAttribute("files") List<MultipartFile> files) {
		
		// Změna kódování na UTF-8
		try {

			email.setFrom(new String(email.getFrom().getBytes("iso-8859-1"), "UTF-8").replace(";", "").replaceAll("<", "").replaceAll(">", ""));
			email.setRecipientsTO(new String(email.getRecipientsTO().getBytes("iso-8859-1"), "UTF-8").replaceAll("<", "").replaceAll(">", ""));
			email.setRecipientsCC(new String(email.getRecipientsCC().getBytes("iso-8859-1"), "UTF-8").replaceAll("<", "").replaceAll(">", ""));
			email.setSubject(new String(email.getSubject().getBytes("iso-8859-1"), "UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		new NewEmail(user).send(email, files);
		
		return "redirect:/dorucene";
	}
	
}
