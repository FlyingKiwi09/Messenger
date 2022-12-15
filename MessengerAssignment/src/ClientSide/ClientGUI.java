package ClientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;

import ServerSide.ClientData;
import ServerSide.ServicedServer;
import common.Message;
import common.MessageCode;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientGUI extends Application {
	
	private String serverAddress = "localhost";
	private int port = 6677;
	private static Socket clientSocket;
	private static ObservableList<String> contacts = FXCollections.observableArrayList();
	static TextField messagesTF;
	
//	reader and writers
	private ObjectInputStream oInputS;
	private ObjectOutputStream oOutputS;
	 
	final private static Text feedback = new Text();
	
	private ClientData user;
	private ServicedClient servicedClient;
	
//	UI elements
	private Stage primaryStage;

	public ClientGUI() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		 
		this.primaryStage = primaryStage;
		
		//set up UI
		VBox root = new VBox();
		Button connectButton = new Button("Connect");		
		messagesTF = new TextField();

		root.getChildren().addAll(connectButton, messagesTF);
		
		
//		listeners for buttons
		connectButton.setOnAction(event -> {
			connectToServer();
		});

		// scene set up
		primaryStage.setScene(new Scene(root, 300, 400));
		primaryStage.sizeToScene();
		primaryStage.setTitle("Chat Box Clientside");
		primaryStage.show();
	}
	
	private void connectToServer() {
		
//		connect to the server
		System.out.println("Client says: trying to connect to server...");
		try {
			clientSocket = new Socket(serverAddress, port);
			System.out.println("connected");
			
//			create the output and input streams when connecting to the server
			OutputStream out = clientSocket.getOutputStream();
			InputStream in = clientSocket.getInputStream();
			
			System.out.println("got streams");
			
			oOutputS = new ObjectOutputStream(out);
			oOutputS.flush();
			oInputS = new ObjectInputStream(in);
			
			System.out.println("created input and output ");
			
			
//			send a message to confirm the connection
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Message connectionMessage = new Message(MessageCode.CONFIRM_CONNECTION, "annon", "server", "", timestamp);
			oOutputS.writeObject(connectionMessage);
			
//			get a response to confirm the connection
			Message reply = (Message) oInputS.readObject();
			processMessage(reply);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	
	}
	
	private void processMessage(Message message) {
		
		if (message.getCode() == MessageCode.CONFIRM_CONNECTION) {
//			show login screen
			showLoginScreen();
		}
		
	}
	
	private void getContacts() {
		try {
			System.out.println("getContacts called");
	//		write to the server
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Message getContactsMessage = new Message(MessageCode.GET_CONTACTS, "server", user.getUserName(), "", timestamp);
			oOutputS.writeObject(getContactsMessage);
			
			Message reply = (Message) oInputS.readObject();
			
			// check there are contacts being returned 
			if (reply.getCode() == MessageCode.GET_CONTACTS) {
				// add each contact to the contacts list
				for(int i = 0; i < Integer.parseInt(reply.getPayload()); i++ ) {
					ClientData client = (ClientData) oInputS.readObject();
					contacts.add(client.getUserName());
				}
			}

			System.out.println("Client: contacts updated");
				
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void showLoginScreen() {
		
		VBox rootLogin = new VBox();
		Text loginHeading = new Text("Login");
		
		HBox usernameHB = new HBox();
		Label usernameLabel = new Label("Username: ");
		TextField usernameTF = new TextField();
		
		HBox passwordHB = new HBox();
		Label passwordLabel = new Label("Password: ");
		TextField passwordTF = new TextField();
		
		Button loginButton = new Button("Login");
		
//		Text feedback = new Text();
		usernameHB.getChildren().addAll(usernameLabel, usernameTF);
		passwordHB.getChildren().addAll(passwordLabel, passwordTF);
		
		
		loginButton.setOnAction(event ->{
			System.out.println("login button pressed");
//			sent the username and password to the login method. 
//			login() returns the user if one is found in the database or null if no user is found
			user = login(usernameTF.getText(), passwordTF.getText());
			
			if (user != null) {
//				show logged in screen
				feedback.setText("Success");
				showMessagesScreen(user);
			} else {
//				display failed login message
				feedback.setText("Error: username or password are incorrect.");
			}
		});
		
		rootLogin.getChildren().addAll(loginHeading, usernameHB, passwordHB, loginButton, feedback);
		Scene loginScene = new Scene(rootLogin, 400, 400);
		this.primaryStage.setScene(loginScene);
	}
	
//	login sends the username and password to the server for verification 
//	if the credentials are verified, the server sends back a Client object with the users details.
//	the method returns this client or null if the details can't be verified
	private ClientData login(String username, String password) {
		System.out.println("login method called");
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Message loginMessage = new Message(MessageCode.LOGIN, username, "server", password, timestamp);
		try {
			oOutputS.writeObject(loginMessage);
			
//			get the reply from the server: says successful or not
			
			Message reply = (Message) oInputS.readObject();
			
			if (reply.getPayload().equals("loginSuccess")) {
				ClientData me = (ClientData) oInputS.readObject();
				return me;
			} else {
				return null;
			}
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	
	}
	
	private void showMessagesScreen(ClientData user) {
		VBox rootMessages = new VBox();
		Text welcome = new Text("Welcome " + user.getUserName());
		
//recipient 
		Label recipientLabel = new Label("Select a recipient: ");
		final ComboBox<String> recipientListCB = new ComboBox<String>(contacts);
		getContacts();
		HBox recipientBox = new HBox();
// message
		TextArea messagesTA = new TextArea();
		messagesTA.setPrefHeight(200);
		messagesTA.setPrefWidth(200);
		messagesTA.setEditable(false);

//	 new message	
		HBox newMessageHB = new HBox();
		TextField newMessageTF = new TextField();
		newMessageTF.setText(null);
		Button sendButton = new Button("Send");
		
		Button logoutButton = new Button("Logout");
		
//		put objects together
		recipientBox.getChildren().addAll(recipientLabel, recipientListCB);
		newMessageHB.getChildren().addAll(newMessageTF, sendButton);
		
		sendButton.setOnAction(event ->{
			System.out.println("Send message called");
			if (recipientListCB.getValue() != null) {
				if (newMessageTF.getText() != null) {
					servicedClient.send(recipientListCB.getValue(), newMessageTF.getText());
					newMessageTF.setText(null);
				} else {
					System.out.println("write a message");
				}
				
			} else {
				System.out.println("select a recipient to message");
			}
			
		});
		
		logoutButton.setOnAction(event ->{
			// tell the server you're logging out so that it can remove the user from the list of logged in users.
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Message logoutMessage = new Message(MessageCode.LOGOUT, user.getUserName(), "server", "", timestamp);
			try {
				oOutputS.writeObject(logoutMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			showLoginScreen();
			
		});
		
		
		
//		create serviced client so separate thread can be made with java fx
		System.out.println("Creating servicedClient");
	    servicedClient = new ServicedClient(contacts, clientSocket, oInputS, oOutputS, user, messagesTA);
		servicedClient.start();
		
		rootMessages.getChildren().addAll(welcome, recipientBox, messagesTA, newMessageHB, logoutButton);
		Scene messagesScene = new Scene(rootMessages, 400, 400);
		this.primaryStage.setScene(messagesScene);

	}

	public static void main(String[] args) {
		
		launch(args);

	}


}
