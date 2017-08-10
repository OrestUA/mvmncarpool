package x.mvmn.carpool.web.ctrl;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RestController;

import x.mvmn.carpool.model.LiftJoinRequest;
import x.mvmn.carpool.model.LiftOffer;
import x.mvmn.carpool.model.LiftRequest;
import x.mvmn.carpool.model.Route;
import x.mvmn.carpool.model.RouteWaypoint;
import x.mvmn.carpool.model.User;
import x.mvmn.carpool.model.Vehicle;
import x.mvmn.carpool.service.EventService;
import x.mvmn.carpool.service.ProximitySearchService;
import x.mvmn.carpool.service.EventService.StateChangeType;
import x.mvmn.carpool.service.persistence.LiftOfferRepository;
import x.mvmn.carpool.service.persistence.LiftRequestRepository;
import x.mvmn.carpool.service.persistence.RouteRepository;
import x.mvmn.carpool.service.persistence.RouteWaypointRepository;
import x.mvmn.carpool.service.persistence.VehicleRepository;
import x.mvmn.carpool.web.dto.GenericResultDTO;
import x.mvmn.carpool.web.dto.LiftOfferDTO;
import x.mvmn.carpool.web.dto.RouteWaypointDTO;
import x.mvmn.util.CoordinatesUtil;
import x.mvmn.util.web.auth.UserUtil;

@RestController
public class LiftOfferController {

	@Autowired
	LiftOfferRepository liftOfferRepository;

	@Autowired
	LiftRequestRepository liftRequestRepository;

	@Autowired
	RouteRepository routeRepository;

	@Autowired
	RouteWaypointRepository routeWaypointRepository;

	@Autowired
	VehicleRepository vehicleRepository;

	@Autowired
	EventService eventService;

	@Autowired
	private ProximitySearchService proximitySearchService;

	@RequestMapping(path = "/api/liftOffer/own", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public List<LiftOfferDTO> getMyLiftOffers(Authentication auth, @RequestParam(name = "actualOnly", required = false) Boolean actualOnly) {
		return getLiftOffers(UserUtil.getCurrentUser(auth).getId(), false, actualOnly != null && actualOnly.booleanValue());
	}

	@RequestMapping(path = "/api/liftOffer/others", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public List<LiftOfferDTO> getOthersLiftOffers(Authentication auth, @RequestParam(name = "actualOnly", required = false) Boolean actualOnly) {
		return getLiftOffers(UserUtil.getCurrentUser(auth).getId(), true, actualOnly != null && actualOnly.booleanValue());
	}

	protected List<LiftOfferDTO> getLiftOffers(Integer userId, boolean excludeUser, boolean actualOnly) {
		List<LiftOffer> result;
		if (userId == null && !actualOnly) {
			result = liftOfferRepository.findAll();
		} else {
			List<Specification<LiftOffer>> specifications = new ArrayList<>();
			if (userId != null) {
				specifications.add(excludeUser ? LiftOfferRepository.specNotUserId(userId) : LiftOfferRepository.specUserId(userId));
			}
			if (actualOnly) {
				specifications.add(LiftOfferRepository.specValidAfter(System.currentTimeMillis() / 1000));
			}
			Specifications<LiftOffer> searchSpecs = Specifications.where(specifications.get(0));
			specifications.remove(0);
			for (Specification<LiftOffer> spec : specifications) {
				searchSpecs = searchSpecs.and(spec);
			}
			result = liftOfferRepository.findAll(searchSpecs);
		}

		return result.stream().map(LiftOfferDTO::fromLiftOffer).collect(Collectors.toList());
	}

	@RequestMapping(path = "/api/liftOffer/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public GenericResultDTO deleteLiftOffer(Authentication auth, @PathVariable("id") int id, HttpServletResponse response) {
		GenericResultDTO result = new GenericResultDTO();
		result.success = false;
		LiftOffer liftOffer = liftOfferRepository.findOne(id);
		if (liftOffer == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			result.message = "Lift offer not found by provided ID";
		} else if (liftOffer.getUser().getId() != UserUtil.getPrincipal(auth).getUser().getId()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			result.message = "Cannot delete lift offer that belongs to a different user";
		} else {
			liftOfferRepository.delete(liftOffer);
			result.success = true;
			result.message = "ok";

			eventService.notifyLiftJoinRequestStateChange(StateChangeType.DELETED, false,
					liftOffer.getJoinRequests().toArray(new LiftJoinRequest[liftOffer.getJoinRequests().size()]));
			eventService.notifyLiftOfferStatusChange(liftOffer, StateChangeType.DELETED);
		}

		return result;
	}

	@RequestMapping(path = "/api/liftOffer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public GenericResultDTO upsertLiftOffer(Authentication auth, @RequestBody LiftOfferDTO liftOfferDTO,
			@RequestParam(required = false, name = "routeId") Integer routeId, HttpServletResponse response) {
		User currentUser = UserUtil.getCurrentUser(auth);
		GenericResultDTO result = new GenericResultDTO();
		result.success = false;

		boolean newLiftOffer = false;
		LiftOffer liftOffer;
		if (liftOfferDTO.getId() > 0) {
			liftOffer = liftOfferRepository.findByIdFetchRouteAndWaypoints(liftOfferDTO.getId());
			if (liftOffer != null && liftOffer.getUser().getId() != currentUser.getId()) {
				liftOffer = null;
			}
		} else {
			liftOffer = new LiftOffer();
			liftOffer.setUser(currentUser);
			newLiftOffer = true;
		}

		Vehicle vehicle = vehicleRepository.findOne(liftOfferDTO.getVehicleId());
		if (vehicle == null || vehicle.getOwner().getId() != currentUser.getId()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result.message = "Vehicle belonging to current user not found by given ID";
		} else if (liftOffer == null) {
			// Can't update - lift offer for given ID either doesn't exist, or doesn't belong to current user.
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			result.message = "Lift offer belonging to current user not found by given ID";
		} else {
			Route storedRoute = null;
			if (routeId == null && liftOfferDTO.getRoute() != null) {
				storedRoute = liftOffer.getRoute();
				if (storedRoute != null && CoordinatesUtil.pathCoordinatesMatch(storedRoute, liftOfferDTO.getRoute())) {
					// reuse stored route since coordinates match, but update which points are user specified
					boolean needsUpdate = storedRoute.getTitle().equals(liftOfferDTO.getRoute().getTitle());
					for (int i = 0; i < storedRoute.getWaypoints().size(); i++) {
						boolean shouldBeUserSpecified = liftOfferDTO.getRoute().getWaypoints().get(i).isUserSpecified();
						RouteWaypoint wp = storedRoute.getWaypoints().get(i);
						if (wp.isUserSpecified() != shouldBeUserSpecified) {
							needsUpdate = true;
							wp.setUserSpecified(shouldBeUserSpecified);
						}
					}

					if (needsUpdate) {
						storedRoute.setTitle(liftOfferDTO.getRoute().getTitle());
						if (liftOffer.getRoute().getFavoured()) {
							// If route is favoured - we shouldn't change it. We'll create a copy of it instead.
							Route routeClone = new Route();
							routeClone.setStartLat(storedRoute.getStartLat());
							routeClone.setStartLon(storedRoute.getStartLon());
							routeClone.setEndLat(storedRoute.getEndLat());
							routeClone.setEndLon(storedRoute.getEndLon());
							routeClone.setUser(currentUser);
							routeClone.setTitle(storedRoute.getTitle());
							routeClone.setWaypoints(new ArrayList<>());
							routeClone = routeRepository.save(routeClone);
							for (RouteWaypoint wp : storedRoute.getWaypoints()) {
								RouteWaypoint wpClone = new RouteWaypoint();
								wpClone.setRoute(routeClone);
								wpClone.setLat(wp.getLat());
								wpClone.setLon(wp.getLon());
								wpClone.setUserSpecified(wp.isUserSpecified());
								routeClone.getWaypoints().add(wpClone);
							}
							routeClone.setWaypoints(routeWaypointRepository.save(routeClone.getWaypoints()));
							storedRoute = routeClone;
						} else {
							// Save updated waypoints and updated route
							storedRoute.setWaypoints(routeWaypointRepository.save(storedRoute.getWaypoints()));
							storedRoute = routeRepository.save(storedRoute);
						}
					}
				} else {
					// Create route and waypoints
					storedRoute = new Route();
					storedRoute.setStartLat(liftOfferDTO.getRoute().getStartLat());
					storedRoute.setStartLon(liftOfferDTO.getRoute().getStartLon());
					storedRoute.setEndLat(liftOfferDTO.getRoute().getEndLat());
					storedRoute.setEndLon(liftOfferDTO.getRoute().getEndLon());
					storedRoute.setTitle(liftOfferDTO.getRoute().getTitle());
					storedRoute.setUser(currentUser);
					storedRoute.setWaypoints(new ArrayList<>());
					storedRoute = routeRepository.save(storedRoute);
					for (RouteWaypointDTO wpDTO : liftOfferDTO.getRoute().getWaypoints()) {
						RouteWaypoint wp = new RouteWaypoint();
						wp.setRoute(storedRoute);
						wp.setLat(wpDTO.getLat());
						wp.setLon(wpDTO.getLon());
						wp.setUserSpecified(wpDTO.isUserSpecified());
						storedRoute.getWaypoints().add(wp);
					}
					storedRoute.setWaypoints(routeWaypointRepository.save(storedRoute.getWaypoints()));
				}
			} else {
				if (routeId != null) {
					// Use routeId, if not present or belongs to different user - issue bad request
					Route route = routeRepository.findOne(routeId);
					if (route != null && route.getUser().getId() != currentUser.getId()) {
						route = null;
					}
					storedRoute = route;
				}
			}
			if (storedRoute == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				result.message = "Route coordinates not specified and no valid route ID provided for existing user's route.";
			} else {
				liftOffer.setTimeValidFrom(liftOfferDTO.getTimeValidFrom());
				liftOffer.setTimeValidTo(liftOfferDTO.getTimeValidTo());
				liftOffer.setVacantSeats(liftOfferDTO.getVacantSeats());
				liftOffer.setVehicle(vehicle);
				liftOffer.setRoute(storedRoute);
				liftOffer = liftOfferRepository.save(liftOffer);
				result.success = true;
				result.message = "ok";
				result.data = LiftOfferDTO.fromLiftOffer(liftOffer);

				eventService.notifyLiftOfferStatusChange(liftOffer, newLiftOffer ? StateChangeType.CREATED : StateChangeType.UPDATED);
			}
		}

		return result;
	}

	@RequestMapping(path = "/api/liftOffer/findNearPoint", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public List<LiftOfferDTO> findNearLiftRequest(@RequestParam("liftRequestId") int liftRequestId, @RequestParam("distanceKm") double distanceKm,
			HttpServletResponse response) {
		LiftRequest liftRequest = liftRequestRepository.findOne(liftRequestId);
		if (liftRequest == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} else {
			if (distanceKm > 10.0) {
				distanceKm = 10.0;
			}
			return proximitySearchService.findLiftOffersForCoordinates(liftRequest.getLat(), liftRequest.getLon(), distanceKm).stream()
					.map(LiftOfferDTO::fromLiftOffer).collect(Collectors.toList());
		}
	}
}
