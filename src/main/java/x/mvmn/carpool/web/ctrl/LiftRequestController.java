package x.mvmn.carpool.web.ctrl;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import x.mvmn.carpool.model.LiftRequest;
import x.mvmn.carpool.model.User;
import x.mvmn.carpool.service.persistence.LiftRequestRepository;
import x.mvmn.carpool.web.dto.GenericResultDTO;
import x.mvmn.carpool.web.dto.LiftRequestDTO;
import x.mvmn.util.web.auth.UserUtil;

@RestController
public class LiftRequestController {
	// private static final Logger LOGGER = LoggerFactory.getLogger(LiftRequestController.class);

	@Autowired
	private LiftRequestRepository liftRequestRepository;

	@RequestMapping(path = "/api/liftrequest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody List<LiftRequestDTO> listOwnLiftRequests(Authentication auth, @RequestParam(name = "activeOnly", required = false) Boolean activeOnly,
			@RequestParam(name = "ownOnly", required = false) Boolean ownOnly, @RequestParam(name = "userId", required = false) Integer userId) {
		// TODO: consider paging
		List<LiftRequest> liftRequests;
		Specifications<LiftRequest> searchSpecs = null;
		if (activeOnly != null && activeOnly.booleanValue()) {
			searchSpecs = Specifications.where(LiftRequestRepository.specValidAfter(System.currentTimeMillis() / 1000));
		}

		if (ownOnly != null && ownOnly.booleanValue()) {
			Specification<LiftRequest> spec = LiftRequestRepository.specUserId(UserUtil.getCurrentUser(auth).getId());
			searchSpecs = searchSpecs != null ? searchSpecs.and(spec) : Specifications.where(spec);
		} else if (userId != null) {
			Specification<LiftRequest> spec = LiftRequestRepository.specUserId(userId.intValue());
			searchSpecs = searchSpecs != null ? searchSpecs.and(spec) : Specifications.where(spec);
		}

		if (searchSpecs == null) {
			liftRequests = liftRequestRepository.findAll();
		} else {
			liftRequests = liftRequestRepository.findAll(searchSpecs);
		}

		return liftRequests.stream().map(LiftRequestDTO::fromLiftRequest).collect(Collectors.toList());
	}

	@RequestMapping(path = "/api/liftrequest/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody GenericResultDTO deleteLiftRequests(@PathVariable("id") int liftRequestId, Authentication auth, HttpServletResponse response) {
		GenericResultDTO result = new GenericResultDTO();
		result.success = false;
		User currentUser = UserUtil.getCurrentUser(auth);
		LiftRequest liftRequest = liftRequestRepository.findOne(liftRequestId);
		if (liftRequest == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			result.message = "Lift request not found by given ID";
		} else {
			if (liftRequest.getUser().getId() != currentUser.getId()) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				result.message = "Cannot delete lift request that belongs to other user";
			} else {
				liftRequestRepository.delete(liftRequest);
				result.success = true;
				result.message = "ok";
			}
		}
		return result;
	}

	@RequestMapping(path = "/api/liftrequest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody GenericResultDTO upsertLiftRequest(Authentication auth, @RequestBody LiftRequestDTO liftRequestDTO, HttpServletResponse response) {
		User currentUser = UserUtil.getCurrentUser(auth);
		GenericResultDTO result = new GenericResultDTO();
		result.success = false;

		LiftRequest liftRequest = null;
		if (liftRequestDTO.getId() > 0) {
			// update
			liftRequest = liftRequestRepository.findOne(liftRequestDTO.getId());
			if (liftRequest == null) {
				// lift request to update doesn't exist
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				result.message = "Lift request not found by given ID";
			} else if (liftRequest.getUser().getId() != currentUser.getId()) {
				// lift request to update doesn't belong to current user
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				result.message = "Cannot update lift request that belongs to other user";
				liftRequest = null;
			}
		} else {
			// insert
			liftRequest = new LiftRequest();
			liftRequest.setUser(currentUser);
		}

		if (liftRequest != null) {
			liftRequest.setLat(liftRequestDTO.getLat());
			liftRequest.setLon(liftRequestDTO.getLon());
			liftRequest.setTimeValidFrom(liftRequestDTO.getTimeValidFrom());
			liftRequest.setTimeValidTo(liftRequestDTO.getTimeValidTo());
			liftRequest.setNotes(liftRequestDTO.getNotes());
			liftRequest.setAddress(liftRequestDTO.getAddress());
			liftRequestRepository.save(liftRequest);
			result.success = true;
			result.message = "ok";
		}

		return result;
	}
}
