package ServerSide;

import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

// Service runs a thread by performing the work defined in Task - this work is run in a separate thread
public class ServicedServer extends Service <String>{
	
	int port;
	ObservableList<Client> ipAddressList;
	private Server server;

	public ServicedServer(int port, ObservableList<Client> ipAddressList) {
		
//		runs when the service is successfully terminated
		setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				
				System.out.println("This is setOnSucceeded " + server);
				
			}
		});
		
		this.port = port;
		this.ipAddressList = ipAddressList;
	}

// the service performs the work defined in the call method of the task
	@Override
	protected Task<String> createTask() {

		return new Task <String>()
		{
			@Override
			protected String call() throws Exception {
				//Thread.sleep(10000);
				
				System.out.println("Creating task....");
				server = new Server(port, ipAddressList);
				
				System.out.println("Task Created .....");
				
				return "nothing"; // returns a variable when the work is completed
			}
		};
	}
	
	public void stopServer() throws IOException {
		System.out.println("Stopping Server...");
		if(server != null) {
			server.stopServer();
			System.out.println("Server Stopped");
		} else {
			System.out.println("Could not stop server");
		}
	}


	public Server getServer() {
		return server;
	}

	
}
