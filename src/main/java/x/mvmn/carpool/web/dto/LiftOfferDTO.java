package x.mvmn.carpool.web.dto;

import x.mvmn.carpool.model.LiftOffer;

public class LiftOfferDTO {

	protected int id;
	protected int userId;
	protected RouteDTO route;
	protected int vehicleId;
	protected int vacantSeats;
	protected long timeValidFrom;
	protected long timeValidTo;
	protected String notes;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public RouteDTO getRoute() {
		return route;
	}

	public void setRoute(RouteDTO route) {
		this.route = route;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public int getVacantSeats() {
		return vacantSeats;
	}

	public void setVacantSeats(int vacantSeats) {
		this.vacantSeats = vacantSeats;
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public static LiftOfferDTO fromLiftOffer(LiftOffer liftOffer) {
		LiftOfferDTO result = new LiftOfferDTO();

		result.setId(liftOffer.getId());
		result.setTimeValidFrom(liftOffer.getTimeValidFrom());
		result.setTimeValidTo(liftOffer.getTimeValidTo());
		result.setVacantSeats(liftOffer.getVacantSeats());
		result.setVehicleId(liftOffer.getVehicle() != null ? liftOffer.getVehicle().getId() : 0);
		result.setUserId(liftOffer.getUser() != null ? liftOffer.getUser().getId() : 0);
		result.setRoute(liftOffer.getRoute() != null ? RouteDTO.fromRoute(liftOffer.getRoute()) : null);
		result.setNotes(liftOffer.getNotes());

		return result;
	}
}
