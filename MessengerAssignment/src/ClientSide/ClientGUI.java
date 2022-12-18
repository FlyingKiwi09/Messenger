package ClientSide;


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import common.ClientData;
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
	

	private static Socket clientSocket;
	private static ObservableList<String> contacts = FXCollections.observableArrayList();
	static TextField messagesTF;
	final private static Text feedback = new Text();
	boolean loginMode = true;
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
		VBox loginRoot = new VBox();
		
		HBox portNumberHB = new HBox();
		Label portNumberLabel = new Label("Port Number: ");
		TextField portNumberTF = new TextField("6677");
		
		HBox ipAddressHB = new HBox();
		Label ipAddressLable = new Label("Server Address: ");
		TextField ipAddressTF = new TextField("localhost");
		
		Button connectButton = new Button("Connect");		
		
		HBox buttonHB = new HBox();
		
		Button loginButton = new Button("login");
		loginButton.setDisable(true);
		
		Button registerButton = new Button("registrer");
		registerButton.setDisable(true);
		HBox usernameHB = new HBox();
		Label usernameLabel = new Label("Username: ");
		TextField usernameTF = new TextField();
		
		HBox passwordHB = new HBox();
		Label passwordLabel = new Label("Password: ");
		TextField passwordTF = new TextField();
		
		HBox firstNameHB = new HBox();
		Label firstNameLabel = new Label("First Name: ");
		TextField firstNameTF = new TextField();
		firstNameHB.setVisible(false);
		
		HBox lastNameHB = new HBox();
		Label lastNameLabel = new Label("Last Name: ");
		TextField lastNameTF = new TextField();
		lastNameHB.setVisible(false);
		
		Button loginSubmitButton = new Button("Login");
		loginSubmitButton.setDisable(true);
		
		
//		combine inputs and labels into HBoxes
		buttonHB.getChildren().addAll(loginButton, registerButton);
		portNumberHB.getChildren().addAll(portNumberLabel, portNumberTF);
		ipAddressHB.getChildren().addAll(ipAddressLable, ipAddressTF);
		usernameHB.getChildren().addAll(usernameLabel, usernameTF);
		passwordHB.getChildren().addAll(passwordLabel, passwordTF);
		firstNameHB.getChildren().addAll(firstNameLabel, firstNameTF);
		lastNameHB.getChildren().addAll(lastNameLabel, lastNameTF);
		
//		listeners for buttons
		connectButton.setOnAction(event -> {
				// connecting to server
				try {
					connectToServer(ipAddressTF.getText(), Integer.parseInt(portNumberTF.getText()));
//					create serviced client so separate thread can be made with java fx
//					this thread holds the connection with the server
					System.out.println("Creating servicedClient");
				    servicedClient = new ServicedClient(this, contacts, clientSocket,user);
				    servicedClient.start();
					registerButton.setDisable(false);
					loginSubmitButton.setDisable(false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		});
		
	  
		
		loginButton.setOnAction(event -> {
			// switch from register mode to login mode
			loginButton.setDisable(true);
			registerButton.setDisable(false);
			firstNameHB.setVisible(false);
			lastNameHB.setVisible(false);
			loginMode = true;
			loginSubmitButton.setText("Login");
		});
		
		registerButton.setOnAction(event -> {
			// switch from login mode to register mode
			loginButton.setDisable(false);
			registerButton.setDisable(true);
			firstNameHB.setVisible(true);
			lastNameHB.setVisible(true);
			loginMode = false;
			loginSubmitButton.setText("Register");
		});
		
		loginSubmitButton.setOnAction(event ->{
			
			if (loginMode == true) {
				System.out.println("login button pressed");

//				logging in
				user = servicedClient.login(usernameTF.getText(), passwordTF.getText());
				
				
				
				if (user != null) {
//					show logged in screen
					feedback.setText("Success");
					showMessagesScreen(user);
					servicedClient.startClient();
				} else {
//					display failed login message
					feedback.setText("Error: username or password are incorrect.");
				}
			} else {
				System.out.println("Register Button Pressed");
				
				if (usernameTF.getText() != "" &&  firstNameTF.getText() != "" 
						&& lastNameTF.getText() != "" && passwordTF.getText() != "") {
					ClientData tempUser = new ClientData(usernameTF.getText(), firstNameTF.getText(), lastNameTF.getText(), passwordTF.getText());
					System.out.println("Temp user to send is " + tempUser.toString());
					
					user = servicedClient.register(tempUser);
					
					if (user != null) {
						showMessagesScreen(user);
						servicedClient.startClient();
						
					} else {
						feedback.setText("Error: could not create user" + usernameTF.getText() + ".\nPlease try again.");
					}
				} else {
					feedback.setText("Ensure all fields are filled in and try again.");
				}
			}
		});
		
		
		
		loginRoot.getChildren().addAll(portNumberHB, ipAddressHB, connectButton, buttonHB, usernameHB,  passwordHB, firstNameHB, lastNameHB, loginSubmitButton, feedback);

		// scene set up
		primaryStage.setScene(new Scene(loginRoot, 300, 400));
		primaryStage.sizeToScene();
		primaryStage.setTitle("Chat Box Clientside");
		primaryStage.show();
	}
	
	private void connectToServer(String address, int portNumber) throws UnknownHostException, IOException {
		
//		connect to the server
		System.out.println("Client says: trying to connect to server...");
		
		clientSocket = new Socket(address, portNumber);
		System.out.println("connected");
		
//		
	}
	
	
	public void showMessagesScreen(ClientData user) {
		VBox rootMessages = new VBox();
		Text welcome = new Text("Welcome " + user.getUserName());
		
//recipient 
		Label recipientLabel = new Label("Select a recipient: ");
		final ComboBox<String> recipientListCB = new ComboBox<String>(contacts);
//		servicedClient.getContacts();
		
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
		
//		on actions for combo box and buttons
		recipientListCB.setOnAction(event -> {
			
			// call through to the client thread to get and updated list of messages based on the contact selection
			servicedClient.updateMessages(recipientListCB.getValue(), messagesTA);
			
		});
		
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
			
			servicedClient.logout();
			try {
				start(primaryStage);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		});

		rootMessages.getChildren().addAll(welcome, recipientBox, messagesTA, newMessageHB, logoutButton);
		Scene messagesScene = new Scene(rootMessages, 400, 400);
		this.primaryStage.setScene(messagesScene);
		

		servicedClient.getContacts();

	}

	public static void main(String[] args) {
		
		launch(args);

	}


}
