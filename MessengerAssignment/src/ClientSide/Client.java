package ClientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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
		while(true) {
			System.out.println("Waiting on message");
			// set up an input for message
			// if a message comes through then update the UI
			try {
				BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String message = input.readLine();
				
			
			} catch (IOException e) {
				
			}
		}
	}

}