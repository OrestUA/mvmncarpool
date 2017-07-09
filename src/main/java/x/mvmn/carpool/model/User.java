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
	protected String emailAddress;
	protected String password;
	protected Boolean confirmed;
	protected String confirmationRequestId;
	protected String passwordResetRequestId;
	protected long passwordResetRequestUnixTime;
	protected List<Route> routes;
	protected List<LiftRequest> liftRequests;
	protected List<LiftOffer> liftOffer;
	protected List<LiftJoinRequest> liftJoinRequests;
	protected List<Car> cars;

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

	public String getConfirmationRequestId() {
		return confirmationRequestId;
	}

	public void setConfirmationRequestId(String confirmationRequestId) {
		this.confirmationRequestId = confirmationRequestId;
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
	protected List<Route> getRoutes() {
		return routes;
	}

	protected void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
	protected List<LiftRequest> getLiftRequests() {
		return liftRequests;
	}

	protected void setLiftRequests(List<LiftRequest> liftRequests) {
		this.liftRequests = liftRequests;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
	protected List<LiftOffer> getLiftOffer() {
		return liftOffer;
	}

	protected void setLiftOffer(List<LiftOffer> liftOffer) {
		this.liftOffer = liftOffer;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
	protected List<LiftJoinRequest> getLiftJoinRequests() {
		return liftJoinRequests;
	}

	protected void setLiftJoinRequests(List<LiftJoinRequest> liftJoinRequests) {
		this.liftJoinRequests = liftJoinRequests;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "owner")
	protected List<Car> getCars() {
		return cars;
	}

	protected void setCars(List<Car> cars) {
		this.cars = cars;
	}
}
