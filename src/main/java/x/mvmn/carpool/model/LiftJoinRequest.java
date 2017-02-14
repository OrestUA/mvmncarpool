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
	protected User driver;

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
	public User getDriver() {
		return driver;
	}

	public void setDriver(User driver) {
		this.driver = driver;
	}
}
