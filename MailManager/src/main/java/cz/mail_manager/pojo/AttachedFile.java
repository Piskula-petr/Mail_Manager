package cz.mail_manager.pojo;

import java.io.InputStream;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AttachedFile {

	private String fileName;
	private InputStream fileInputStream;
	private String fileSize;
	
}
