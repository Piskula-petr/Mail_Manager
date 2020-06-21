package cz.mail_manager.pojo;

import java.io.InputStream;

public class AttachedFile {

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
