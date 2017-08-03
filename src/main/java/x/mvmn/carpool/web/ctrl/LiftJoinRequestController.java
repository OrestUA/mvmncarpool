package x.mvmn.carpool.web.ctrl;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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

import x.mvmn.carpool.model.LiftJoinRequest;
import x.mvmn.carpool.model.LiftOffer;
import x.mvmn.carpool.model.LiftRequest;
import x.mvmn.carpool.model.User;
import x.mvmn.carpool.service.persistence.LiftJoinRequestRepository;
import x.mvmn.carpool.service.persistence.LiftOfferRepository;
import x.mvmn.carpool.service.persistence.LiftRequestRepository;
import x.mvmn.carpool.service.persistence.UserRepository;
import x.mvmn.carpool.web.dto.GenericResultDTO;
import x.mvmn.carpool.web.dto.LiftJoinRequestDTO;
import x.mvmn.util.web.auth.UserUtil;

@RestController
public class LiftJoinRequestController {

	@Autowired
	LiftJoinRequestRepository liftJoinRequestRepository;

	@Autowired
	LiftOfferRepository liftOfferRepository;

	@Autowired
	LiftRequestRepository liftRequestRepository;

	@Autowired
	UserRepository userRepository;

	@RequestMapping(path = "/api/liftjoinrequest/issued", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody List<LiftJoinRequestDTO> listIssuedLiftJoinRequests(Authentication auth,
			@RequestParam(name = "liftOfferId", required = false) Integer liftOfferId, @RequestParam(name = "actualOnly", required = false) Boolean actualOnly,
			@RequestParam(name = "driverInitiated", required = false) Boolean driverInitiated,
			@RequestParam(name = "approvalState", required = false) Boolean approvalState, @RequestParam(name = "pending", required = false) Boolean pending) {
		return doListLiftJoinRequests(UserUtil.getCurrentUser(auth).getId(), liftOfferId, actualOnly, driverInitiated, approvalState, pending, false);
	}

	@RequestMapping(path = "/api/liftjoinrequest/received", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody List<LiftJoinRequestDTO> listReceivedLiftJoinRequests(Authentication auth,
			@RequestParam(name = "liftOfferId", required = false) Integer liftOfferId, @RequestParam(name = "actualOnly", required = false) Boolean actualOnly,
			@RequestParam(name = "driverInitiated", required = false) Boolean driverInitiated,
			@RequestParam(name = "approvalState", required = false) Boolean approvalState, @RequestParam(name = "pending", required = false) Boolean pending) {
		return doListLiftJoinRequests(UserUtil.getCurrentUser(auth).getId(), liftOfferId, actualOnly, driverInitiated, approvalState, pending, true);
	}

	protected @ResponseBody List<LiftJoinRequestDTO> doListLiftJoinRequests(int currentUserId, Integer liftOfferId, Boolean actualOnly, Boolean driverInitiated,
			Boolean approvalState, Boolean pending, boolean received) {
		Specifications<LiftJoinRequest> searchSpec = Specifications
				.where(received ? LiftJoinRequestRepository.specDriverUserId(currentUserId) : LiftJoinRequestRepository.specPassengerUserId(currentUserId));
		if (liftOfferId != null) {
			searchSpec = searchSpec.and(LiftJoinRequestRepository.specLiftOfferId(liftOfferId));
		}
		if (actualOnly != null && actualOnly.booleanValue()) {
			searchSpec = searchSpec.and(LiftJoinRequestRepository.specValidAfter(System.currentTimeMillis() / 1000));
		}
		if (driverInitiated != null) {
			searchSpec = searchSpec.and(LiftJoinRequestRepository.specDriverInitiated(driverInitiated));
		}
		if (approvalState != null) {
			searchSpec = searchSpec.and(LiftJoinRequestRepository.specApproved(approvalState));
		}
		if (pending != null && pending.booleanValue()) {
			searchSpec = searchSpec.and(LiftJoinRequestRepository.specApprovedIsNull());
		}

		return liftJoinRequestRepository.findAll(searchSpec).stream().map(LiftJoinRequestDTO::fromLiftJoinRequest).collect(Collectors.toList());
	}

	@RequestMapping(path = "/api/liftjoinrequest/{id}/approve", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody GenericResultDTO approveRequest(Authentication auth, @PathVariable("id") int requestId, HttpServletResponse response) {
		return doApproveOrRejectRequest(requestId, UserUtil.getCurrentUser(auth).getId(), response, true);
	}

	@RequestMapping(path = "/api/liftjoinrequest/{id}/reject", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody GenericResultDTO rejectRequest(Authentication auth, @PathVariable("id") int requestId, HttpServletResponse response) {
		return doApproveOrRejectRequest(requestId, UserUtil.getCurrentUser(auth).getId(), response, false);
	}

	protected @ResponseBody GenericResultDTO doApproveOrRejectRequest(int requestId, int currentUserId, HttpServletResponse response, boolean approve) {
		GenericResultDTO result = new GenericResultDTO();
		result.success = false;

		LiftJoinRequest ljr = liftJoinRequestRepository.findOne(requestId);
		if (ljr != null) {
			// I can approve/reject if:
			// A - driver created request for me to join;
			// B - passenger created request and I'm the driver.
			if (ljr.isDriverInitiated() && ljr.getUser().getId() == currentUserId
					|| !ljr.isDriverInitiated() && ljr.getOffer().getUser().getId() == currentUserId) {
				ljr.setApproved(approve);
				liftJoinRequestRepository.save(ljr);

				result.success = true;
				result.message = "ok";
			} else {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				result.message = "You cannot approve/reject lift join request that was not created for you";
			}
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			result.message = "Lift join request not found by specified ID";
		}

		return result;
	}

	@RequestMapping(path = "/api/liftjoinrequest/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	protected @ResponseBody GenericResultDTO deleteRequest(int requestId, Authentication auth, HttpServletResponse response, boolean approve) {
		int currentUserId = UserUtil.getCurrentUser(auth).getId();
		GenericResultDTO result = new GenericResultDTO();
		result.success = false;

		LiftJoinRequest ljr = liftJoinRequestRepository.findOne(requestId);
		if (ljr != null) {
			// I can delete request if:
			// A - driver created request and I'm the driver;
			// B - passenger created request and I'm the passenger.
			if (ljr.isDriverInitiated() && ljr.getOffer().getUser().getId() == currentUserId
					|| !ljr.isDriverInitiated() && ljr.getUser().getId() == currentUserId) {
				liftJoinRequestRepository.delete(ljr);

				result.success = true;
				result.message = "ok";
			} else {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				result.message = "You cannot delete lift join request that was not created by you";
			}
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			result.message = "Lift join request not found by specified ID";
		}

		return result;
	}

	@RequestMapping(path = "/api/liftjoinrequest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody GenericResultDTO issueLiftJoinRequest(Authentication auth, HttpServletResponse response,
			@RequestBody LiftJoinRequestDTO liftJoinRequestDTO) {
		User currentUser = UserUtil.getCurrentUser(auth);
		GenericResultDTO result = new GenericResultDTO();
		result.success = false;

		LiftOffer liftOffer = liftOfferRepository.findOne(liftJoinRequestDTO.getLiftOfferId());
		if (liftOffer == null) {
			// TODO: not found
		} else {
			if (liftOffer.getJoinRequests().stream().filter(ljr -> ljr.getUser().getId() == currentUser.getId()).count() > 0) {
				// User already has join request TODO: bad request

			} else {
				boolean driverInitiated = liftOffer.getUser().getId() == currentUser.getId();
				User passenger;
				LiftRequest liftRequest = null;
				if (driverInitiated) {
					if (liftJoinRequestDTO.getLiftRequestId() != null) {
						liftRequest = liftRequestRepository.findOne(liftJoinRequestDTO.getLiftRequestId());
					}
					if (liftRequest == null) {
						// TODO: bad request - must specify which lift request is this join offer for
						return result;
					} else {
						passenger = liftRequest.getUser();
					}
				} else {
					passenger = currentUser;
				}
				LiftJoinRequest ljr = new LiftJoinRequest();
				ljr.setUser(passenger);
				ljr.setOffer(liftOffer);
				ljr.setApproved(null);
				ljr.setDriverInitiated(driverInitiated);
				ljr.setLiftRequest(liftRequest);
				liftJoinRequestRepository.save(ljr);
				result.success = true;
				result.message = "ok";
			}
		}

		return result;
	}
}
