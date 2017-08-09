package x.mvmn.carpool.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import x.mvmn.carpool.model.DrivePath;
import x.mvmn.carpool.model.Route;

public class RouteDTO implements DrivePath {

	protected int id;
	protected String title;
	protected Boolean favoured;
	protected double startLat;
	protected double startLon;
	protected double endLat;
	protected double endLon;
	protected List<RouteWaypointDTO> waypoints;
	protected int userId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getFavoured() {
		return favoured;
	}

	public void setFavoured(Boolean favoured) {
		this.favoured = favoured;
	}

	public double getStartLat() {
		return startLat;
	}

	public void setStartLat(double startLat) {
		this.startLat = startLat;
	}

	public double getStartLon() {
		return startLon;
	}

	public void setStartLon(double startLon) {
		this.startLon = startLon;
	}

	public double getEndLat() {
		return endLat;
	}

	public void setEndLat(double endLat) {
		this.endLat = endLat;
	}

	public double getEndLon() {
		return endLon;
	}

	public void setEndLon(double endLon) {
		this.endLon = endLon;
	}

	public List<RouteWaypointDTO> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<RouteWaypointDTO> waypoints) {
		this.waypoints = waypoints;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public static RouteDTO fromRoute(Route route) {
		RouteDTO result = new RouteDTO();

		result.setId(route.getId());
		result.setStartLat(route.getStartLat());
		result.setStartLon(route.getStartLon());
		result.setEndLat(route.getEndLat());
		result.setEndLon(route.getEndLon());
		result.setTitle(route.getTitle());
		result.setUserId(route.getUser().getId());
		result.setFavoured(route.getFavoured());
		result.setWaypoints(route.getWaypoints().stream().map(RouteWaypointDTO::fromRouteWaypoint).collect(Collectors.toList()));

		return result;
	}
}
