package x.mvmn.carpool.web.dto;

import x.mvmn.carpool.model.LiftJoinRequest;

public class LiftJoinRequestDTO {

	protected int id;
	protected int liftOfferId;
	protected Integer liftRequestId;
	protected int userId;
	protected boolean driverInitiated;
	protected Boolean approved;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLiftOfferId() {
		return liftOfferId;
	}

	public void setLiftOfferId(int liftOfferId) {
		this.liftOfferId = liftOfferId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public Integer getLiftRequestId() {
		return liftRequestId;
	}

	public void setLiftRequestId(Integer liftRequestId) {
		this.liftRequestId = liftRequestId;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public static LiftJoinRequestDTO fromLiftJoinRequest(LiftJoinRequest request) {
		LiftJoinRequestDTO result = new LiftJoinRequestDTO();
		result.setId(request.getId());
		result.setLiftOfferId(request.getOffer().getId());
		result.setUserId(request.getUser().getId());
		result.setDriverInitiated(request.isDriverInitiated());
		result.setApproved(request.getApproved());
		result.setLiftRequestId(request.getLiftRequest() != null ? request.getLiftRequest().getId() : null);

		return result;
	}
}
