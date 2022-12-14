package ClientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ServerSide.ClientData;
import common.Message;
import common.MessageCode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client extends Thread {

	ObservableList<String> contacts = FXCollections.observableArrayList();
	Socket clientSocket;
	private ObjectOutputStream oOutputS;
	private ObjectInputStream oInputS;
	
	public Client(ObservableList<String> contacts, Socket clientSocket, ObjectInputStream oInputS, ObjectOutputStream oOutputS) {
		this.contacts = contacts;
		this.clientSocket = clientSocket;
		this.oInputS = oInputS;
		this.oOutputS = oOutputS;
	}

	public void run() {
		System.out.println("Waiting on message");
		
		try {
				while(true) {
					// process incoming messages
					Message message = (Message)oInputS.readObject();
					System.out.println(message.toString());
					processMessage(message);
						
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void processMessage(Message message) {
		if (message.getCode() == MessageCode.ADD_CONTACT) {
			contacts.add(message.getPayload());
		} else if (message.getCode() == MessageCode.REMOVE_CONTACT) {
			contacts.remove(message.getPayload());
		} else if (message.getCode() == MessageCode.GET_CONTACTS) {
			// add each contact to the contacts list
			for(int i = 0; i < Integer.parseInt(message.getPayload()); i++ ) {
				
				try {
					ClientData client;
					client = (ClientData) oInputS.readObject();
					contacts.add(client.getUserName());
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			System.out.println("Client: contacts updated");
		}
	}

}
