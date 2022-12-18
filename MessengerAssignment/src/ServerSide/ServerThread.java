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

import common.ClientData;
import common.Message;
import common.MessageCode;
import javafx.collections.ObservableList;

public class ServerThread extends Thread {
	
	private Socket clientSocket;
	private ObservableList<ClientData> ipAddressList;
	private ClientData client;
	private Server server;
	
	//keys
	private int myPrivateKey; 
	private double sharedKey;
	int G = 3;
	int P = 17;
	
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
		}else if (message.getCode() == MessageCode.MESSAGE_RECEIVED) {
			forwardMessageReceived(message);
		}else if (message.getCode() == MessageCode.GET_MESSAGES) {
			sendOldMessages(message);
		}else if (message.getCode() == MessageCode.KEYS) {
			generateKey(message);
		}else if (message.getCode() == MessageCode.REGISTER) {
			attemptRegistration(message);
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
	
	
	//https://www.javatpoint.com/diffie-hellman-algorithm-in-java
	//https://www.geeksforgeeks.org/java-implementation-of-diffie-hellman-algorithm-between-client-and-server/
	private void generateKey(Message message) {
		
		// create my private key
		myPrivateKey = getRandomKeyInt();
		System.out.println("server public key" + myPrivateKey);
		
		// get A from the client
		double clientA = Double.parseDouble(message.getPayload());
		System.out.println(clientA);
		
		// sent B back to the client
		double B = ((Math.pow(G, myPrivateKey)) % P);
		System.out.println("B" + B);

		message.setPayload("" + B);
		
		try {
			oOutputS.writeObject(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// calculate the shared key
		sharedKey = ((Math.pow(clientA, myPrivateKey)) % P); 
		System.out.println("shared key" + sharedKey);
	}
	
	// create calculatePower() method to find the value of x ^ y mod P  
    private static long calculatePower(long x, long y, long P)  
    {  
        long result = 0;          
        if (y == 1){  
            return x;  
        }  
        else{  
            result = ((long)Math.pow(x, y)) % P;  
            return result;  
        }  
    } 
	
	private void validateLogin(Message message) {
		System.out.println("logging in...");
		String username = message.getFromUsername();
		String password = message.getPayload();
		
		
		
		System.out.println("Calling validateLogin on the databasehandler\nusername: " + username);
		System.out.println("password: " + password);
		
//		databasehandler queries the database and returns a client, returns null if a match isn't found
		ClientData returnedClient = server.getDatabaseHandler().validateLogin(username, password);
			
		// prepare a reply to the client
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Message reply = new Message(MessageCode.LOGIN, "Server", "", "", timestamp);
		
		if (client != null) {
			// reply that the login is successful 
			reply.setPayload("loginSuccess");
			
			try {
				oOutputS.writeObject(reply);

				// send the ClientData as an object
				client.setUserName(returnedClient.getUserName());
				client.setFirstName(returnedClient.getFirstName());
				client.setLastName(returnedClient.getLastName());
				oOutputS.writeObject(client);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// tell the server to add this client to it's hash map
			// the server also tells other clients that this user is online at this point
			this.server.addClientSocket(this, client);
		}
	}
	
	private void attemptRegistration(Message message) {
		System.out.println("Attempting registration");

		try {
			// get the client to register
			ClientData tempUser = (ClientData) oInputS.readObject();
			System.out.println("Received client " + tempUser.toString());
			
			// attempt to register the client with the databaseHandler
			ClientData returnedClient = server.getDatabaseHandler().attemptRegistration(tempUser);
			
			if (returnedClient !=null) {
				System.out.println("Returned client " + returnedClient.toString());	
				client.setUserName(returnedClient.getUserName());
				client.setFirstName(returnedClient.getFirstName());
				client.setLastName(returnedClient.getLastName());
				oOutputS.writeObject(client);
				
			} else {
				oOutputS.writeObject(returnedClient);
			}
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// tell the server to add this client to it's hash map
					// the server also tells other clients that this user is online at this point
		this.server.addClientSocket(this, client);
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
		
		System.out.println("recieved message for forwarding: " + message.toString());
		
		// get the client that the message is for
		String destination = message.getToUsername();
		ClientData destinationClient = null;
		
		Set<ClientData> clients = server.getClientSockets().keySet();
		
		for (ClientData client : clients) {
			if (client.getUserName().equals(destination)) {
				destinationClient = client;
				System.out.println("Destination: " + destinationClient);
				break;
			}
		}
		
		// if that client can be found forward the message onto them
		if (destinationClient != null) {
			try {
				message.setCode(MessageCode.MESSAGE);
				server.getClientSockets().get(destinationClient).getoOutputS().writeObject(message);
				System.out.println("Sending: " + message.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void forwardMessageReceived(Message message) {
		// get the client that the message is for
		String destination = message.getToUsername();
		ClientData destinationClient = null;
		
		Set<ClientData> clients = server.getClientSockets().keySet();
		
		for (ClientData client : clients) {
			if (client.getUserName().equals(destination)) {
				destinationClient = client;
				System.out.println("Destination: " + destinationClient);
				break;
			}
		}
		
		// send the confirmation message to the sender to let them know that their message was sent
		if (destinationClient != null) {
			try {
				server.getClientSockets().get(destinationClient).getoOutputS().writeObject(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// save the message to the database
		server.getDatabaseHandler().saveMessage(message);
		
	}
	
	private void sendOldMessages(Message message) {
		// get old messages from the database where the user and recipient are the sender&reciever
		// note: the client sends their name as the fromUsername and the contact name as the payload
		System.out.println("requesting messages from databasehandler");
		ArrayList<Message> messages = server.getDatabaseHandler().getMessages(message.getFromUsername(), message.getPayload());
		
		System.out.println("recieved " + messages.size() + "from the databasehandler");
		
		try {
			// tell the client how many messages to expect
			message.setPayload("" + messages.size());
			oOutputS.writeObject(message);
			
			// send the messages
			for (Message oldMessage : messages) {
				oOutputS.writeObject(oldMessage);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// send the messages
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
	
	private int getRandomKeyInt() {
		return (int) Math.floor(Math.random()*100 +1);
	}

}
