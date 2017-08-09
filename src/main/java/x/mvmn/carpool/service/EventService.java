package x.mvmn.carpool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import x.mvmn.carpool.model.LiftJoinRequest;
import x.mvmn.carpool.model.LiftOffer;
import x.mvmn.carpool.model.LiftRequest;
import x.mvmn.carpool.web.dto.LiftJoinRequestDTO;
import x.mvmn.carpool.web.dto.LiftOfferDTO;
import x.mvmn.carpool.web.dto.LiftRequestDTO;

@Service
public class EventService {

	@Autowired
	public SimpMessagingTemplate msgTemplate;

	public static enum StateChangeType {
		CREATED, ACCEPTED, REJECTED, DELETED, UPDATED
	};

	public static class WSMessage {
		protected String topic;
		protected Object data;

		public WSMessage() {
		}

		public WSMessage(String topic, Object data) {
			super();
			this.topic = topic;
			this.data = data;
		}

		public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}
	}

	public static class StateChangeNotificationDTO {
		protected String itemType;
		protected int itemId;
		protected StateChangeType changeType;
		protected Object itemDTO;

		public StateChangeNotificationDTO() {
		}

		public StateChangeNotificationDTO(String itemType, int itemId, StateChangeType changeType) {
			this.itemType = itemType;
			this.itemId = itemId;
			this.changeType = changeType;
		}

		public StateChangeNotificationDTO(String itemType, int itemId, StateChangeType changeType, Object itemDTO) {
			this(itemType, itemId, changeType);
			this.itemDTO = itemDTO;
		}

		public int getItemId() {
			return itemId;
		}

		public void setItemId(int itemId) {
			this.itemId = itemId;
		}

		public StateChangeType getChangeType() {
			return changeType;
		}

		public void setChangeType(StateChangeType changeType) {
			this.changeType = changeType;
		}

		public String getItemType() {
			return itemType;
		}

		public void setItemType(String itemType) {
			this.itemType = itemType;
		}

		public Object getItemDTO() {
			return itemDTO;
		}

		public void setItemDTO(Object itemDTO) {
			this.itemDTO = itemDTO;
		}
	}

	public void notifyLiftJoinRequestStateChange(StateChangeType changeType, boolean passengerAction, LiftJoinRequest... ljrs) {
		for (LiftJoinRequest ljr : ljrs) {
			String userToNotify;

			if (ljr.isDriverInitiated()) {
				// Lift join request was created by driver for passenger
				if (changeType.equals(StateChangeType.CREATED) || changeType.equals(StateChangeType.DELETED)) {
					// Notify passenger we've created join offer for him, or deleted join offer we've previously created for him
					userToNotify = ljr.getUser().getEmailAddress();
				} else {
					// if not created/deleted - then accepted/rejected by passenger. Notify driver
					userToNotify = ljr.getOffer().getUser().getEmailAddress();
				}
			} else {
				// Lift join request was created by passenger for driver
				if (changeType.equals(StateChangeType.CREATED) || changeType.equals(StateChangeType.DELETED)) {
					if (changeType.equals(StateChangeType.DELETED) && !passengerAction) {
						// If driver deleted lift offer and thus lift join offers got deleted too - notify passengers their offers were effectively rejected by
						// deletion
						userToNotify = ljr.getUser().getEmailAddress();
					} else {
						// Notify driver we've asked to join, or no longer asking
						userToNotify = ljr.getOffer().getUser().getEmailAddress();
					}
				} else {
					// if not created/deleted - then accepted/rejected by driver. Notify passenger
					userToNotify = ljr.getUser().getEmailAddress();
				}
			}

			sendToUser(userToNotify, "statechange.liftjoinrequest", new StateChangeNotificationDTO("liftJoinRequest", ljr.getId(), changeType,
					createdOrUpdated(changeType) ? LiftJoinRequestDTO.fromLiftJoinRequest(ljr) : null));
		}
	}

	public void notifyLiftOfferStatusChange(LiftOffer liftOffer, StateChangeType state) {
		sentToAll(new WSMessage("statechange.liftoffer",
				new StateChangeNotificationDTO("liftOffer", liftOffer.getId(), state, createdOrUpdated(state) ? LiftOfferDTO.fromLiftOffer(liftOffer) : null)));
	}

	public void notifyLiftRequestStatusChange(LiftRequest liftRequest, StateChangeType state) {
		sentToAll(new WSMessage("statechange.liftrequest", new StateChangeNotificationDTO("liftOffer", liftRequest.getId(), state,
				createdOrUpdated(state) ? LiftRequestDTO.fromLiftRequest(liftRequest) : null)));
	}

	protected static boolean createdOrUpdated(StateChangeType state) {
		return state != null && (state.equals(StateChangeType.CREATED) || state.equals(StateChangeType.UPDATED));
	}

	protected void sendToUser(String username, String topic, Object data) {
		sendToUser(username, new WSMessage(topic, data));
	}

	protected void sendToUser(String username, WSMessage message) {
		msgTemplate.convertAndSendToUser(username, "/server", message);
	}

	protected void sentToAll(WSMessage message) {
		msgTemplate.convertAndSend("/server", message);
	}
}
