package x.mvmn.util.spring.mvc;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.VariablesMap;

public class ThymeleafContext implements IContext {

	protected final Locale locale;
	protected final VariablesMap<String, Object> varMap = new VariablesMap<String, Object>();

	public ThymeleafContext(Locale locale, Map<String, Object> vars) {
		this(locale);
		varMap.putAll(vars);
	}

	@SafeVarargs
	public ThymeleafContext(Locale locale, Pair<String, Object>... vars) {
		this.locale = locale;
		for (Pair<String, Object> var : vars) {
			varMap.put(var.getKey(), var.getValue());
		}
	}

	public ThymeleafContext(Locale locale) {
		this.locale = locale;
	}

	@Override
	public void addContextExecutionInfo(String arg) {
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public VariablesMap<String, Object> getVariables() {
		return varMap;
	}
}
