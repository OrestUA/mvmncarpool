package x.mvmn.carpool.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import x.mvmn.util.web.auth.UserUtil;

@Service
public class WebHelperService {

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

	@Value("${mvmncarpool.locales}")
	String availableLocales;

	public String[] getAvailableLocales() {
		return availableLocales.split("\\s*,\\s*");
	}

	public void populateCommonModelData(Model model, Authentication auth) {
		model.addAttribute("currentUser", UserUtil.getCurrentUser(auth));
		model.addAttribute("locales", getAvailableLocales());
		model.addAttribute("global_baseUrl", baseUrl);
		model.addAttribute("emailRegexJs", emailRegExPatternStrJs);
		model.addAttribute("passwordRegexJs", passwordRegExPatternStrJs);
		model.addAttribute("passwordLengthMin", passwordLengthMin);
		model.addAttribute("passwordLengthMax", passwordLengthMax);
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
