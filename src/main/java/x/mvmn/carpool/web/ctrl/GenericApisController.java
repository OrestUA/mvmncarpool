package x.mvmn.carpool.web.ctrl;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import x.mvmn.carpool.l10n.ExtReloadableResourceBundleMessageSource;
import x.mvmn.carpool.web.dto.GenericResultDTO;

@RestController
public class GenericApisController {

	@Autowired
	LocaleResolver localeResolver;

	@Autowired
	ExtReloadableResourceBundleMessageSource msgSource;

	@Value("${mvmncarpool.locales}")
	String availableLocales;

	@RequestMapping(path = "/l10n", method = RequestMethod.GET)
	public Map<String, String> getLocalizedMessages(HttpServletRequest request, @RequestParam(required = false, name = "locale") String localeCode) {
		Locale locale = null;
		if (localeCode != null) {
			Optional<Locale> localeOptional = Optional.of(Locale.forLanguageTag(localeCode));
			if (localeOptional.isPresent()) {
				locale = localeOptional.get();
			}
		}
		if (locale == null) {
			locale = localeResolver.resolveLocale(request);
		}
		return msgSource.getAll(locale);
	}

	@RequestMapping(path = "/locale/{locale}", method = RequestMethod.POST)
	public GenericResultDTO setLocale(@PathVariable("locale") String localeCode, HttpServletRequest request, HttpServletResponse response) {
		Optional<Locale> localeOptional = pickAvailableLocaleByCode(localeCode);
		if (localeOptional.isPresent()) {
			localeResolver.setLocale(request, response, localeOptional.get());
			return new GenericResultDTO(true, "Ok");
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new GenericResultDTO(false, "Unknown locale");
		}
	}

	@RequestMapping(value = "/xt", method = RequestMethod.GET)
	public @ResponseBody String getCsrfToken(HttpServletRequest request) {
		CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		return token.getToken();
	}

	protected Optional<Locale> pickAvailableLocaleByCode(String localeCode) {
		return Arrays.stream(getAvailableLocales()).map(Locale::forLanguageTag).filter(locale -> locale.toLanguageTag().equals(localeCode)).findFirst();
	}

	protected String[] getAvailableLocales() {
		return availableLocales.split("\\s*,\\s*");
	}
}
