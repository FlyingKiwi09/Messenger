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
	
	Socket clientSocket;
	ObservableList<Client> ipAddressList;

	public ServerThread(Socket clientSocket, ObservableList<Client> ipAddressList) {
		this.clientSocket = clientSocket;
		this.ipAddressList = ipAddressList;
	}

	public ServerThread(Runnable target) {
		super(target);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(ThreadGroup group, Runnable target) {
		super(group, target);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(ThreadGroup group, String name) {
		super(group, name);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(Runnable target, String name) {
		super(target, name);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(ThreadGroup group, Runnable target, String name, long stackSize, boolean inheritThreadLocals) {
		super(group, target, name, stackSize, inheritThreadLocals);
		// TODO Auto-generated constructor stub
	}
	
	public void run() {
		try {
			
			
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
					String firstName = input.readLine();
					String lastName = input.readLine();
					String ipAddress = input.readLine();
					
//					create a new client object with the client details
					Client client = new Client("annon", "annon", "annon", ipAddress);
					System.out.println(client);
					ipAddressList.add(client);
				} else if (reply.equals("getContacts")) {	
					
//					send contacts
					System.out.println("Sending contacts");
					for (Client client: ipAddressList) {
						out.println(client.getFirstName());
						System.out.println("Sending " + client.getFirstName());
					}
					System.out.println("ended");
					out.println("end");
					
				}else if (reply.equals("login")) {

					String username = input.readLine();
					String password = input.readLine();
					
//					connect to the database and check the credentials
					try {
			    		String databaseUser = "root";
			        	String databasePass = "";
						Class.forName("com.mysql.cj.jdbc.Driver");
						Connection connection = null;
						String url = "jdbc:mysql://localhost/messenger";
						connection = DriverManager.getConnection(url, databaseUser, databasePass);
						
//						PreparedStatement pst = con.prepareStatement(sql)) {
//
//				            pst.setString(1, author);
//				            pst.executeUpdate();
//				            
//				            System.out.println("A new author has been inserted");
						
				        String statement = "SELECT * FROM users WHERE users.username = ? AND users.password = ?";
						PreparedStatement pst = connection.prepareStatement(statement);
						
						pst.setString(1, username);
						pst.setString(2, password);
	
						ResultSet rs =	pst.executeQuery();
						
						if (rs.next()) {
							System.out.println(rs.getString("username"));
							
							out.print("loginSuccess");
							// write object to file
							
							ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
							Client user = new Client(rs.getString("username"), rs.getString("firstname"), rs.getString("lastname"), null); 
							oos.writeObject(user);
							
						} else {
							System.out.print("Login failed");
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else {
				
					System.out.println("Something went wrong in ServerThread.run()");
				}
			
			}
			
//			clientSocket.close();
		} catch(IOException e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}
	

}
