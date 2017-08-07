package x.mvmn.carpool.web.dto;

import x.mvmn.carpool.model.LiftRequest;

public class LiftRequestDTO {

	protected int id;
	protected long timeValidFrom;
	protected long timeValidTo;
	protected double lat;
	protected double lon;
	protected int userId;
	protected String address;
	protected String notes;
	protected String placeId;

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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public static LiftRequestDTO fromLiftRequest(LiftRequest liftRequest) {
		LiftRequestDTO result = new LiftRequestDTO();
		result.setId(liftRequest.getId());
		result.setLat(liftRequest.getLat());
		result.setLon(liftRequest.getLon());
		result.setTimeValidFrom(liftRequest.getTimeValidFrom());
		result.setTimeValidTo(liftRequest.getTimeValidTo());
		result.setNotes(liftRequest.getNotes());
		result.setAddress(liftRequest.getAddress());
		result.setPlaceId(liftRequest.getPlaceId());
		result.setUserId(liftRequest.getUser().getId());

		return result;
	}
}
