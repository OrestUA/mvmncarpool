package x.mvmn.carpool.web.ctrl;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import x.mvmn.util.web.auth.UserUtil;

@Controller
public class LandingPagesController {

	@Autowired
	UserRepository userRepository;

	@Value("${mvmncarpool.locales}")
	String availableLocales;

	@Value("${mvmncarpool.baseurl}")
	String baseUrl;
	
	@Value("${mvmncarpool.emailregex.js:^.*@.*$}")
	String emailRegExPatternStrJs;
	
	@Value("${mvmncarpool.password_policy.length.min:6}")
	int passwordLengthMin;

	@Value("${mvmncarpool.password_policy.length.max:256}")
	int passwordLengthMax;

	@Value("${mvmncarpool.password_policy.regex.js:^.*$}")
	String passwordRegExPatternStrJs;	

	@Autowired
	UserConfirmationService userConfirmationService;

	@RequestMapping(path = "/", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ROLE_USER')")
	public String index(Model model, Authentication auth) {
		populateCommonModelData(model, auth);
		return "index";
	}

	@RequestMapping(path = "/profile", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ROLE_USER')")
	public String profilePage(Model model, Authentication auth) {
		populateCommonModelData(model, auth);
		return "profile";
	}

	@RequestMapping(path = "/signin", method = RequestMethod.GET)
	public String showSignin(Model model, Authentication auth, Locale locale) {
		if (auth != null && auth.getPrincipal() != null) {
			return "redirect:" + baseUrl + "/";
		} else {
			populateCommonModelData(model, auth);
			return "signin";
		}
	}

	@RequestMapping(path = "/set_new_password", method = RequestMethod.GET)
	public String showSetNewPasswordForm(@Email @RequestParam(name = "email", required = true) String emailAddress,
			@RequestParam(name = UserConfirmationService.CONFIRMATION_ID_PARAM_NAME, required = true) String confirmationId,
			@RequestParam(name = "action", required = false) String action, HttpServletResponse response, Model model) {
		User user = userConfirmationService.validatePasswordResetRequest(emailAddress, confirmationId);
		model.addAttribute("requestValid", user != null);
		model.addAttribute("confirmationIdParamName", UserConfirmationService.CONFIRMATION_ID_PARAM_NAME);
		model.addAttribute("confirmationId", confirmationId);
		model.addAttribute("registerAction", "register".equalsIgnoreCase(action));
		populateCommonModelData(model, null);
		model.addAttribute("user", user);
		return "new_password";
	}

	protected String[] getAvailableLocales() {
		return availableLocales.split("\\s*,\\s*");
	}

	protected void populateCommonModelData(Model model, Authentication auth) {
		model.addAttribute("currentUser", UserUtil.getCurrentUser(auth));
		model.addAttribute("locales", getAvailableLocales());
		model.addAttribute("global_baseUrl", baseUrl);
		model.addAttribute("emailRegexJs", emailRegExPatternStrJs);
		model.addAttribute("passwordRegexJs", passwordRegExPatternStrJs);
		model.addAttribute("passwordLengthMin", passwordLengthMin);
		model.addAttribute("passwordLengthMax", passwordLengthMax);
	}
}
