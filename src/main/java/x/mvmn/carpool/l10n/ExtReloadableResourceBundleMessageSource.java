package x.mvmn.carpool.l10n;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class ExtReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
	public Map<String, String> getAll(Locale locale) {
		Map<String, String> result = new TreeMap<>();
		Properties props = this.getMergedProperties(locale).getProperties();
		for (Object keyObj : props.keySet()) {
			String key = keyObj.toString();
			result.put(key, props.getProperty(key));
		}
		return result;
	}
}
