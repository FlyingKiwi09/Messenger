package ClientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;

import ServerSide.ClientData;
import common.Message;
import common.MessageCode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

public class Client extends Thread {

	ObservableList<String> contacts = FXCollections.observableArrayList();
	Socket clientSocket;
	private ObjectOutputStream oOutputS;
	private ObjectInputStream oInputS;
	private ClientData user;
	private TextArea ta;
	
	public Client(ObservableList<String> contacts, Socket clientSocket, ObjectInputStream oInputS, ObjectOutputStream oOutputS, ClientData user, TextArea ta) {
		this.contacts = contacts;
		this.clientSocket = clientSocket;
		this.oInputS = oInputS;
		this.oOutputS = oOutputS;
		this.user = user;
		this.ta = ta;
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
		} else if (message.getCode() == MessageCode.MESSAGE) {
			ta.appendText(message.getPayload());
		}
	}

	public void send(String destination, String messageText) {
		System.out.println("calling send message in client");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Message message = new Message(MessageCode.FORWARD_MESSAGE, user.getUserName(), destination, messageText, timestamp );
		try {
			oOutputS.writeObject(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
