package ServerSide;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.collections.ObservableList;

public class Server extends Thread{

	ServerSocket serverSocket;
	ObservableList<ClientData> ipAddressList;
	private int port;
	
	
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
	    		Thread.sleep(1000);
				System.out.println("Waiting on client to connect!");
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected, sending hello world...");
	//    		creates and starts a new thread from the ServerThread class
				new ServerThread(clientSocket, ipAddressList).start();
    		}
    			
    	}  catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void stopServer() throws IOException {
		serverSocket.close();
		System.out.println("Program finished! - closed from stopServer");
	}

}
