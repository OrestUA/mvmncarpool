package x.mvmn.carpool.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import x.mvmn.util.javaxmail.InetAddressFunction;
import x.mvmn.util.javaxmail.PasswordAuthenticator;

@Service
public class EmailService {

	protected final Properties javaxMailProperties;
	protected char[] password;
	protected final String username;

	protected final LazyInitializer<Session> sessionInitializer;

	public EmailService(@Value("${mail.username}") String username, @Value("${mail.password}") char[] password,
			@Autowired @Qualifier("javaxMailProps") final Properties javaxMailProperties) {
		this.username = username;
		this.password = password;
		this.javaxMailProperties = javaxMailProperties;
		this.sessionInitializer = new LazyInitializer<Session>() {
			@Override
			protected Session initialize() {
				Session session = Session.getInstance(javaxMailProperties, new PasswordAuthenticator(username, new String(password)));
				EmailService.this.password = null;
				return session;
			}
		};
	}

	public void send(String htmlContent, String textContent, String sender, String subject, String... recepients) throws MessagingException, AddressException {
		MimeMessage mimeMessage = new MimeMessage(getSession());
		mimeMessage.setFrom(sender);
		mimeMessage.addRecipients(RecipientType.TO, Arrays.stream(recepients).map(new InetAddressFunction()).toArray(InternetAddress[]::new));
		mimeMessage.setSubject(subject, StandardCharsets.UTF_8.name());
		if (textContent != null) {
			mimeMessage.setText(textContent, StandardCharsets.UTF_8.name());
		}
		if (htmlContent != null) {
			mimeMessage.setContent(textContent, "text/html; charset=utf-8");
		}
		Transport.send(mimeMessage);
	}

	protected Session getSession() {
		try {
			return sessionInitializer.get();
		} catch (ConcurrentException e) {
			throw new RuntimeException(e);
		}
	}
}
