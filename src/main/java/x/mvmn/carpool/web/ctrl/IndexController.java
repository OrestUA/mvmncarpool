package x.mvmn.carpool.web.ctrl;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;

import x.mvmn.carpool.service.EmailService;
import x.mvmn.carpool.service.persistence.UserRepository;

@Controller
public class IndexController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	LocaleResolver localeResolver;

	@Autowired
	EmailService emailService;

	@Value("${mvmncarpool.locales}")
	String availableLocales;

	@Value("${mail.username}")
	String mailSender;

	@RequestMapping("/")
	@PreAuthorize("hasRole('ROLE_USER')")
	public String index(Model model) {
		model.addAttribute("userCount", userRepository.count());

		try {
			emailService.send("<html><body>Test <B>here</B></body></html>", "Test here", mailSender, "Test here", "sauron.inbox@gmail.com");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "index";
	}

	@RequestMapping("/signin")
	public String showSignin(Model model) {
		model.addAttribute("locales", getAvailableLocales());
		return "signin";
	}

	@RequestMapping("/locale/{locale}")
	public String setLocale(@PathVariable("locale") String localeCode, @RequestParam String redirect, HttpServletRequest request,
			HttpServletResponse response) {
		Optional<Locale> localeOptional = Arrays.stream(getAvailableLocales()).map(Locale::forLanguageTag)
				.filter(locale -> locale.toLanguageTag().equals(localeCode)).findFirst();
		if (localeOptional.isPresent()) {
			localeResolver.setLocale(request, response, localeOptional.get());
		}
		return "redirect:/" + redirect;
	}

	@RequestMapping(value = "/xt", method = RequestMethod.GET)
	public @ResponseBody String getCsrfToken(HttpServletRequest request) {
		CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		return token.getToken();
	}

	protected String[] getAvailableLocales() {
		return availableLocales.split("\\s*,\\s*");
	}
}
