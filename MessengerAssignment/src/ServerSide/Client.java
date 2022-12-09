package ServerSide;

public class Client {

	private String firstName;
	private String lastName;
	private String ipAddress;
	
	public Client(String firstName, String lastName, String ipAddress) {
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

	@Override
	public String toString() {
		return "Client [firstName=" + firstName + ", lastName=" + lastName + ", ipAddress=" + ipAddress + "]";
	}

	
}
