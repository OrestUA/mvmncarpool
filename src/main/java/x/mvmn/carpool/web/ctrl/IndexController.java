package x.mvmn.carpool.web.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import x.mvmn.carpool.service.persistence.UserRepository;

@Controller
public class IndexController {

	@Autowired
	UserRepository userRepository;

	@Value("${mvmncarpool.locales}")
	String availableLocales;

	@RequestMapping("/")
	@PreAuthorize("hasRole('ROLE_USER')")
	public String index(Model model) {
		model.addAttribute("userCount", userRepository.count());
		return "index";
	}

	@RequestMapping("/signin")
	public String showSignin(Model model) {
		model.addAttribute("locales", getAvailableLocales());
		return "signin";
	}

	protected String[] getAvailableLocales() {
		return availableLocales.split("\\s*,\\s*");
	}
}
