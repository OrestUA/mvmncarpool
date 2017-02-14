package x.mvmn.carpool.conf;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfiguration {

	@Value("${mail.configprops}")
	protected String propertiesPath;

	@Bean
	@Qualifier("javaxMailProps")
	public Properties javaxMailProps() {
		Properties props = new Properties();
		try {
			props.load(this.getClass().getResourceAsStream(propertiesPath));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load javax_mail.properties", e);
		}
		return props;
	}
}
