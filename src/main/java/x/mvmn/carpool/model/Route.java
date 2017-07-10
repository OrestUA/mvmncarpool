package x.mvmn.carpool.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

@Entity
public class Route {

	protected int id;
	protected String title;
	protected Boolean favoured;
	protected double startLat;
	protected double startLon;
	protected double endLat;
	protected double endLon;
	protected List<RouteWaypoint> waypoints;
	protected User user;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "route")
	@OrderColumn(name = "waypoint_index")
	public List<RouteWaypoint> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<RouteWaypoint> waypoints) {
		this.waypoints = waypoints;
	}

	@ManyToOne(optional = false)
	protected User getUser() {
		return user;
	}

	protected void setUser(User user) {
		this.user = user;
	}
}
