package x.mvmn.carpool.web.ctrl;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import x.mvmn.carpool.model.User;
import x.mvmn.carpool.service.UserConfirmationService;
import x.mvmn.carpool.service.persistence.UserRepository;

@RestController
public class RegistrationController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserConfirmationService userConfirmationService;

	@Value("${mvmncarpool.user.confirmation.required:true}")
	boolean confirmationRequired;

	@RequestMapping(path = "/check_email_available")
	public @ResponseBody boolean checkEmailAvailability(@RequestParam("email") String emailAddress) {
		return userRepository.findByEmailAddress(emailAddress) == null;
	}

	@RequestMapping(path = "/register", method = RequestMethod.POST)
	public @ResponseBody String doRegister(@Email @RequestParam("email") String emailAddress, @RequestParam("password") String password,
			@RequestParam("passwordConfirmation") String passwordConfirmation, Locale locale, HttpServletResponse response) {
		String result;
		// TODO: Configurable password validation (min length and characters)
		// TODO: Validate email/username
		if (password != null && password.length() > 8 && password.equals(passwordConfirmation)) {
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
				// TODO: re-check email availability
				throw new RuntimeException("Email already taken");
			} catch (Exception e) {
				// TODO: better handling
				try {
					if (user != null) {
						userRepository.delete(user);
					}
				} catch (Exception cleanupUserException) {
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

	// TODO: password reset function

	@RequestMapping(path = "/confirm_reg", method = RequestMethod.GET)
	public void doRegister(@Email @RequestParam("email") String emailAddress,
			@RequestParam(UserConfirmationService.CONFIRMATION_ID_PARAM_NAME) String confirmationId, HttpServletResponse response) {
		User user = userConfirmationService.validateConfirmationResponse(emailAddress, confirmationId);
		if (user != null) {
			user.setConfirmed(true);
			userRepository.save(user);
			// TODO: serve confirmation page - use non-REST controller
		} else {
			// TODO: serve error page - use non-REST controller
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}
