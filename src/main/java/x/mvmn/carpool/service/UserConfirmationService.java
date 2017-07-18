package x.mvmn.carpool.service;

import static x.mvmn.util.CollectionsUtil.pair;

import java.security.SecureRandom;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import x.mvmn.carpool.model.User;
import x.mvmn.carpool.service.persistence.UserRepository;
import x.mvmn.util.CollectionsUtil;

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

	@Value("${mvmncarpool.baseurl}")
	String siteBaseUrl;

	@Value("${mvmncarpool.passwordreset.timeout:86400}")
	int passwordResetRequestValiditySeconds;

	@Autowired
	MessageSource msgSource;

	@Autowired
	private SpringTemplateEngine engine;

	SecureRandom secureRandom = new SecureRandom();

	public String sendConfirmationRequest(User user, Locale locale) throws AddressException, MessagingException {
		String requestId = encoder.encode(user.getEmailAddress() + "//" + System.currentTimeMillis() + "//" + secureRandom.nextLong());

		Context thymeleafContext = createContext(locale, user, requestId);
		emailService.send(engine.process("email/register", thymeleafContext), engine.process("email/register.txt", thymeleafContext), senderEmailAddress,
				getSubject(locale, "mail.confirmation.subject"), user.getEmailAddress());

		return requestId;
	}

	public String sendPasswordResetRequest(User user, Locale locale) throws AddressException, MessagingException {
		String requestId = encoder.encode(user.getEmailAddress() + "//" + System.currentTimeMillis() + "//" + secureRandom.nextLong());

		Context thymeleafContext = createContext(locale, user, requestId);
		emailService.send(engine.process("email/reset_password", thymeleafContext), engine.process("email/reset_password.txt", thymeleafContext),
				senderEmailAddress, getSubject(locale, "mail.reset_password.subject"), user.getEmailAddress());

		return requestId;
	}

	protected String getSubject(Locale locale, String subjectMsgCode) {
		String localizedSitename = msgSource.getMessage("sitename", null, locale);
		return msgSource.getMessage(subjectMsgCode, new Object[] { localizedSitename }, locale);
	}

	protected Context createContext(Locale locale, User user, String requestId) {
		return new Context(locale, CollectionsUtil.toHashMap(pair("user", user), pair("confirmationRequestId", requestId),
				pair("confirmationIdParamName", UserConfirmationService.CONFIRMATION_ID_PARAM_NAME), pair("siteBaseUrl", siteBaseUrl)));
	}

	public User validatePasswordResetRequest(String emailAddress, String requestId) {
		User user = userRepository.findByEmailAddress(emailAddress);
		return (user != null
				&& (user.getPasswordResetRequestUnixTime() == -1
						|| (System.currentTimeMillis() / 1000 - user.getPasswordResetRequestUnixTime()) < passwordResetRequestValiditySeconds)
				&& requestId.equals(user.getPasswordResetRequestId())) ? user : null;
	}
}
