package ClientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientGUI extends Application {
	
	private String serverAddress = "localhost";
	private int port = 7777;
	private String firstName = "Bob";
	private String lastName = "Hook";
	private String myipAddress = "ipAddress";

	public ClientGUI() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		VBox root = new VBox();
		Button connect = new Button("Connect");		
		root.getChildren().add(connect);
		
		connect.setOnAction(event -> {
			try {
				connectToServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		// scene set up
		primaryStage.setScene(new Scene(root, 300, 400));
		primaryStage.sizeToScene();
		primaryStage.setTitle("Chat Box Clientside");
		primaryStage.show();
	}
	
	public String connectToServer() throws UnknownHostException, IOException {
		
		System.out.println("Client says: trying to connect to server...");
		
//		connect to the server
		Socket clientSocket = new Socket(serverAddress, port);
		
		System.out.println("Client says: connected");
		
//		write to the server
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		out.println("ClientConnected");
		
//		read ouput from server
		BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String reply = input.readLine();
		System.out.println("Client gets reply from server: " + reply);
		

		
		try {
			if(reply.equals("connectionSuccess")) {
				out.println(firstName);
				System.out.println(firstName);
				out.println(lastName);
				System.out.println(lastName);
				out.println(myipAddress);
				System.out.println(myipAddress);
			}
			
			System.out.println("Client says: closing connection..");
			clientSocket.close();
		}
		catch (Exception e) {
		}
		
		return reply;
	}

	public static void main(String[] args) {
		launch(args);

	}

}
