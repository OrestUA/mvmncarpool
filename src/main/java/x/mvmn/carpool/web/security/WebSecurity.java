package x.mvmn.carpool.web.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

import x.mvmn.carpool.model.User;
import x.mvmn.carpool.service.persistence.UserRepository;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurity {

	@Configuration
	@Order(1)
	public static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().and().antMatcher("/api/**");
		}
	}

	@Configuration
	@Order(2)
	public static class LandingPagesSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).and().formLogin().defaultSuccessUrl("/").usernameParameter("email")
					.passwordParameter("password").loginPage("/signin").successHandler(successHandler()).failureHandler(failureHandler())
					.loginProcessingUrl("/login").and().csrf().ignoringAntMatchers("/h2db/*").and().headers().frameOptions().sameOrigin().and()
					.requestMatcher(new NegatedRequestMatcher(new AntPathRequestMatcher("/api/**")));
		}

		protected AuthenticationFailureHandler failureHandler() {
			return new AuthenticationFailureHandler() {
				@Override
				public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
						throws IOException, ServletException {
					response.setStatus(401);
				}
			};
		}

		protected AuthenticationSuccessHandler successHandler() {
			return new AuthenticationSuccessHandler() {
				@Override
				public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
						throws IOException, ServletException {
					response.setStatus(200);
				}
			};
		}
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, final UserRepository userRepository, final PasswordEncoder passwordEncoder)
			throws Exception {
		auth.userDetailsService(new UserDetailsService() {
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				User user = userRepository.findByEmailAddress(username);
				if (user == null || user.getConfirmed() == null || !user.getConfirmed().booleanValue()) {
					throw new UsernameNotFoundException("User not found for name: " + username);
				}
				return new UserDetailsAdaptor(user);
			}
		}).passwordEncoder(passwordEncoder);
	}

	public static class UserDetailsAdaptor implements UserDetails {
		private static final long serialVersionUID = 7142434952822513491L;
		protected final User user;

		protected static final GrantedAuthority AUTHORITY_ROLE_USER = new GrantedAuthority() {
			private static final long serialVersionUID = 1926613739390249454L;

			public String getAuthority() {
				return "ROLE_USER";
			}
		};

		protected static final List<GrantedAuthority> AUTHORITIES_USER = Arrays.asList(AUTHORITY_ROLE_USER);

		public UserDetailsAdaptor(User user) {
			this.user = user;
		}

		public Collection<? extends GrantedAuthority> getAuthorities() {
			return AUTHORITIES_USER;
		}

		public String getPassword() {
			return user.getPassword();
		}

		public String getUsername() {
			return user.getEmailAddress();
		}

		public boolean isAccountNonExpired() {
			return true;
		}

		public boolean isAccountNonLocked() {
			return true;
		}

		public boolean isCredentialsNonExpired() {
			return true;
		}

		public boolean isEnabled() {
			return user.getConfirmed() != null && user.getConfirmed().booleanValue();
		}

		public User getUser() {
			return user;
		}
	}
}
