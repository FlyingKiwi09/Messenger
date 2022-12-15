package ServerSide;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import common.ClientData;
import common.Message;
import common.MessageCode;
import javafx.collections.ObservableList;

public class Server extends Thread{

	private ServerSocket serverSocket;
	private ObservableList<ClientData> ipAddressList;
	private int port;
	private HashMap<ClientData, ServerThread> clientSockets = new HashMap<ClientData, ServerThread>();
	private DatabaseHandler databaseHandler;
	
	public Server (int port, ObservableList<ClientData> ipAddressList) throws IOException {
		this.ipAddressList = ipAddressList;
		this.port = port;
		serverSocket = new ServerSocket(port);
    	System.out.println("Server started on: " + port);
    	
//    	create a new databaseHandler. 
//    	The constructor creates the connection and throws errors if a connection can't be established.
    	try {
			this.databaseHandler = new DatabaseHandler();
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Error connecting to the database, some functions may not be running");
			e.printStackTrace();
		}
    	
	}
	
	public void run() {
		System.out.println("In Server run");
		// accept connections from clients
    	try {
    		while(true) {
	    
				System.out.println("Waiting on client to connect!");
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected");
	//    		creates and starts a new thread from the ServerThread class
				ServerThread newThread = new ServerThread(clientSocket, ipAddressList, this);
				newThread.start();  
    		}
    			
    	}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
//			try {
////				serverSocket.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
	}
	
	public void addClientSocket(ServerThread clientThread, ClientData client) {
		clientSockets.put(client, clientThread);

//		let all the other clients know that a new client has logged in
		clientSockets.forEach((key, value) -> {
			try {
				//create an add_contact message where the new username is the payload
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				Message addContact = new Message(MessageCode.ADD_CONTACT, "server", key.getUserName(), client.getUserName(), timestamp);
				
				// sent the message to the each user using their object output stream
				value.getoOutputS().writeObject(addContact);
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			
		});
	}
	
	public void logoutClient(ClientData user) {
		// remove client from the hashmap
		clientSockets.remove(user);
		
//		let all the other clients know that a client has left
		clientSockets.forEach((key, value) -> {
			try {
				//create a remove_contact message where the contact to remove is the payload
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				Message addContact = new Message(MessageCode.REMOVE_CONTACT, "server", key.getUserName(), user.getUserName(), timestamp);
				
				// sent the message to the each user using their object output stream
				value.getoOutputS().writeObject(addContact);
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		});
	}
	
	public void stopServer() throws IOException {
		serverSocket.close();
		System.out.println("Program finished! - closed from stopServer");
	}

	public HashMap<ClientData, ServerThread> getClientSockets() {
		return clientSockets;
	}

	public DatabaseHandler getDatabaseHandler() {
		return databaseHandler;
	}

	public void setDatabaseHandler(DatabaseHandler databaseHandler) {
		this.databaseHandler = databaseHandler;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public ObservableList<ClientData> getIpAddressList() {
		return ipAddressList;
	}

	public int getPort() {
		return port;
	}


	

}
