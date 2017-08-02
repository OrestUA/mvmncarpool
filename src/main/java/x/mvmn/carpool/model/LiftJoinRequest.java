package x.mvmn.carpool.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "offer_id" }) })
public class LiftJoinRequest {

	protected int id;
	protected LiftOffer offer;
	protected User user;
	protected boolean driverInitiated;
	// null - pending, true - approved, false - rejected
	protected Boolean approved;

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

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public boolean isDriverInitiated() {
		return driverInitiated;
	}

	public void setDriverInitiated(boolean driverInitiated) {
		this.driverInitiated = driverInitiated;
	}
}
