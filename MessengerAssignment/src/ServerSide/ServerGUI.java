package ServerSide;
	
import ClientSide.Client;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;


public class ServerGUI extends Application {
	
	ObservableList<Client> ipAddressList;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			int portNum = 7777;
			ServicedServer servicedServer = new ServicedServer(portNum, ipAddressList);
			
			
			primaryStage.setTitle("HBox Experiment 1");

	        Button button1 = new Button("Start Server");
	        button1.setOnAction(value ->  {
	            System.out.println("Server started");
	            try
	            {
//	            	start the server
	            	System.out.println(portNum);
					servicedServer.start();

	            }
	            catch (Exception e)
	            {
	            	
	            }
	         });
	        Button button2 = new Button("Do something else");
	        button2.setOnAction(value ->  {
	        	System.out.println("Doing something else");
	         });
	        
	        HBox hbox = new HBox(button1, button2);
	        Scene scene = new Scene(hbox, 200, 100);
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
