package cz.mail_manager.pojo;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class User {
	
	@NotBlank
	private String email;
	private String emailServer;
	
	@NotBlank
	private String password;
	
}
