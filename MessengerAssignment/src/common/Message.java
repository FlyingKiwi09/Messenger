package common;

import java.io.Serializable;

public class Message implements Serializable{

	private int opInt;
	private String fromIP;
	private String toIP;
	private String payload;
	
	public Message() {
		
	}

	public Message(int opInt, String fromIP, String toIP, String payload) {
		this.opInt = opInt;
		this.fromIP = fromIP;
		this.toIP = toIP;
		this.payload = payload;
	}

	public int getOpInt() {
		return opInt;
	}

	public void setOpInt(int opInt) {
		this.opInt = opInt;
	}

	public String getFromIP() {
		return fromIP;
	}

	public void setFromIP(String fromIP) {
		this.fromIP = fromIP;
	}

	public String getToIP() {
		return toIP;
	}

	public void setToIP(String toIP) {
		this.toIP = toIP;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	

} 
