package common;

import java.io.Serializable;

public class ClientData implements Serializable {

	private String userName;
	private String firstName;
	private String lastName;
	private String ipAddress;
	
	public ClientData(String userName, String firstName, String lastName, String ipAddress) {
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.ipAddress = ipAddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "Client [firstName=" + firstName + ", lastName=" + lastName + ", ipAddress=" + ipAddress + "]";
	}

	
}
