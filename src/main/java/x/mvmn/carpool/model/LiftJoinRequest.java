package x.mvmn.carpool.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class LiftJoinRequest {

	protected int id;
	protected LiftOffer offer;
	protected User user;
	protected boolean driverInitiated;
	protected boolean approved;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(optional = false)
	public LiftOffer getOffer() {
		return offer;
	}

	public void setOffer(LiftOffer offer) {
		this.offer = offer;
	}

	@ManyToOne(optional = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isDriverInitiated() {
		return driverInitiated;
	}

	public void setDriverInitiated(boolean driverInitiated) {
		this.driverInitiated = driverInitiated;
	}
}
