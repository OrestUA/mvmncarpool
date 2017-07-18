package x.mvmn.carpool.web.ctrl;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import x.mvmn.carpool.model.User;
import x.mvmn.carpool.service.UserConfirmationService;
import x.mvmn.carpool.service.persistence.UserRepository;
import x.mvmn.carpool.web.security.WebSecurity.UserDetailsAdaptor;

@Controller
public class LandingPagesController {

	@Autowired
	UserRepository userRepository;

	@Value("${mvmncarpool.locales}")
	String availableLocales;

	@Autowired
	UserConfirmationService userConfirmationService;

	@Autowired
	MessageSource msgSource;

	@RequestMapping(path = "/", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ROLE_USER')")
	public String index(Model model, Authentication auth) {
		model.addAttribute("currentUser", getPrincipal(auth).getUser());
		model.addAttribute("locales", getAvailableLocales());
		return "index";
	}

	@RequestMapping(path = "/signin", method = RequestMethod.GET)
	public String showSignin(Model model, Authentication auth, Locale locale) {
		if (auth != null && auth.getPrincipal() != null) {
			String baseUrl = msgSource.getMessage("global.baseUrl", null, locale);
			return "redirect:" + baseUrl + "/";
		} else {
			model.addAttribute("locales", getAvailableLocales());
			return "signin";
		}
	}

	@RequestMapping(path = "/set_new_password", method = RequestMethod.GET)
	public String showSetNewPasswordForm(@Email @RequestParam(name = "email", required = true) String emailAddress,
			@RequestParam(name = UserConfirmationService.CONFIRMATION_ID_PARAM_NAME, required = true) String confirmationId,
			@RequestParam(name = "action", required = false) String action, HttpServletResponse response, Model model) {
		User user = userConfirmationService.validatePasswordResetRequest(emailAddress, confirmationId);
		model.addAttribute("requestValid", user != null);
		model.addAttribute("user", user);
		model.addAttribute("confirmationIdParamName", UserConfirmationService.CONFIRMATION_ID_PARAM_NAME);
		model.addAttribute("confirmationId", confirmationId);
		model.addAttribute("registerAction", "register".equalsIgnoreCase(action));
		model.addAttribute("locales", getAvailableLocales());
		return "new_password";
	}

	protected String[] getAvailableLocales() {
		return availableLocales.split("\\s*,\\s*");
	}

	protected UserDetailsAdaptor getPrincipal(Authentication auth) {
		return (UserDetailsAdaptor) auth.getPrincipal();
	}
}
