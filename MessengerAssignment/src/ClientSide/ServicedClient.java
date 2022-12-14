package ClientSide;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

// Service runs a thread by performing the work defined in Task
public class ServicedClient extends Service<String> {

	ObservableList<String> contacts = FXCollections.observableArrayList();
	Socket clientSocket;
	private ObjectOutputStream oOutputS;
	private ObjectInputStream oInputS;
	
	public ServicedClient(ObservableList<String> contacts, Socket clientSocket, ObjectInputStream oInputS, ObjectOutputStream oOutputS) {
		this.contacts = contacts;
		this.clientSocket = clientSocket;
		this.oInputS = oInputS;
		this.oOutputS = oOutputS;
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
				Client client = new Client(contacts, clientSocket, oInputS, oOutputS);
				client.start();
				return "nothing";
			}
	
		};
	}

}
