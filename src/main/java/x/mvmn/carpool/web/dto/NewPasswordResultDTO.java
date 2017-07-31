package x.mvmn.carpool.web.dto;

import java.util.List;

public class NewPasswordResultDTO {
	protected List<String> errors;
	protected boolean success;

	public NewPasswordResultDTO() {
	}

	public NewPasswordResultDTO(boolean success, List<String> errors) {
		this.errors = errors;
		this.success = success;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
