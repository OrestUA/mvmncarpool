package x.mvmn.carpool.web.ctrl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import x.mvmn.carpool.service.persistence.LiftJoinRequestRepository;
import x.mvmn.carpool.web.dto.LiftJoinRequestDTO;
import x.mvmn.util.web.auth.UserUtil;

@RestController
public class LiftJoinRequestController {

	@Autowired
	LiftJoinRequestRepository liftJoinRequestRepository;

	@RequestMapping(path = "/api/liftjoinrequest/active/own", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody List<LiftJoinRequestDTO> listOwnLiftJoinRequests(Authentication auth) {
		return liftJoinRequestRepository.findByUserAndTimeValidToGreaterThanEqual(UserUtil.getCurrentUser(auth).getId(), System.currentTimeMillis()).stream()
				.map(LiftJoinRequestDTO::fromLiftJoinRequest).collect(Collectors.toList());
	}

}
