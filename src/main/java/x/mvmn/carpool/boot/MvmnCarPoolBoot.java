package x.mvmn.carpool.boot;

import org.h2.server.web.WebServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "x.mvmn.carpool")
@EnableJpaRepositories("x.mvmn.carpool")
@EntityScan("x.mvmn.carpool")
@EnableSwagger2
public class MvmnCarPoolBoot {

	public static void main(String args[]) {
		SpringApplication.run(MvmnCarPoolBoot.class, args);
	}

	@Bean
	@Profile("dev")
	ServletRegistrationBean h2servletRegistration() {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
		registrationBean.addUrlMappings("/h2db/*");
		return registrationBean;
	}

	@Bean
	@Profile("dev")
	public Docket petApi() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build().pathMapping("/");
	}
}
