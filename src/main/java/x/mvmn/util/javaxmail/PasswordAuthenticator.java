package x.mvmn.util.javaxmail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class PasswordAuthenticator extends Authenticator {

	protected final PasswordAuthentication auth;

	public PasswordAuthenticator(String username, String password) {
		auth = new PasswordAuthentication(username, password);
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return auth;
	}
}
