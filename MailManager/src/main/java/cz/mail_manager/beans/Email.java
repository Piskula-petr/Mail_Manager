package cz.mail_manager.beans;

import java.io.Serializable;
import java.util.List;

public class Email implements Serializable {

	private static final long serialVersionUID = 1L;

	// Atributy pro náhled emailů
	private Boolean isSeen;
	private String from;
	private String subject;
	private String sentDate;
	private String sentTime;

	// Zbývající atributy
	private String recipientsTO;
	private String recipientsCC;
	private String content;
	private List<AttachedFile> attachedFiles;
	
// Bezparametrový konstuktor /////////////////////////////////////////////////////////////////////////
	
	public Email() {
		
	}
	
// Gettery + Settery /////////////////////////////////////////////////////////////////////////////////
	
	public Boolean getIsSeen() {
		return isSeen;
	}

	public void setIsSeen(Boolean isSeen) {
		this.isSeen = isSeen;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSentDate() {
		return sentDate;
	}

	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}

	public String getSentTime() {
		return sentTime;
	}

	public void setSentTime(String sentTime) {
		this.sentTime = sentTime;
	}

	public String getRecipientsTO() {
		return recipientsTO;
	}

	public void setRecipientsTO(String recipientsTO) {
		this.recipientsTO = recipientsTO;
	}

	public String getRecipientsCC() {
		return recipientsCC;
	}

	public void setRecipientsCC(String recipientsCC) {
		this.recipientsCC = recipientsCC;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<AttachedFile> getAttachedFiles() {
		return attachedFiles;
	}

	public void setAttachedFiles(List<AttachedFile> attachedFiles) {
		this.attachedFiles = attachedFiles;
	}
	
}
