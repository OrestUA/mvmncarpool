package x.mvmn.carpool.l10n;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class ExtReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
	public Map<String, String> getAll(Locale locale) {
		Map<String, String> result = new TreeMap<>();
		if (this.getCacheMillis() < 0) {
			Properties props = this.getMergedProperties(locale).getProperties();
			for (Object keyObj : props.keySet()) {
				String key = keyObj.toString();
				result.put(key, props.getProperty(key));
			}
		} else {
			for (String basename : getBasenameSet()) {
				List<String> filenames = calculateAllFilenames(basename, locale);
				for (String filename : filenames) {
					PropertiesHolder propHolder = getProperties(filename);
					if (propHolder.getProperties() != null) {
						for (Object key : propHolder.getProperties().keySet()) {
							if (!result.containsKey(key.toString())) {
								result.put(key.toString(), propHolder.getProperty(key.toString()));
							}
						}
					}
				}
			}
		}
		return result;
	}
}
