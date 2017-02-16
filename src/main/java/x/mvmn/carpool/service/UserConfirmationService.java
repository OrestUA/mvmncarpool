package x.mvmn.carpool.service;

import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring4.SpringTemplateEngine;

import x.mvmn.carpool.l10n.ExtReloadableResourceBundleMessageSource;
import x.mvmn.carpool.model.User;
import x.mvmn.carpool.service.persistence.UserRepository;
import x.mvmn.util.spring.mvc.ThymeleafContext;

@Service
public class UserConfirmationService {

	public static final String CONFIRMATION_ID_PARAM_NAME = "confirmation_id";

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	UserRepository userRepository;

	@Autowired
	EmailService emailService;

	@Value("${mail.username}")
	String senderEmailAddress;

	@Autowired
	ExtReloadableResourceBundleMessageSource msgSource;

	@Autowired
	private SpringTemplateEngine engine;

	public String sendConfirmationRequest(User user, Locale locale) throws AddressException, MessagingException {
		String requestId = encoder.encode(user.getEmailAddress() + "//" + System.currentTimeMillis());

		String localizedSitename = msgSource.getMessage("sitename", null, locale);
		String localizedSubject = msgSource.getMessage("mail.confirmation.subject", new Object[] { localizedSitename }, locale);

		ThymeleafContext tymeleafContext = new ThymeleafContext(locale, new ImmutablePair<String, Object>("user", user),
				new ImmutablePair<String, Object>("confirmationRequestId", requestId));
		// FIXME: find a way to render TXT template
		emailService.send(engine.process("email_register", tymeleafContext), engine.process("email_register", tymeleafContext), senderEmailAddress,
				localizedSubject, user.getEmailAddress());
		return requestId;
	}

	public User validateConfirmationResponse(String email, String requestId) {
		User user = userRepository.findByEmailAddress(email);
		return (user != null && requestId.equals(user.getConfirmationRequestId())) ? user : null;
	}
}
