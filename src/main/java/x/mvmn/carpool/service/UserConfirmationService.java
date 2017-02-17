package x.mvmn.carpool.service;

import static x.mvmn.util.CollectionsUtil.pair;

import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import x.mvmn.carpool.l10n.ExtReloadableResourceBundleMessageSource;
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

	@Autowired
	ExtReloadableResourceBundleMessageSource msgSource;

	@Autowired
	private SpringTemplateEngine engine;

	public String sendConfirmationRequest(User user, Locale locale) throws AddressException, MessagingException {
		String requestId = encoder.encode(user.getEmailAddress() + "//" + System.currentTimeMillis());

		String localizedSitename = msgSource.getMessage("sitename", null, locale);
		String localizedSubject = msgSource.getMessage("mail.confirmation.subject", new Object[] { localizedSitename }, locale);

		Context thymeleafContext = createContext(locale, user, requestId);
		emailService.send(engine.process("email/register", thymeleafContext), engine.process("email/register.txt", thymeleafContext), senderEmailAddress,
				localizedSubject, user.getEmailAddress());
		return requestId;
	}

	public User validateConfirmationResponse(String email, String requestId) {
		User user = userRepository.findByEmailAddress(email);
		return (user != null && requestId.equals(user.getConfirmationRequestId())) ? user : null;
	}

	protected Context createContext(Locale locale, User user, String requestId) {
		return new Context(locale, CollectionsUtil.toHashMap(pair("user", user), pair("confirmationRequestId", requestId),
				pair("confirmationIdParamName", UserConfirmationService.CONFIRMATION_ID_PARAM_NAME), pair("siteBaseUrl", siteBaseUrl)));

	}
}
