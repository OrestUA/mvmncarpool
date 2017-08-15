package x.mvmn.carpool.web.ctrl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import x.mvmn.carpool.model.Route;
import x.mvmn.carpool.model.RouteWaypoint;
import x.mvmn.carpool.model.User;
import x.mvmn.carpool.service.persistence.LiftOfferRepository;
import x.mvmn.carpool.service.persistence.RouteRepository;
import x.mvmn.carpool.service.persistence.RouteWaypointRepository;
import x.mvmn.carpool.web.dto.GenericResultDTO;
import x.mvmn.carpool.web.dto.RouteDTO;
import x.mvmn.carpool.web.dto.RouteWaypointDTO;
import x.mvmn.util.web.auth.UserUtil;

@RestController
public class RouteController {

	@Autowired
	RouteRepository routeRepository;

	@Autowired
	RouteWaypointRepository routeWaypointRepository;

	@Autowired
	LiftOfferRepository liftOfferRepository;

	@RequestMapping(path = "/api/route", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public List<RouteDTO> getRoutes(Authentication auth) {
		return routeRepository.findByUser(UserUtil.getPrincipal(auth).getUser()).stream().map(RouteDTO::fromRoute).collect(Collectors.toList());
	}

	@RequestMapping(path = "/api/route/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public RouteDTO getRoute(Authentication auth, @PathVariable("id") int id, HttpServletResponse response) {
		Route route = routeRepository.findOne(id);
		int currentUserId = UserUtil.getPrincipal(auth).getUser().getId();
		if (route.getUser().getId() != currentUserId) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		} else {
			return RouteDTO.fromRoute(route);
		}
	}

	@RequestMapping(path = "/api/route", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public GenericResultDTO insertFavouredRoute(Authentication auth, RouteDTO routeDTO) {
		GenericResultDTO result = new GenericResultDTO();

		User currentUser = UserUtil.getPrincipal(auth).getUser();
		Route route = new Route();
		route.setUser(currentUser);
		route.setFavoured(true);

		route.setTitle(routeDTO.getTitle());
		route.setOverviewPolyline(routeDTO.getOverviewPolyline());
		route.setStartLat(routeDTO.getStartLat());
		route.setStartLon(routeDTO.getStartLon());
		route.setEndLat(routeDTO.getEndLat());
		route.setEndLon(routeDTO.getEndLon());
		route.setWaypoints(Collections.emptyList());

		route = routeRepository.save(route);
		for (RouteWaypointDTO waypointDTO : routeDTO.getWaypoints()) {
			RouteWaypoint waypoint = new RouteWaypoint();
			waypoint.setRoute(route);
			waypoint.setLat(waypointDTO.getLat());
			waypoint.setLon(waypointDTO.getLon());
			waypoint.setUserSpecified(waypointDTO.isUserSpecified());
			routeWaypointRepository.save(waypoint);
		}

		result.data = route;

		return result;
	}

	@RequestMapping(path = "/api/route/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public GenericResultDTO deleteRoute(Authentication auth, @PathVariable("id") int id, HttpServletResponse response) {
		GenericResultDTO result = new GenericResultDTO();
		result.success = false;

		Route route = routeRepository.findOne(id);
		if (route == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			result.message = "Route not found by provided ID";
		} else if (route.getUser().getId() != UserUtil.getPrincipal(auth).getUser().getId()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			result.message = "Cannot delete route that belongs to a different user";
		} else if (liftOfferRepository.countByRoute(route) > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result.message = "Cannot delete route that has associated lift offers - delete lift offers first";
		} else {
			routeRepository.delete(route);
			result.success = true;
			result.message = "ok";
		}

		return result;
	}

	@RequestMapping(path = "/api/route/{id}/favoured", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public GenericResultDTO markRouteFavoured(Authentication auth, @PathVariable("id") int id, HttpServletResponse response) {
		return setRouteFavoured(auth, id, response, true);
	}

	@RequestMapping(path = "/api/route/{id}/favoured", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public GenericResultDTO unmarkRouteFavoured(Authentication auth, @PathVariable("id") int id, HttpServletResponse response) {
		return setRouteFavoured(auth, id, response, false);
	}

	protected GenericResultDTO setRouteFavoured(Authentication auth, int routeId, HttpServletResponse response, boolean favor) {
		GenericResultDTO result = new GenericResultDTO();
		result.success = false;
		Route route = routeRepository.findOne(routeId);
		if (route == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			result.message = "Route not found by provided ID";
		} else if (route.getUser().getId() != UserUtil.getPrincipal(auth).getUser().getId()) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			result.message = "Cannot update route that belongs to a different user";
		} else {
			route.setFavoured(favor);
			routeRepository.save(route);
			result.success = true;
			result.message = "ok";
		}
		return result;
	}
}
