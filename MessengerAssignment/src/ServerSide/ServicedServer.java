package ServerSide;

import ClientSide.Client;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class ServicedServer extends Service <String>{
	
	int port;
	ObservableList<Client> ipAddressList;
	private Server server;

	public ServicedServer(int port, ObservableList<Client> ipAddressList) {
		
		setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				
				System.out.println("This is setOnSucceeded " + server);
				
			}
		});
		
		this.port = port;
		this.ipAddressList = ipAddressList;
	}


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
				
				return "nothing";
			}
		};
	}


	public Server getServer() {
		return server;
	}

	
}
