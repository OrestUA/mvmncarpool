package x.mvmn.carpool.web.ctrl;

import java.util.Locale;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.savoirtech.logging.slf4j.json.LoggerFactory;
import com.savoirtech.logging.slf4j.json.logger.Logger;

import x.mvmn.carpool.model.User;
import x.mvmn.carpool.service.UserConfirmationService;
import x.mvmn.carpool.service.persistence.UserRepository;

@RestController
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserConfirmationService userConfirmationService;

	@Value("${mvmncarpool.user.confirmation.required:true}")
	boolean confirmationRequired;

	@Value("${mvmncarpool.emailregex:^.*@.*$}")
	String emailRegExPatternStr;

	@Value("${mvmncarpool.password_policy.length.min:6}")
	int passwordLengthMin;

	@Value("${mvmncarpool.password_policy.length.max:256}")
	int passwordLengthMax;

	@Value("${mvmncarpool.password_policy.regex:^.*$}")
	String passwordRegExPatternStr;

	protected Pattern emailRegEx;
	protected Pattern passwordRegEx;

	@PostConstruct
	protected void compileRegexps() {
		this.emailRegEx = Pattern.compile(emailRegExPatternStr);
		this.passwordRegEx = Pattern.compile(passwordRegExPatternStr);
	}

	public static enum EmailCheckResult {
		INVALID, TAKEN, OK
	}

	@RequestMapping(path = "/check_email", method = RequestMethod.POST)
	public @ResponseBody EmailCheckResult checkEmail(@RequestParam("email") String emailAddress) {
		if (!isEmailValid(emailAddress)) {
			return EmailCheckResult.INVALID;
		}
		if (!isEmailAvailable(emailAddress)) {
			return EmailCheckResult.TAKEN;
		}
		return EmailCheckResult.OK;
	}

	@RequestMapping(path = "/reset_password", method = RequestMethod.POST)
	public @ResponseBody String resetPassword(@Email @RequestParam("email") String emailAddress, Locale locale, HttpServletResponse response) {
		String result;
		User user = userRepository.findByEmailAddress(emailAddress);
		if (user != null) {
			try {
				user.setPasswordResetRequestId(userConfirmationService.sendPasswordResetRequest(user, locale));
				user.setPasswordResetRequestUnixTime(System.currentTimeMillis() / 1000);
				userRepository.save(user);
				result = "Ok";
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			result = "No user.";
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

		return result;
	}

	@RequestMapping(path = "/register", method = RequestMethod.POST)
	public @ResponseBody String doRegister(@Email @RequestParam("email") String emailAddress, @RequestParam("password") String password,
			@RequestParam("passwordConfirmation") String passwordConfirmation, Locale locale, HttpServletResponse response) {
		String result; // TODO: JSON object result
		if (isEmailValid(emailAddress) && isEmailAvailable(emailAddress) && password != null && password.equals(passwordConfirmation)
				&& isPasswordValid(passwordConfirmation)) {
			User user = null;
			try {
				user = new User();
				user.setEmailAddress(emailAddress);
				user.setPassword(passwordEncoder.encode(password));
				user = userRepository.save(user);
				if (confirmationRequired) {
					user.setConfirmationRequestId(userConfirmationService.sendConfirmationRequest(user, locale));
				} else {
					user.setConfirmed(true);
				}
				userRepository.save(user);
				result = "ok";
			} catch (DataIntegrityViolationException ex) {
				if (!isEmailAvailable(emailAddress)) {
					throw new RuntimeException("Email already taken");
				} else {
					throw ex;
				}
			} catch (Exception e) {
				// TODO: better handling
				try {
					if (user != null) {
						userRepository.delete(user);
					}
				} catch (Exception cleanupUserException) {
					LOGGER.warn().message("Failed to delete user after reg confirm sending failed").exception("exception", cleanupUserException).log();
					cleanupUserException.printStackTrace();
				}
				throw new RuntimeException(e);
			}
		} else {
			result = "Invalid password or passwords don't match";
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return result;
	}

	@RequestMapping(path = "/set_new_password", method = RequestMethod.POST)
	public String setNewPassword(@Email @RequestParam("email") String emailAddress,
			@RequestParam(UserConfirmationService.CONFIRMATION_ID_PARAM_NAME) String confirmationId, @RequestParam("password") String password,
			@RequestParam("passwordConfirmation") String passwordConfirmation, HttpServletResponse response, Model model) {
		String result;
		User user = userConfirmationService.validatePasswordResetRequest(emailAddress, confirmationId);
		if (user != null && password != null && password.equals(passwordConfirmation) && isPasswordValid(password)) {
			user.setPassword(passwordEncoder.encode(password));
			user.setPasswordResetRequestId(null);
			user.setPasswordResetRequestUnixTime(0);
			userRepository.save(user);
			result = "ok";
		} else {
			// TODO: specific errors
			result = "error";
		}
		return result;
	}

	protected boolean isEmailAvailable(String emailAddress) {
		// TODO: move to service/helper
		return userRepository.findByEmailAddress(emailAddress) == null;
	}

	protected boolean isEmailValid(String email) {
		// TODO: move to service/helper
		return emailRegEx.matcher(email).matches();
	}

	protected boolean isPasswordValid(String password) {
		// TODO: move to service/helper
		int pwdLength = password.length();
		return pwdLength >= passwordLengthMin && pwdLength <= passwordLengthMax && passwordRegEx.matcher(password).matches();
	}
}
