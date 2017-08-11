package x.mvmn.carpool.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class LiftOffer {

	protected int id;
	protected User user;
	protected Route route;
	protected Vehicle vehicle;
	protected int vacantSeats = 1;
	protected List<LiftJoinRequest> joinRequests;
	protected long timeValidFrom;
	protected long timeValidTo;
	protected String notes;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(optional = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(optional = false)
	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	@ManyToOne(optional = false)
	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public int getVacantSeats() {
		return vacantSeats;
	}

	public void setVacantSeats(int vacantSeats) {
		this.vacantSeats = vacantSeats;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "offer")
	public List<LiftJoinRequest> getJoinRequests() {
		return joinRequests;
	}

	public void setJoinRequests(List<LiftJoinRequest> joinRequests) {
		this.joinRequests = joinRequests;
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
}
