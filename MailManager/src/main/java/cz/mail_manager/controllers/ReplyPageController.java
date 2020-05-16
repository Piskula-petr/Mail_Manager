package cz.mail_manager.controllers;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import cz.mail_manager.beans.Email;
import cz.mail_manager.beans.User;
import cz.mail_manager.server_connection.NewEmail;
import cz.mail_manager.server_connection.ReceivedEmails;
import cz.mail_manager.server_connection.ReplyType;

@Controller
@SessionAttributes({"user", "email"})
public class ReplyPageController {

	/**
	 * 	Přednastavení formůláře na odpověď na email
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param detailIndex - index vybraného emailu
	 * 	@param model
	 * 	@return vrací formulář, odpověď na email
	 */
	@RequestMapping(value = "/odpoved", method = RequestMethod.POST)
	public String reply(@ModelAttribute("user") User user, @RequestParam("detailIndex") int detailIndex, @RequestParam("folderName") String folderName, Model model) {
		
		model.addAttribute("replyType", "Odpověď");
		
		Email email = new ReceivedEmails(user).reply(folderName, detailIndex, ReplyType.REPLY);
		model.addAttribute("from", email.getFrom());
		model.addAttribute("to", email.getRecipientsTO());
		model.addAttribute("cc", email.getRecipientsCC());
		model.addAttribute("subject", email.getSubject());
		model.addAttribute("content", email.getContent());

		return "emailReply";
	}
	
	/**
	 * 	Přednastavení formuláře na odpověď všem
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param detailIndex - index vybraného emailu
	 * 	@param model
	 * 	@return vrací formulář, odpověď na email
	 */
	@RequestMapping(value = "/odpoved-vsem", method = RequestMethod.POST)
	public String replyAll(@ModelAttribute("user") User user, @RequestParam("detailIndex") int detailIndex, @RequestParam("folderName") String folderName, Model model) {
		
		model.addAttribute("replyType", "Odpověď všem");
		
		Email email = new ReceivedEmails(user).reply(folderName, detailIndex, ReplyType.REPLY_ALL);
		model.addAttribute("from", email.getFrom());
		model.addAttribute("to", email.getRecipientsTO());
		model.addAttribute("cc", email.getRecipientsCC());
		model.addAttribute("subject", email.getSubject());
		model.addAttribute("content", email.getContent());
		
		return "emailReply";
	}
	
	/**
	 * 	Přednastavení formuláře na přeposlání emailu
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param detailIndex - index vybraného emailu
	 * 	@param model
	 * 	@return vrací formulář, odpověď na email
	 */
	@RequestMapping(value = "/preposlat", method = RequestMethod.POST)
	public String resend(@ModelAttribute("user") User user, @RequestParam("detailIndex") int detailIndex, @RequestParam("folderName") String folderName, Model model) {
		
		model.addAttribute("replyType", "Přeposlat");
		
		Email email = new ReceivedEmails(user).reply(folderName, detailIndex, ReplyType.RESEND);
		model.addAttribute("from", email.getFrom());
		model.addAttribute("to", email.getRecipientsTO());
		model.addAttribute("cc", email.getRecipientsCC());
		model.addAttribute("subject", email.getSubject());
		model.addAttribute("content", email.getContent());
		
		return "emailReply";
	}
	
	/**
	 * 	Odeslání emailu
	 * 
	 * 	@param user - session přihlášeného uživatele
	 * 	@param email - parametry emailu z formuláře
	 * 	@param files - List přiložených souborů
	 * 	@return přesměrování zpět na doručené emaily
	 */
	@RequestMapping(value = "/odeslat", method = RequestMethod.POST)
	public String sendReply(@ModelAttribute("user") User user, @ModelAttribute("email") Email email, @ModelAttribute("files") List<MultipartFile> files) {
		
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
