package x.mvmn.util.web.auth;

import org.springframework.security.core.Authentication;

import x.mvmn.carpool.model.User;
import x.mvmn.carpool.web.security.WebSecurity.UserDetailsAdaptor;

public class UserUtil {

	public static UserDetailsAdaptor getPrincipal(Authentication auth) {
		return auth != null ? ((UserDetailsAdaptor) auth.getPrincipal()) : null;
	}

	public static User getCurrentUser(Authentication auth) {
		UserDetailsAdaptor userDetailsAdaptor = getPrincipal(auth);
		return userDetailsAdaptor != null ? userDetailsAdaptor.getUser() : null;
	}
}
