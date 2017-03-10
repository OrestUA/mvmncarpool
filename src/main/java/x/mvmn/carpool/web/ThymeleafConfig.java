package x.mvmn.carpool.web;

import java.util.Collections;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import x.mvmn.carpool.l10n.ExtReloadableResourceBundleMessageSource;

@Configuration
public class ThymeleafConfig {

	@Value("${mvmncarpool.defaultlocale:en_US}")
	String defaultLocaleLanguageTag;

	@Bean
	public SpringTemplateEngine thymeleafSpringTemplateEngine(@Autowired MessageSource messageSource) {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();

		templateEngine.addTemplateResolver(createTemplateResolver(1, "*.txt", "/templates/", null, TemplateMode.TEXT));
		templateEngine.addTemplateResolver(createTemplateResolver(2, "*", "/templates/", ".html", TemplateMode.HTML));
		templateEngine.setTemplateEngineMessageSource(messageSource);

		return templateEngine;
	}

	public static ITemplateResolver createTemplateResolver(int order, String pathPattern, String prefix, String suffix, TemplateMode templateMode) {
		final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setOrder(order);
		templateResolver.setResolvablePatterns(Collections.singleton(pathPattern));
		templateResolver.setPrefix(prefix);
		templateResolver.setSuffix(suffix);
		templateResolver.setTemplateMode(templateMode);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(true);
		return templateResolver;
	}

	@Bean
	@Scope("singleton")
	public ExtReloadableResourceBundleMessageSource messageSource() {
		ExtReloadableResourceBundleMessageSource messageSource = new ExtReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/messages/l10n");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.forLanguageTag(defaultLocaleLanguageTag));
		return localeResolver;
	}
}
