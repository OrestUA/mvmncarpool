package x.mvmn.carpool.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class User {

	protected int id;
	protected String fullName;
	protected String emailAddress;
	protected String password;
	protected Boolean confirmed;
	protected String passwordResetRequestId;
	protected long passwordResetRequestUnixTime;
	protected List<Route> routes;
	protected List<LiftRequest> liftRequests;
	protected List<LiftOffer> liftOffer;
	protected List<LiftJoinRequest> liftJoinRequests;
	protected List<Vehicle> vehicles;
	protected List<StoredLocation> storedLocations;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "email_address", unique = true)
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Column(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	public String getPasswordResetRequestId() {
		return passwordResetRequestId;
	}

	public void setPasswordResetRequestId(String passwordResetRequestId) {
		this.passwordResetRequestId = passwordResetRequestId;
	}

	public long getPasswordResetRequestUnixTime() {
		return passwordResetRequestUnixTime;
	}

	public void setPasswordResetRequestUnixTime(long passwordResetRequestUnixTime) {
		this.passwordResetRequestUnixTime = passwordResetRequestUnixTime;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
	public List<LiftRequest> getLiftRequests() {
		return liftRequests;
	}

	public void setLiftRequests(List<LiftRequest> liftRequests) {
		this.liftRequests = liftRequests;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
	public List<LiftOffer> getLiftOffer() {
		return liftOffer;
	}

	public void setLiftOffer(List<LiftOffer> liftOffer) {
		this.liftOffer = liftOffer;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
	public List<LiftJoinRequest> getLiftJoinRequests() {
		return liftJoinRequests;
	}

	public void setLiftJoinRequests(List<LiftJoinRequest> liftJoinRequests) {
		this.liftJoinRequests = liftJoinRequests;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "owner")
	public List<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
	public List<StoredLocation> getStoredLocations() {
		return storedLocations;
	}

	public void setStoredLocations(List<StoredLocation> storedLocations) {
		this.storedLocations = storedLocations;
	}

	@Column(name = "full_name", length = 256)
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
