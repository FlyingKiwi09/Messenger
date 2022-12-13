package ServerSide;

import java.io.Serializable;

public class MessageData implements Serializable {

	private String sender_username;
	private String receiver_username;
	private String message;
	private String timestamp;
	
	public MessageData(String sender_username, String receiver_username, String message, String timestamp) {
		this.sender_username = sender_username;
		this.receiver_username = receiver_username;
		this.message = message;
		this.timestamp = timestamp;
	}

	public String getSender_username() {
		return sender_username;
	}

	public void setSender_username(String sender_username) {
		this.sender_username = sender_username;
	}

	public String getReceiver_username() {
		return receiver_username;
	}

	public void setReceiver_username(String receiver_username) {
		this.receiver_username = receiver_username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "MessageData [sender_username=" + sender_username + ", receiver_username=" + receiver_username
				+ ", message=" + message + ", timestamp=" + timestamp + "]";
	}
	
	

}
