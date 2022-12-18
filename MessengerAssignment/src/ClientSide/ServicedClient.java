package ClientSide;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.ClientData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

// Service runs a thread by performing the work defined in Task
public class ServicedClient extends Service<String> {

	private ClientGUI clientGUI;
	ObservableList<String> contacts = FXCollections.observableArrayList();
	Socket clientSocket;
	private ClientData user;
	private Client client;
	private TextArea ta;
	
	public ServicedClient(ClientGUI gui, ObservableList<String> contacts, Socket clientSocket, ClientData user) {
		this.clientGUI = gui;
		this.contacts = contacts;
		this.clientSocket = clientSocket;

		this.user = user;
	
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
				client = new Client(clientGUI, contacts, clientSocket, user);
				System.out.println(client.toString());
				
				return "nothing";
			}
	
		};
	}
	
	public ClientData login(String username, String password) {
		System.out.println("calling login in serviced client");
		return client.login (username, password);
	}
	
	public ClientData register(ClientData tempUser) {
		System.out.println("calling register in serviced client");
		return client.register(tempUser);
	}
	
	public void send(String destination, String message) {
		System.out.println("calling send message in serviced client");
		client.send(destination, message);
	}
	
	public void updateMessages(String contactUsername, TextArea messagesTA) {
		System.out.println("calling updateMessages in serviced client");
		client.updateMessages(contactUsername, messagesTA);
	}
	
	public void getContacts() {
		client.getContacts();
	}
	
	public void startClient() {
		client.start();
	}
}
