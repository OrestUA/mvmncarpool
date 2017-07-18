package x.mvmn.carpool.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class LiftRequest {

	protected int id;
	protected long timeValidFrom;
	protected long timeValidTo;
	protected double lat;
	protected double lon;
	protected User user;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTimeValidFrom() {
		return timeValidFrom;
	}

	public void setTimeValidFrom(long timeValidFrom) {
		this.timeValidFrom = timeValidFrom;
	}

	public long getTimeValidTo() {
		return timeValidTo;
	}

	public void setTimeValidTo(long timeValidTo) {
		this.timeValidTo = timeValidTo;
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

	@ManyToOne(optional = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
