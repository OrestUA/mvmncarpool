package x.mvmn.carpool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import x.mvmn.carpool.model.User;
import x.mvmn.carpool.service.persistence.UserRepository;

@Service
public class UserConfirmationService {

	public static final String CONFIRMATION_ID_PARAM_NAME = "confirmation_id";

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	UserRepository userRepository;

	public String sendConfirmationRequest(User user) {
		String requestId = encoder.encode(user.getEmailAddress() + "//" + System.currentTimeMillis());
		// TODO: implement sending of email
		return requestId;
	}

	public User validateConfirmationResponse(String email, String requestId) {
		User user = userRepository.findByEmailAddress(email);
		return (user != null && requestId.equals(user.getConfirmationRequestId())) ? user : null;
	}
}
