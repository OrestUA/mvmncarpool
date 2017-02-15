package x.mvmn.carpool.web;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import x.mvmn.carpool.l10n.ExtReloadableResourceBundleMessageSource;

@Configuration
public class ThymeleafConfig {
	@Bean
	public ExtReloadableResourceBundleMessageSource messageSource() {
		ExtReloadableResourceBundleMessageSource messageSource = new ExtReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/messages/l10n");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("en", "US")); // TODO: make configurable
		return localeResolver;
	}
}
