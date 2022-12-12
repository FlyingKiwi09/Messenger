package ClientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import ServerSide.Client;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientGUI extends Application {
	
	private String serverAddress = "localhost";
	private int port = 6677;
	private String firstName = "Bob";
	private String lastName = "Hook";
	private String myipAddress = "ipAddress";
	private static Socket clientSocket;
	final private static ObservableList<String> contacts = FXCollections.observableArrayList();
	static TextField messagesTF;
	
//	reader and writers
	private PrintWriter out;
	private BufferedReader input;
	private ObjectInputStream ois;
	 
	
	
	
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
		
		
		//recipient 
		Label recipientLabel = new Label("Select a recipient: ");
		ComboBox<String> recipientListCB = new ComboBox<String>(contacts);	
		HBox recipientBox = new HBox();
		

//	 messages	
		HBox newMessageHB = new HBox();
		TextField newMessageTF = new TextField();
		Button sendButton = new Button("Send");

//		put objects together
		recipientBox.getChildren().addAll(recipientLabel, recipientListCB);
		newMessageHB.getChildren().addAll(newMessageTF, sendButton);
		root.getChildren().addAll(connectButton, messagesTF, recipientBox, newMessageHB);
		
		
//		listeners for buttons
		connectButton.setOnAction(event -> {
			try {
				connectToServer(messagesTF);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		sendButton.setOnAction(event ->{
			if (clientSocket != null) {
//				sent the message
			}
		});
		
		// scene set up
		primaryStage.setScene(new Scene(root, 300, 400));
		primaryStage.sizeToScene();
		primaryStage.setTitle("Chat Box Clientside");
		primaryStage.show();
	}
	
	public String connectToServer(TextField messagesTF) throws UnknownHostException, IOException {
		
		System.out.println("Client says: trying to connect to server...");
		
//		connect to the server
		clientSocket = new Socket(serverAddress, port);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	
		
//		write to the server
		out.println("ClientConnected");
		
//		read ouput from server
		BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String reply = input.readLine();
		System.out.println("Client gets reply from server: " + reply);
		messagesTF.setText(reply);
		
		try {
			if(reply.equals("connectionSuccess")) {
				out.println(firstName);
				System.out.println(firstName);
				out.println(lastName);
				System.out.println(lastName);
				out.println(myipAddress);
				System.out.println(myipAddress);
				
				showLoginScreen();
//				getContacts();				
				
			}
			
//			System.out.println("Client says: closing connection...");
//			clientSocket.close();
		}
		catch (Exception e) {
		}
		
		return reply;
	}
	
	private void getContacts() {
		try {
	//		write to the server
			 
			
			out.println("getContacts");
			
	//		read ouput from server
			String contact = input.readLine();
			
			while (!(contact.equals("end"))) {
				contacts.add(contact);
				contact = input.readLine();
			}
			
			messagesTF.setText("contacts updated");
			System.out.println("Client: contacts updated");
			
		} catch (IOException e) {
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
		Label passwordLabel = new Label("Username: ");
		TextField passwordTF = new TextField();
		
		Button loginButton = new Button("Login");
		
		Text feedback = new Text();
		
		usernameHB.getChildren().addAll(usernameLabel, usernameTF);
		passwordHB.getChildren().addAll(passwordLabel, passwordTF);
		
		
		loginButton.setOnAction(event ->{
			Client user = login(usernameTF.getText(), passwordTF.getText());
			
			if (!(user).equals(null)) {
//				show logged in screen
				feedback.setText("Success");
			} else {
				feedback.setText("Failed");
			}
		});
		
		rootLogin.getChildren().addAll(loginHeading, usernameHB, passwordHB, loginButton, feedback);
		Scene loginScene = new Scene(rootLogin, 400, 400);
		this.primaryStage.setScene(loginScene);
	}
	
	public Client login(String username, String password) {
		out.println("login");
		out.println(username);
		out.println(password);
		String reply;
		try {
			reply = input.readLine();
			if (reply.equals("loginSuccess")) {
				System.out.println("Reply: " + reply);
				
//				get client object and return it if the login is successful
				try {
					ois = new ObjectInputStream (clientSocket.getInputStream());
					Client me = (Client) ois.readObject();
					return me;
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				return client object
				
			} else {
				System.out.println("Reply: " + reply);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	
	}

	public static void main(String[] args) {
		
		launch(args);

	}

}
