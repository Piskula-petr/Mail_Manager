package cz.mail_manager.beans;

import java.io.InputStream;
import java.io.Serializable;

public class AttachedFile implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileName;
	private InputStream fileInputStream;
	private String fileSize;
	
// Bezparametrový konstruktor ///////////////////////////////////////////////////////////////////////////
	
	public AttachedFile() {
		
	}
		
// Gettery + Settery ////////////////////////////////////////////////////////////////////////////////////

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	
}
