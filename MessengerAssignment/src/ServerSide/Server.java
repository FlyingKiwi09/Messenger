package ServerSide;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import ClientSide.Client;
import javafx.collections.ObservableList;

public class Server {

	ServerSocket serverSocket;
	ObservableList<Client> ipAddressList;
	
	
	public Server (int port, ObservableList<Client> ipAddressList) throws IOException {
		this.ipAddressList = ipAddressList;
		setUpServer(port);
	}
	
	private void setUpServer(int port) throws IOException {
//	 	create a serverSocket to listen to the specified port
    	serverSocket = new ServerSocket(port);
    	System.out.println("Server started on: " + port);
	}
	
	public void startServer() throws IOException, InterruptedException {
		// accept connections from clients
    	try {
    		Thread.sleep(1000);
			System.out.println("Waiting on client to connect!");
			Socket clientSocket = serverSocket.accept();
			System.out.println("Client connected, sending hello world...");
//    		creates and starts a new thread from the ServerThread class
			new ServerThread(clientSocket, ipAddressList).start();
    			
    	} finally {
    		serverSocket.close();
    		serverSocket = null;
    		System.out.println("Program finished! - closed from startServer");
    	}
		
	}
	
	public void stopServer() throws IOException {
		serverSocket.close();
		System.out.println("Program finished! - closed from stopServer");
	}

}
