package cz.mail_manager.controllers;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cz.mail_manager.pojo.Email;
import cz.mail_manager.server_connection.NewEmail;
import cz.mail_manager.server_connection.ReceivedEmails;
import cz.mail_manager.server_connection.ReplyType;

@Controller
public class ReplyPageController {

	@Autowired
	private ReceivedEmails receivedEmails;
	
	@Autowired
	private NewEmail newEmail;
	
	private ReplyType replyType;
	
	/**
	 * 	Přednastavení formuláře, při chybné validaci
	 * 
	 * 	@param model
	 * 	@param email - parametry emailu z formuláře
	 * 
	 * 	@return - vrací přednastavený formulář 
	 */
	@RequestMapping(value = "/neplatny-vstup", method = RequestMethod.GET)
	public String invalidInput(Model model, @ModelAttribute("email") Email email) {
		
		// Nastavení typu odpovědi
		if (replyType.equals(ReplyType.REPLY)) {
			
			model.addAttribute("replyType", "Odpověď");
			
		} else if (replyType.equals(ReplyType.REPLY_ALL)) {
			
			model.addAttribute("replyType", "Odpověď všem");
			
		} else if (replyType.equals(ReplyType.RESEND)) {
			
			model.addAttribute("replyType", "Přeposlat");
		}
		
		return "emailReply";
	}
	
	/**
	 * 	Přednastavení formůláře na odpověď na email
	 * 
	 * 	@param detailIndex - index vybraného emailu
	 * 	@param folderName - typ složky ( INBOX, sent, drafts, newsletters, archive, spam, trash )
	 * 	@param model
	 * 
	 * 	@return vrací formulář, odpověď na email
	 */
	@RequestMapping(value = "/odpoved", method = RequestMethod.POST)
	public String reply(@RequestParam("detailIndex") int detailIndex, 
						@RequestParam("folderName") String folderName, 
						Model model) {
		
		model.addAttribute("replyType", "Odpověď");
		
		// Přednastavený email
		Email email = receivedEmails.reply(folderName, detailIndex, ReplyType.REPLY);
		
		// Typ odpovědi
		replyType = ReplyType.REPLY;
		
		model.addAttribute("email", email);

		return "emailReply";
	}
	
	/**
	 * 	Přednastavení formuláře na odpověď všem
	 * 
	 * 	@param detailIndex - index vybraného emailu
	 * 	@param folderName - typ složky ( INBOX, sent, drafts, newsletters, archive, spam, trash )
	 * 	@param model
	 * 
	 * 	@return vrací formulář, odpověď na email
	 */
	@RequestMapping(value = "/odpoved-vsem", method = RequestMethod.POST)
	public String replyAll(@RequestParam("detailIndex") int detailIndex, 
						   @RequestParam("folderName") String folderName, 
						   Model model) {
		
		model.addAttribute("replyType", "Odpověď všem");
		
		// Přednastavený email
		Email email = receivedEmails.reply(folderName, detailIndex, ReplyType.REPLY_ALL);
		
		// Typ odpovědi
		replyType = ReplyType.REPLY_ALL;
		
		model.addAttribute("email", email);
		
		return "emailReply";
	}
	
	/**
	 * 	Přednastavení formuláře na přeposlání emailu
	 * 
	 * 	@param detailIndex - index vybraného emailu
	 * 	@param folderName - typ složky ( INBOX, sent, drafts, newsletters, archive, spam, trash )
	 * 	@param model
	 * 
	 * 	@return vrací formulář, odpověď na email
	 */
	@RequestMapping(value = "/preposlat", method = RequestMethod.POST)
	public String resend(@RequestParam("detailIndex") int detailIndex, 
						 @RequestParam("folderName") String folderName, 
						 Model model) {
		
		model.addAttribute("replyType", "Přeposlat");
		
		// Přednastavený email
		Email email = receivedEmails.reply(folderName, detailIndex, ReplyType.RESEND);
		
		// Typ odpovědi
		replyType = ReplyType.RESEND;
		
		model.addAttribute("email", email);
		
		return "emailReply";
	}
	
	/**
	 * 	Odeslání emailu
	 * 
	 * 	@param email - parametry emailu z formuláře
	 * 	@param files - List přiložených souborů
	 * 
	 * 	@return přesměrování zpět na doručené emaily
	 */
	@RequestMapping(value = "/odeslat", method = RequestMethod.POST)
	public String sendReply(@Valid @ModelAttribute("email") Email email, 
							BindingResult result,
							RedirectAttributes attributes,
							@ModelAttribute("files") List<MultipartFile> files) throws UnsupportedEncodingException {
		
		// Změna kódování na UTF-8
		email.setFrom(new String(email.getFrom().getBytes("iso-8859-1"), "UTF-8").replace(";", "").replaceAll("<", "").replaceAll(">", ""));
		email.setRecipientsTO(new String(email.getRecipientsTO().getBytes("iso-8859-1"), "UTF-8").replaceAll("<", "").replaceAll(">", ""));
		email.setRecipientsCC(new String(email.getRecipientsCC().getBytes("iso-8859-1"), "UTF-8").replaceAll("<", "").replaceAll(">", ""));
		email.setSubject(new String(email.getSubject().getBytes("iso-8859-1"), "UTF-8"));
	
		//
		if (result.hasErrors()) {

			email.setFrom("<" + email.getFrom() + ">;");
			
			// Změna kódování na UTF-8 (pouze při chybné validaci)
			email.setContent(new String(email.getContent().getBytes("iso-8859-1"), "UTF-8"));
			
			attributes.addFlashAttribute("email", email);
			
			return "redirect:/neplatny-vstup";
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
	@RequestMapping(value = {"/odpoved/rozepsany", "/odpoved-vsem/rozepsany", "/preposlat/rozepsany"}, method = RequestMethod.POST)
	public @ResponseBody void moveToDraft(Email email) {
		
		// Odstranění středníků a hranatých závorek
		email.setFrom(email.getFrom().replace(";", "").replaceAll("<", "").replaceAll(">", ""));
		email.setRecipientsTO(email.getRecipientsTO().replaceAll("<", "").replaceAll(">", ""));
		
		if (email.getRecipientsCC() != null) {
			
			email.setRecipientsCC(email.getRecipientsCC().replaceAll("<", "").replaceAll(">", ""));
		}
		
		// Přesunutí emailu
		newEmail.moveToDraft(email);
	}
	
}
