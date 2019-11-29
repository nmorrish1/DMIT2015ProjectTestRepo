package ca.assignment05.mail;

import java.util.Properties;

import org.jasypt.util.text.AES256TextEncryptor;

import ca.assignment05.entities.CalendarEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMail {
	private String recipientAddress;
	private CalendarEvent event;
	
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
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientAddress));
																										
			message.setSubject("Reminder for: " + event.getEventName());
			message.setText("This is a reminder for:" + event.getEventName() + "\n" +
							"Starting at: " + event.getStartDate()  + "\n" +
							"Ending at: " + event.getEndDate() + "\n" +
							"Location: " + event.getLocation() + "\n" +
							"Details:" + event.getDescription());
			Transport.send(message);

			System.out.println("Done");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
