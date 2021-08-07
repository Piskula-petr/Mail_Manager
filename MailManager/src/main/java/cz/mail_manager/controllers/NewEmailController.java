package cz.mail_manager.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cz.mail_manager.pojo.Email;
import cz.mail_manager.server_connection.NewEmail;

@Controller
public class NewEmailController {
	
	@Autowired
	private NewEmail newEmail;
	
	
	/**
	 * 	Formulář pro odeslání nového emailu
	 * 
	 * 	@param model
	 * 
	 * 	@return vrací formulář pro odeslání nového emailu
	 */
	@RequestMapping(value = "/novy", method = RequestMethod.GET)
	public String showNewEmailPage(Model model) {
		
		// Přednastavení odesilatele
		model.addAttribute("from", "<" + newEmail.getEmail() + ">;");
		model.addAttribute("email", new Email());
		
		// List pro přiložené soubory
		List<MultipartFile> files = new ArrayList<>();
		model.addAttribute("files", files);
		
		return "newEmail";
	}
	
	
	/**
	 * 	Odeslání nového emailu
	 * 
	 * 	@param email - parametry emailu z formuláře
	 * 	@param files - List přiložených souborů
	 * 
	 * 	@return přesměrování zpět na doručené emaily
	 */
	@RequestMapping(value = "/novy/odeslat", method = RequestMethod.POST)
	public String sendEmail(@Valid @ModelAttribute("email") Email email, 
							BindingResult result,
							RedirectAttributes attributes,
							@ModelAttribute("files") List<MultipartFile> files) throws UnsupportedEncodingException {
		
		// Změna kódování na UTF-8
		email.setFrom(new String(email.getFrom().getBytes("iso-8859-1"), "UTF-8").replace(";", "").replaceAll("<", "").replaceAll(">", ""));
		email.setRecipientsTO(new String(email.getRecipientsTO().getBytes("iso-8859-1"), "UTF-8").replaceAll("<", "").replaceAll(">", ""));
		email.setRecipientsCC(new String(email.getRecipientsCC().getBytes("iso-8859-1"), "UTF-8").replaceAll("<", "").replaceAll(">", ""));
		email.setSubject(new String(email.getSubject().getBytes("iso-8859-1"), "UTF-8"));
	
		// Přidání atributů, při přesměrování zpět na formůlář
		if (result.hasErrors()) {
			
			attributes.addFlashAttribute("to", email.getRecipientsTO());
			attributes.addFlashAttribute("copy", email.getRecipientsCC());
			attributes.addFlashAttribute("subject", email.getSubject());
			
			// Změna kódování na UTF-8 (pouze při chybné validaci)
			email.setContent(new String(email.getContent().getBytes("iso-8859-1"), "UTF-8"));
			attributes.addFlashAttribute("content", email.getContent());
			
			return "redirect:/novy";
		}
		
		// Odeslání emailu
		newEmail.send(email, files);
		
		return "redirect:/dorucene";
	}
	
	
	/**
	 * 	Přesunutí emailu do složky rozepsané
	 * 
	 * 	@param email - email k přesunu
	 */
	@RequestMapping(value = "/novy/rozepsany", method = RequestMethod.POST)
	public @ResponseBody void moveToDraft(Email email) {
		
		// Odstranění středníků a hranatých závorek
		email.setFrom(email.getFrom().replace(";", "").replaceAll("<", "").replaceAll(">", ""));
		email.setRecipientsTO(email.getRecipientsTO().replaceAll("<", "").replaceAll(">", ""));
		email.setRecipientsCC(email.getRecipientsCC().replaceAll("<", "").replaceAll(">", ""));
		
		// Přesunutí emailu
		newEmail.moveToDraft(email);
	}
	
}
