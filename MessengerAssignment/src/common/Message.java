package common;

import java.io.Serializable;
import java.sql.Timestamp;

public class Message implements Serializable{

	private MessageCode code;
	private String fromUsername;
	private String toUsername;
	private String payload;
	private Timestamp timestamp;
	
	public Message() {
		
	}

	public Message(MessageCode code, String fromUsername, String toUsername, String payload, Timestamp timestamp) {
		this.code = code;
		this.fromUsername = fromUsername;
		this.toUsername = toUsername;
		this.payload = payload;
		this.timestamp = timestamp;
	}

	public MessageCode getCode() {
		return code;
	}

	public void setCode(MessageCode code) {
		this.code = code;
	}

	public String getFromUsername() {
		return fromUsername;
	}

	public void setFromUsername(String fromUsername) {
		this.fromUsername = fromUsername;
	}

	public String getToUsername() {
		return toUsername;
	}

	public void setToUsername(String toUsername) {
		this.toUsername = toUsername;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Message [code=" + code + ", fromUsername=" + fromUsername + ", toUsername=" + toUsername + ", payload="
				+ payload + ", timestamp=" + timestamp + "]";
	}
	
	

} 
