package ServerSide;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import javafx.collections.ObservableList;

public class Server extends Thread{

	private ServerSocket serverSocket;
	private ObservableList<ClientData> ipAddressList;
	private int port;
	private HashMap<ClientData, ServerThread> clientSockets = new HashMap<ClientData, ServerThread>();
	
	
	public Server (int port, ObservableList<ClientData> ipAddressList) throws IOException {
		this.ipAddressList = ipAddressList;
		this.port = port;
		serverSocket = new ServerSocket(port);
    	System.out.println("Server started on: " + port);
    	
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
				PrintWriter out = new PrintWriter(value.getClientSocket().getOutputStream (), true);
				out.println("addContact");
				out.println(client.getUserName());
				
				
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

}
