package ServerSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
			
//			get input from client
			BufferedReader input = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
			String reply = input.readLine();
			
			System.out.println("Server says: " + reply);
			
//			if the connection is good (i.e. the client has replied with "connect"), set up a printWriter to print to the client, this lets them know that the connection is successful too.	
			if (reply.equals("ClientConnected")) {
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				System.out.println("A client request received at " + clientSocket);
				out.println("connectionSuccess");
				
//				read the client details sent from the client
				String firstName = input.readLine();
				String lastName = input.readLine();
				String ipAddress = input.readLine();
				
//				create a new client object with the client details
				Client client = new Client(firstName, lastName, ipAddress);
				System.out.println(client);
				ipAddressList.add(client);
			} else {
				System.out.println("Something went wrong in ServerThread.run()");
			}
			
			clientSocket.close();
		} catch(IOException e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}
	

}
