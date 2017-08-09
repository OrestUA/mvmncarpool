package x.mvmn.carpool.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import x.mvmn.carpool.model.LiftOffer;
import x.mvmn.carpool.model.LiftRequest;
import x.mvmn.carpool.model.Route;
import x.mvmn.carpool.model.RouteWaypoint;
import x.mvmn.carpool.service.persistence.LiftOfferRepository;
import x.mvmn.carpool.service.persistence.LiftRequestRepository;
import x.mvmn.carpool.service.persistence.RouteRepository;

@Service
public class ProximitySearchService {

	protected static final Double V_ANGLE_PER_KILOMETER = 40075.0 / 360; // 40 075 km is earth circumference

	@Autowired
	RouteRepository routeRepository;

	@Autowired
	LiftOfferRepository liftOfferRepository;

	@Autowired
	LiftRequestRepository liftRequestRepository;

	public List<LiftOffer> findLiftOffersForCoordinates(double lat, double lon, double distanceKm) {
		double hAngleDelta = hAngleDelta(distanceKm, lat);
		double vAngleDelta = vAngleDelta(distanceKm);

		List<Route> routes = routeRepository.findByLatLngInRange(lat - hAngleDelta / 2, lat + hAngleDelta / 2, lon - vAngleDelta / 2, lon + vAngleDelta / 2);

		if (routes != null && routes.size() > 0) {
			return liftOfferRepository.findAll(Specifications.where(LiftOfferRepository.specValidAfter(System.currentTimeMillis() / 1000))
					.and(LiftOfferRepository.specRouteIn(routes.stream().map(Route::getId).collect(Collectors.toList()))));
		} else {
			return Collections.emptyList();
		}
	}

	protected static class LatLngRange {
		public double latFrom;
		public double latTo;
		public double lngFrom;
		public double lngTo;

		public LatLngRange(double lat, double lng, double distanceKm) {
			double hAngleDelta = hAngleDelta(distanceKm, lat);
			double vAngleDelta = vAngleDelta(distanceKm);

			latFrom = lat - hAngleDelta / 2;
			latTo = lat + hAngleDelta / 2;
			lngFrom = lng - vAngleDelta / 2;
			lngTo = lng + vAngleDelta / 2;
		}

		public LatLngRange(double latFrom, double latTo, double lngFrom, double lngTo) {
			this.latFrom = latFrom;
			this.latTo = latTo;
			this.lngFrom = lngFrom;
			this.lngTo = lngTo;
		}

		public boolean in(double lat, double lng) {
			return lat > latFrom && lat < latTo && lng > lngFrom && lng < lngTo;
		}
	}

	protected static class LatLngRangeList {
		public List<LatLngRange> list = new ArrayList<>();

		public boolean inAny(double lat, double lng) {
			return list.stream().anyMatch(llr -> llr.in(lat, lng));
		}
	}

	public List<LiftRequest> findLiftRequestsNearRoute(Route route, double distanceKm) {
		List<LiftRequest> result = new ArrayList<>();
		LatLngRangeList llrl = new LatLngRangeList();

		llrl.list.add(new LatLngRange(route.getStartLat(), route.getStartLon(), distanceKm));
		llrl.list.add(new LatLngRange(route.getEndLat(), route.getEndLon(), distanceKm));

		for (RouteWaypoint wp : route.getWaypoints()) {
			if (!llrl.inAny(wp.getLat(), wp.getLon())) {
				llrl.list.add(new LatLngRange(wp.getLat(), wp.getLon(), distanceKm));
			}
		}

		for (LatLngRange llr : llrl.list) {
			result.addAll(
					liftRequestRepository.findByLatLngInRangeAndValidAfter(llr.latFrom, llr.latTo, llr.lngFrom, llr.lngTo, System.currentTimeMillis() / 1000));
		}

		return result;
	}

	protected static double hAngleDelta(double distanceKm, double lat) {
		return distanceKm * 360 / (2 * Math.PI * Math.sin(Math.toRadians(90 - Math.abs(lat))) * 6371);
	}

	protected static double vAngleDelta(double distanceKm) {
		return distanceKm * V_ANGLE_PER_KILOMETER;
	}
}
