package ca.project.mail;

import org.jasypt.util.text.AES256TextEncryptor;

public class Decryptor {
	
	public static String decrypt(String encryptedString) {
		AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
		textEncryptor.setPassword("Password2015");
		
		return textEncryptor.decrypt(encryptedString);
	}
	

}
