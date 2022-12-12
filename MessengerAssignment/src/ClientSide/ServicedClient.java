package ClientSide;

import java.net.Socket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

// Service runs a thread by performing the work defined in Task
public class ServicedClient extends Service<String> {

	ObservableList<String> contacts = FXCollections.observableArrayList();
	Socket clientSocket;
	
	public ServicedClient(ObservableList<String> contacts, Socket clientSocket) {
		this.contacts = contacts;
		this.clientSocket = clientSocket;
//		??? put setOnSucceeded here???
	}

	@Override
	protected Task<String> createTask() {
		// TODO Auto-generated method stub
		return new Task <String>()
		{

			@Override
			protected String call() throws Exception {
				System.out.println("Creating Task in ServicedClient");
				Client client = new Client(contacts, clientSocket);
				client.start();
				return null;
			}
	
		};
	}

}
