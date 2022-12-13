package ServerSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.collections.ObservableList;

public class ServerThread extends Thread {
	
	private Socket clientSocket;
	private ObservableList<ClientData> ipAddressList;
	private ClientData client;
	private Server server;

	public ServerThread(Socket clientSocket, ObservableList<ClientData> ipAddressList, Server server) {
		this.clientSocket = clientSocket;
		this.ipAddressList = ipAddressList;
		this.server = server;
	}

	
	public void run() {
		try {
			
//	string for getting messages from the client		
			String reply;	
			
//			readers and writers
			BufferedReader input = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		
			
			while(true) {
				reply = input.readLine();
				
//				if the connection is good (i.e. the client has replied with "connect"), set up a printWriter to print to the client, this lets them know that the connection is successful too.	
				if (reply.equals("ClientConnected")) {
					System.out.println("A client request received at " + clientSocket);
					
//					tell the client that the connection has been successful
					out.println("connectionSuccess");
					
//					read the client details sent from the client
					String ipAddress = input.readLine();
					
//					create a new client object with the client details
					client = new ClientData("annon", "annon", "annon", ipAddress);
					System.out.println(client);
					ipAddressList.add(client);
				} else if (reply.equals("getContacts")) {	
					
//					send contacts
					System.out.println("Sending contacts");
					for (ClientData client: ipAddressList) {
						out.println(client.getFirstName());
						System.out.println("Sending " + client.getFirstName());
					}
					System.out.println("ended");
					out.println("end");
					
				}else if (reply.equals("login")) {

					System.out.println("logging in...");
					String username = input.readLine();
					String password = input.readLine();
					
					System.out.println("username: " + username);
					System.out.println("password: " + password);
					
//					connect to the database and check the credentials
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
						
						if (rs.next()) {
							out.println("loginSuccess");
							// write object to file
							
							
							client.setUserName(rs.getString("username"));
							client.setFirstName(rs.getString("firstname"));
							client.setLastName(rs.getString("lastname"));
							System.out.println("Sending: " + client.toString());
							out.println(client.getUserName());
							
							this.server.addClientSocket(this, client);
							
						} else {
							System.out.print("Login failed");
							out.println("loginFailed");
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else {
					System.out.println("reply:" + reply);
					System.out.println("Something went wrong in ServerThread.run()");
					
				}
			
			}
			
//			clientSocket.close();
		} catch(IOException e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}


	public Socket getClientSocket() {
		return clientSocket;
	}


	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	

}
