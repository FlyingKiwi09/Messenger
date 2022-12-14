package ServerSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Set;

import common.Message;
import common.MessageCode;
import javafx.collections.ObservableList;

public class ServerThread extends Thread {
	
	private Socket clientSocket;
	private ObservableList<ClientData> ipAddressList;
	private ClientData client;
	private Server server;
	
//	readers and writers
	private ObjectInputStream oInputS;
	private ObjectOutputStream oOutputS;

	public ServerThread(Socket clientSocket, ObservableList<ClientData> ipAddressList, Server server) {
		this.clientSocket = clientSocket;
		this.ipAddressList = ipAddressList;
		this.server = server;
		this.client = new ClientData("annon", "annon", "annon", clientSocket.getInetAddress().getAddress().toString() );
		ipAddressList.add(client);
	}

	
	public void run() {
		try {
			
//	string for getting messages from the client		
			Message reply;	
			
//			create the output and input streams when connecting to the client
			InputStream in = clientSocket.getInputStream();
			OutputStream out = clientSocket.getOutputStream();
			
			oInputS = new ObjectInputStream(in);
			oOutputS = new ObjectOutputStream(out);
			
			while(true) {
				reply = (Message) oInputS.readObject();
				processMessage(reply);
			}
			
		} catch(IOException e) {
			System.out.println("Error: ");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processMessage(Message message) {
		if (message.getCode() == MessageCode.CONFIRM_CONNECTION) {
			confirmConnection(message);
		} else if (message.getCode() == MessageCode.LOGIN) {
			validateLogin(message);
		} else if (message.getCode() == MessageCode.GET_CONTACTS) {
			getContacts(message);
		} else if (message.getCode() == MessageCode.LOGOUT) {
			logout(message);
		}else if (message.getCode() == MessageCode.FORWARD_MESSAGE) {
			forwardMessage(message);
		}else {
			System.out.println("Did not recogise code: " + message.getCode());
		}
	}
	
	private void confirmConnection(Message message) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Message connectionConfirmation = new Message(MessageCode.CONFIRM_CONNECTION, "server", message.getFromUsername(), "", timestamp);
		try {
			oOutputS.writeObject(connectionConfirmation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void validateLogin(Message message) {
		System.out.println("logging in...");
		String username = message.getFromUsername();
		String password = message.getPayload();
		
		System.out.println("username: " + username);
		System.out.println("password: " + password);
		
//		connect to the database and check the credentials
		try {
    		String databaseUser = "root";
        	String databasePass = "";
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = null;
			String url = "jdbc:mysql://localhost/messenger";
			connection = DriverManager.getConnection(url, databaseUser, databasePass);
							
	        String statement = "SELECT * FROM users WHERE users.username = ? AND users.password = ?";
			PreparedStatement pst = connection.prepareStatement(statement);
			
			pst.setString(1, username);
			pst.setString(2, password);

			ResultSet rs =	pst.executeQuery();
			
			// prepare a reply to the client
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Message reply = new Message(MessageCode.LOGIN, "Server", "", "", timestamp);
			
			if (rs.next()) {
				// reply that the login is successful 
				reply.setPayload("loginSuccess");
				oOutputS.writeObject(reply);
				
				// send the ClientData as an object
				client.setUserName(rs.getString("username"));
				client.setFirstName(rs.getString("firstname"));
				client.setLastName(rs.getString("lastname"));
				oOutputS.writeObject(client);
				

				// tell the server to add this client to it's hash map
				// the server also tells other clients that this user is online at this point
				this.server.addClientSocket(this, client);
				
			} else {
				// reply that the login has failed
				reply.setPayload("loginFailed");
				oOutputS.writeObject(reply);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getContacts(Message message) {
//		send contacts
		System.out.println("Sending contacts");
		
		ArrayList<ClientData> contacts = new ArrayList<>();
		
		// get all the clients from the server hashmap of connected clients
		for (ClientData client: server.getClientSockets().keySet()) {
			System.out.println(client.toString());
			contacts.add(client);
		}
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		if (contacts != null) {
			// tell the client how many contacts to expect
			Message sendingContacts =  new Message(MessageCode.GET_CONTACTS, "server", client.getUserName(), ("" + contacts.size()), timestamp);
			
			try {
				System.out.println("Sending contacts");
				oOutputS.writeObject(sendingContacts);
				
				// send the contacts
				for (ClientData client : contacts) {
					oOutputS.writeObject(client);
				}
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			// send a message to say that all the contacts have been sent
			Message noMoreContacts =  new Message(MessageCode.OTHER, "server", client.getUserName(), "lastUserSent", timestamp);
		}

		
	}
	
	private void forwardMessage(Message message) {
		
		System.out.println("recieved message for forwarding");
		String destination = message.getPayload();
		
		ClientData destinationClient = null;
		
		Set<ClientData> clients = server.getClientSockets().keySet();
		
		for (ClientData client : clients) {
			if (client.getUserName().equals(destination)) {
				destinationClient = client;
				break;
			}
		}
		
		if (destinationClient != null) {
			try {
				message.setCode(MessageCode.MESSAGE);
				server.getClientSockets().get(destinationClient).getoOutputS().writeObject(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void logout(Message message) {
		server.logoutClient(client);
	}

	public Socket getClientSocket() {
		return clientSocket;
	}


	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}


	public ObjectInputStream getoInputS() {
		return oInputS;
	}


	public void setoInputS(ObjectInputStream oInputS) {
		this.oInputS = oInputS;
	}


	public ObjectOutputStream getoOutputS() {
		return oOutputS;
	}


	public void setoOutputS(ObjectOutputStream oOutputS) {
		this.oOutputS = oOutputS;
	}


	public ObservableList<ClientData> getIpAddressList() {
		return ipAddressList;
	}


	public ClientData getClient() {
		return client;
	}


	public Server getServer() {
		return server;
	}
	
	

}
