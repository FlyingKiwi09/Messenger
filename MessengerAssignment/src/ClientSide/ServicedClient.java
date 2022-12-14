package ClientSide;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ServerSide.ClientData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

// Service runs a thread by performing the work defined in Task
public class ServicedClient extends Service<String> {

	ObservableList<String> contacts = FXCollections.observableArrayList();
	Socket clientSocket;
	private ObjectOutputStream oOutputS;
	private ObjectInputStream oInputS;
	private ClientData user;
	private Client client;
	private TextArea ta;
	
	public ServicedClient(ObservableList<String> contacts, Socket clientSocket, ObjectInputStream oInputS, ObjectOutputStream oOutputS, ClientData user, TextArea ta) {
		this.contacts = contacts;
		this.clientSocket = clientSocket;
		this.oInputS = oInputS;
		this.oOutputS = oOutputS;
		this.user = user;
		this.ta = ta;
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
				client = new Client(contacts, clientSocket, oInputS, oOutputS, user, ta);
				client.start();
				return "nothing";
			}
	
		};
	}
	
	public void send(String destination, String message) {
		System.out.println("calling send message in serviced client");
		client.send(destination, message);
	}
}
