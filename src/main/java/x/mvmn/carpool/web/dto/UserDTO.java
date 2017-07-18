package x.mvmn.carpool.web.dto;

import x.mvmn.carpool.model.User;

public class UserDTO {
	protected int id;
	protected String fullName;
	protected String emailAddress;
	protected Boolean confirmed;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	public static UserDTO toDTO(User user) {
		UserDTO dto = new UserDTO();
		dto.setConfirmed(user.getConfirmed() != null && user.getConfirmed().booleanValue());
		dto.setEmailAddress(user.getEmailAddress());
		dto.setFullName(user.getFullName());
		dto.setId(user.getId());
		return dto;
	}
}
