package x.mvmn.carpool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

	protected int id;
	protected String emailAddress;
	protected String password;
	protected Boolean confirmed;
	protected String confirmationRequestId;
	protected String passwordResetRequestId;
	protected long passwordResetRequestUnixTime;

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
}
