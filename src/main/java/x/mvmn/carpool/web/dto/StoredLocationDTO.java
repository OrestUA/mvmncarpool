package x.mvmn.carpool.web.dto;

import x.mvmn.carpool.model.StoredLocation;

public class StoredLocationDTO {

	protected int id;
	protected double lat;
	protected double lon;
	protected int userId;
	protected String name;
	protected String description;

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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static StoredLocationDTO fromStoredLocation(StoredLocation storedLocation) {
		StoredLocationDTO result = new StoredLocationDTO();

		result.setId(storedLocation.getId());
		result.setLat(storedLocation.getLat());
		result.setLon(storedLocation.getLon());
		result.setName(storedLocation.getName());
		result.setDescription(storedLocation.getDescription());
		result.setUserId(storedLocation.getUser() != null ? storedLocation.getUser().getId() : 0);

		return result;
	}
}
