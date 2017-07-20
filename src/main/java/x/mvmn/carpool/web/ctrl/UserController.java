package x.mvmn.carpool.web.ctrl;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import x.mvmn.carpool.web.dto.GenericResultDTO;
import x.mvmn.carpool.web.dto.UserDTO;
import x.mvmn.util.web.auth.UserUtil;

@RestController
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserConfirmationService userConfirmationService;

	@Value("${mvmncarpool.emailregex:^.*@.*$}")
	String emailRegExPatternStr;

	@Value("${mvmncarpool.password_policy.length.min:6}")
	int passwordLengthMin;

	@Value("${mvmncarpool.password_policy.length.max:256}")
	int passwordLengthMax;

	@Value("${mvmncarpool.password_policy.regex:^.*$}")
	String passwordRegExPatternStr;

	@Autowired
	MessageSource msgSource;

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

	public static enum PasswordInvalidityCause {
		TOO_LONG, TOO_SHORT, BAD_CHARACTERS
	}

	@RequestMapping(path = "/check_email", method = RequestMethod.POST)
	public @ResponseBody EmailCheckResult checkEmail(@RequestParam(required = false, name = "email") String emailAddress) {
		if (!isEmailValid(emailAddress)) {
			return EmailCheckResult.INVALID;
		}
		if (!isEmailAvailable(emailAddress)) {
			return EmailCheckResult.TAKEN;
		}
		return EmailCheckResult.OK;
	}

	@RequestMapping(path = "/reset_password", method = RequestMethod.POST)
	public @ResponseBody GenericResultDTO resetPassword(@Email @RequestParam(required = false, name = "email") String emailAddress, Locale locale,
			HttpServletResponse response) {
		GenericResultDTO result = new GenericResultDTO();
		User user = userRepository.findByEmailAddress(emailAddress);
		if (user != null) {
			try {
				user.setPasswordResetRequestId(userConfirmationService.sendPasswordResetRequest(user, locale));
				user.setPasswordResetRequestUnixTime(System.currentTimeMillis() / 1000);
				userRepository.save(user);
				result.message = "Ok";
				result.success = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			result.message = msgSource.getMessage("error.user_not_found_for_email", new Object[] { emailAddress }, locale);
			result.success = true;
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

		return result;
	}

	@RequestMapping(path = "/register", method = RequestMethod.POST)
	public @ResponseBody GenericResultDTO register(@Email @RequestParam(required = false, name = "email") String emailAddress, Locale locale,
			HttpServletResponse response) {
		GenericResultDTO result = new GenericResultDTO();
		EmailCheckResult emailCheckResult = checkEmail(emailAddress);
		if (emailCheckResult.equals(EmailCheckResult.OK)) {
			User user = null;
			try {
				user = new User();
				user.setEmailAddress(emailAddress);
				user = userRepository.save(user);
				userRepository.flush();

				user.setPasswordResetRequestId(userConfirmationService.sendConfirmationRequest(user, locale));
				user.setPasswordResetRequestUnixTime(-1);
				userRepository.save(user);
				result.success = true;
				result.message = "Ok";
			} catch (DataIntegrityViolationException ex) {
				if (!isEmailAvailable(emailAddress)) {
					result.message = "Email already taken";
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
			// Use password reset instead
			// } else if (isEmailValid(emailAddress) && !isEmailAvailable(emailAddress)) {
			// User existingUser = userRepository.findByEmailAddress(emailAddress);
			// if (existingUser.getConfirmed() == null || !existingUser.getConfirmed().booleanValue()) {
			// try {
			// existingUser.setPassword(passwordEncoder.encode(password));
			// userRepository.save(existingUser);
			// userConfirmationService.sendConfirmationRequest(existingUser, locale);
			// } catch (Exception e) {
			// throw new RuntimeException(e);
			// }
			// }
		} else {
			result.message = msgSource.getMessage("error.email." + emailCheckResult.name().toLowerCase(), new Object[0], locale);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return result;
	}

	@RequestMapping(path = "/api/user/update", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_USER')")
	public GenericResultDTO updateUser(UserDTO userDto, Authentication auth) {
		GenericResultDTO result = new GenericResultDTO();

		User user = UserUtil.getCurrentUser(auth);
		user.setFullName(userDto.getFullName());
		userRepository.save(user);

		return result;
	}

	@RequestMapping(path = "/set_new_password", method = RequestMethod.POST)
	public GenericResultDTO setNewPassword(@Email @RequestParam(required = false, name = "email") String emailAddress,
			@RequestParam(required = false, name = UserConfirmationService.CONFIRMATION_ID_PARAM_NAME) String confirmationId,
			@RequestParam(required = false, name = "password") String password,
			@RequestParam(required = false, name = "passwordConfirmation") String passwordConfirmation,
			@RequestParam(required = false, name = "fullName") String fullName, HttpServletResponse response, Model model) {
		GenericResultDTO result = new GenericResultDTO();
		User user = userConfirmationService.validatePasswordResetRequest(emailAddress, confirmationId);
		if (user != null && password != null && password.equals(passwordConfirmation)) {
			Set<PasswordInvalidityCause> passwordValidationResult = isPasswordValid(password);
			if (passwordValidationResult.isEmpty()) {
				user.setPassword(passwordEncoder.encode(password));
				user.setPasswordResetRequestId(null);
				user.setPasswordResetRequestUnixTime(0);
				user.setConfirmed(true);
				if (fullName != null && !fullName.trim().isEmpty()) {
					user.setFullName(fullName);
				}
				userRepository.save(user);
				result.success = true;
				result.message = "ok";
			} else {
				// TODO: provide password validation result to UI
				result.message = passwordValidationResult.iterator().next().name();
				result.success = false;
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			// TODO: specific errors
			result.message = "Wrong user or passwords don't match";
			result.success = false;
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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

	protected Set<PasswordInvalidityCause> isPasswordValid(String password) {
		Set<PasswordInvalidityCause> result = new HashSet<>();
		// TODO: move to service/helper
		int pwdLength = password.length();
		if (pwdLength < passwordLengthMin) {
			result.add(PasswordInvalidityCause.TOO_SHORT);
		}
		if (pwdLength > passwordLengthMax) {
			result.add(PasswordInvalidityCause.TOO_LONG);
		}
		if (!passwordRegEx.matcher(password).matches()) {
			result.add(PasswordInvalidityCause.BAD_CHARACTERS);
		}

		return result;
	}
}
