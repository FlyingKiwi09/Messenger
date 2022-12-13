package ClientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;

import ServerSide.ClientData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client extends Thread {

	ObservableList<String> contacts = FXCollections.observableArrayList();
	Socket clientSocket;
	
	public Client(ObservableList<String> contacts, Socket clientSocket) {
		this.contacts = contacts;
		this.clientSocket = clientSocket;
	}

	public void run() {
		System.out.println("Waiting on message");
		
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	
				while(true) {
					// set up an input for message
					// if a message comes through then update the UI
						String message = input.readLine();
						if (message != null) {
							System.out.println("Message Recieved:" + message);
							if (message.equals("addContact")) {
								String contactUsername = input.readLine();
								if (!(contacts.contains(contactUsername))) {
									contacts.add(contactUsername);
								}

							} else if (message.equals("removeContact")) {
								String contactUsername = input.readLine();
								contacts.remove(contactUsername);
							}
						}
					
					}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
	}

}
