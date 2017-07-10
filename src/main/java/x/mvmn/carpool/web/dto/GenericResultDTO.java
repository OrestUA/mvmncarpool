package x.mvmn.carpool.web.dto;

public class GenericResultDTO {
	public boolean success;
	public String message;

	public GenericResultDTO() {
	}

	public GenericResultDTO(boolean success, String message) {
		this.success = success;
		this.message = message;
	}
}
