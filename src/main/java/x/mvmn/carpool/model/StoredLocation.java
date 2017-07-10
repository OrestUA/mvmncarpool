package x.mvmn.carpool.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class StoredLocation {

	protected int id;
	protected double lat;
	protected double lon;
	protected User user;
	protected String name;
	protected String description;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	protected double getLat() {
		return lat;
	}

	protected void setLat(double lat) {
		this.lat = lat;
	}

	protected double getLon() {
		return lon;
	}

	protected void setLon(double lon) {
		this.lon = lon;
	}

	@ManyToOne(optional = false)
	protected User getUser() {
		return user;
	}

	protected void setUser(User user) {
		this.user = user;
	}

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected String getDescription() {
		return description;
	}

	protected void setDescription(String description) {
		this.description = description;
	}
}
