package ServerSide;
	
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class ServerGUI extends Application {
	
//	list and table to view the connected clients
	private ObservableList<ClientData> ipAddressList = FXCollections.observableArrayList();
	private TableView<ClientData> clientTable = new TableView<ClientData>();
	
//	ServicedServer and Server 
	private ServicedServer servicedServer;
	private Server server;
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
//	set up display
			
		//create elements
			VBox root = new VBox();
			
			//port input
			HBox portInputHBox = new HBox();
			Label portLabel = new Label("Enter Port Number:");
			TextField portInput = new TextField();
			
			//buttons
			HBox buttonsHBox = new HBox();
			Button startServerButton = new Button("Start Server");
			Button stopServerButton = new Button("Stop Server");
			Button test = new Button("test");
			
			//table
			clientTable.setEditable(true);
			
			TableColumn<ClientData, String> firstNameCol = new TableColumn("First Name");
			firstNameCol.setCellValueFactory(new PropertyValueFactory<ClientData, String>("firstName"));

			TableColumn<ClientData, String> lastNameCol = new TableColumn("Last Name");
			lastNameCol.setCellValueFactory(new PropertyValueFactory<ClientData, String>("lastName"));
			
			TableColumn<ClientData, String> ipAddressCol = new TableColumn("IP Address");
			ipAddressCol.setCellValueFactory(new PropertyValueFactory<ClientData, String>("ipAddress"));
			
			clientTable.setItems(ipAddressList);
			clientTable.getColumns().addAll(firstNameCol, lastNameCol, ipAddressCol);
			
		//add elements to containers
			root.getChildren().addAll(portInputHBox, buttonsHBox, clientTable);
			buttonsHBox.getChildren().addAll(startServerButton, stopServerButton, test);
			portInputHBox.getChildren().addAll(portLabel, portInput);
			
////        	hard coded variables for testing...
//        	int portNum = 7777;		

// set up onActions for buttons
	        
	        startServerButton.setOnAction(value ->  {
	            System.out.println("Starting Server");
	         // get the user input port number
            	Integer portNum = Integer.parseInt(portInput.getText());
            	
	            try
	            {
	            	
            	//start the server
	            	System.out.println(portNum);
	            	servicedServer = new ServicedServer(portNum, ipAddressList);
					servicedServer.start();
					server = servicedServer.getServer();
					
					System.out.println("Connected to port: " + portNum);

	            }
	            catch (Exception e)
	            {
	            	System.out.println("Could not connect to port: " + portNum);
	            	e.printStackTrace();
	            }
	         });

	        stopServerButton.setOnAction(value ->  {
	        	System.out.println("Calling ServicedServer.stopServer() Server");
	        	try {
	        		
		        	servicedServer.stopServer();
		        	
		        	if (server != null) {
		        		server.stopServer();
		        	} else {
		        		System.out.println("server null");
		        	}
		        	
	        	}catch (IOException e) {
						e.printStackTrace();
	        	}
	         });
  
	        test.setOnAction(value -> {
	        	System.out.println("testing");
	        });
	        
// create scene and set up primaryStage	        
	        Scene scene = new Scene(root, 200, 100);
			primaryStage.setTitle("Server GUI");
	        primaryStage.setScene(scene);
	        primaryStage.show();

	} catch(Exception e) {
		e.printStackTrace();
	}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
