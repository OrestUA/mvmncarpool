package x.mvmn.carpool.web.dto;

import x.mvmn.carpool.model.RouteWaypoint;
import x.mvmn.carpool.model.DrivePath.PointCoordinates;

public class RouteWaypointDTO implements PointCoordinates {

	protected int id;
	protected double lat;
	protected double lon;
	protected boolean userSpecified;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public boolean isUserSpecified() {
		return userSpecified;
	}

	public void setUserSpecified(boolean userSpecified) {
		this.userSpecified = userSpecified;
	}

	public static RouteWaypointDTO fromRouteWaypoint(RouteWaypoint rwp) {
		RouteWaypointDTO result = new RouteWaypointDTO();

		result.setId(rwp.getId());
		result.setLat(rwp.getLat());
		result.setLon(rwp.getLon());
		result.setUserSpecified(rwp.isUserSpecified());

		return result;
	}
}
