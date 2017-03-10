package x.mvmn.carpool.web.ctrl;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import x.mvmn.carpool.model.User;
import x.mvmn.carpool.service.UserConfirmationService;
import x.mvmn.carpool.service.persistence.UserRepository;

@Controller
public class LandingPagesController {

	@Autowired
	UserRepository userRepository;

	@Value("${mvmncarpool.locales}")
	String availableLocales;

	@Autowired
	UserConfirmationService userConfirmationService;

	@RequestMapping(path = "/", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ROLE_USER')")
	public String index(Model model) {
		model.addAttribute("userCount", userRepository.count());
		return "index";
	}

	@RequestMapping(path = "/signin", method = RequestMethod.GET)
	public String showSignin(Model model) {
		model.addAttribute("locales", getAvailableLocales());
		return "signin";
	}

	@RequestMapping(path = "/set_new_password", method = RequestMethod.GET)
	public String showSetNewPasswordForm(@Email @RequestParam("email") String emailAddress,
			@RequestParam(UserConfirmationService.CONFIRMATION_ID_PARAM_NAME) String confirmationId, HttpServletResponse response, Model model) {

		User user = userConfirmationService.validatePasswordResetRequest(emailAddress, confirmationId);
		model.addAttribute("requestValid", user != null);
		model.addAttribute("user", user);
		model.addAttribute("confirmationIdParamName", UserConfirmationService.CONFIRMATION_ID_PARAM_NAME);
		model.addAttribute("confirmationId", confirmationId);
		return "new_password";
	}

	@RequestMapping(path = "/confirm_reg", method = RequestMethod.GET)
	public String confirmRegistration(@Email @RequestParam("email") String emailAddress,
			@RequestParam(UserConfirmationService.CONFIRMATION_ID_PARAM_NAME) String confirmationId, HttpServletResponse response, Model model) {
		User user = userConfirmationService.validateConfirmationResponse(emailAddress, confirmationId);
		if (user != null) {
			if (user.getConfirmed() != null && user.getConfirmed().booleanValue()) {
				model.addAttribute("regSuccess", true);
				model.addAttribute("alreadyConfirmed", true);
			} else {
				user.setConfirmed(true);
				userRepository.save(user);
				model.addAttribute("regSuccess", true);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return "index";
	}

	protected String[] getAvailableLocales() {
		return availableLocales.split("\\s*,\\s*");
	}
}
