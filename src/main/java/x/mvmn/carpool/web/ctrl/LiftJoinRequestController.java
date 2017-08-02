package x.mvmn.carpool.web.ctrl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import x.mvmn.carpool.model.LiftJoinRequest;
import x.mvmn.carpool.service.persistence.LiftJoinRequestRepository;
import x.mvmn.carpool.web.dto.LiftJoinRequestDTO;
import x.mvmn.util.web.auth.UserUtil;

@RestController
public class LiftJoinRequestController {

	@Autowired
	LiftJoinRequestRepository liftJoinRequestRepository;

	@RequestMapping(path = "/api/liftjoinrequest/own", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody List<LiftJoinRequestDTO> listOwnLiftJoinRequests(Authentication auth,
			@RequestParam(name = "actualOnly", required = false) Boolean actualOnly,
			@RequestParam(name = "driverInitiated", required = false) Boolean driverInitiated,
			@RequestParam(name = "approved", required = false) Boolean approved) {
		int currentUserId = UserUtil.getCurrentUser(auth).getId();
		Specifications<LiftJoinRequest> searchSpec = Specifications.where(LiftJoinRequestRepository.specUserId(currentUserId));
		if (actualOnly != null && actualOnly.booleanValue()) {
			searchSpec = searchSpec.and(LiftJoinRequestRepository.specValidAfter(System.currentTimeMillis() / 1000));
		}
		if (driverInitiated != null) {
			searchSpec = searchSpec.and(LiftJoinRequestRepository.specDriverInitiated(driverInitiated));
		}
		if (approved != null) {
			searchSpec = searchSpec.and(LiftJoinRequestRepository.specApproved(approved));
		}

		return liftJoinRequestRepository.findAll(searchSpec).stream().map(LiftJoinRequestDTO::fromLiftJoinRequest).collect(Collectors.toList());
	}

}
