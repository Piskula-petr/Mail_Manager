package cz.mail_manager.pojo;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Email {

	// Atributy pro náhled emailů
	private Boolean isSeen;
	private String from;
	
	@NotBlank
	private String subject;
	private String sentDate;
	private String sentTime;

	// Zbývající atributy
	
	@NotBlank
	private String recipientsTO;
	private String recipientsCC;
	private String content;
	private List<AttachedFile> attachedFiles;
	
}
