package ca.project.mail;

import java.util.Properties;

import org.jasypt.util.text.AES256TextEncryptor;
import org.omnifaces.util.Faces;

import ca.project.entities.CalendarEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import security.entities.User;
import security.entities.VerificationToken;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendConfirmationMail {
	private User user;
	private VerificationToken token;
	
	public void Send() {
		
		String encryptedPassword = "3T5HOKT7LrrAVS4vogttO9fm+ZomlwxB4Q3WSCvgmFxEg8L85DxhvW59VHhDfIUj";

		final String username = "nockmorris@outlook.com";
		final String password = Decryptor.decrypt(encryptedPassword);

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "outlook.office365.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		session.setDebug(true);

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
																										
			message.setSubject("Validation for calendar account: " + user.getUsername());
			message.setText("Please click the following link to verify your email address:\n" +
					"https://localhost:8443" + Faces.getRequestContextPath() + "/security/verifyAccount.xhtml" +
					"?vKey=" + token.getToken());
			Transport.send(message);

			System.out.println("Done");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
