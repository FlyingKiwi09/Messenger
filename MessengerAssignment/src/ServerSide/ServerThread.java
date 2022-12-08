package ServerSide;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ClientSide.Client;
import javafx.collections.ObservableList;

public class ServerThread extends Thread {
	
	Socket socket;
	ObservableList<Client> ipAddressList;

	public ServerThread(Socket socket, ObservableList<Client> ipAddressList) {
		this.socket = socket;
		this.ipAddressList = ipAddressList;
	}

	public ServerThread(Runnable target) {
		super(target);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(ThreadGroup group, Runnable target) {
		super(group, target);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(ThreadGroup group, String name) {
		super(group, name);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(Runnable target, String name) {
		super(target, name);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
		// TODO Auto-generated constructor stub
	}

	public ServerThread(ThreadGroup group, Runnable target, String name, long stackSize, boolean inheritThreadLocals) {
		super(group, target, name, stackSize, inheritThreadLocals);
		// TODO Auto-generated constructor stub
	}
	
	public void run() {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			System.out.println("A client request received at " + socket);
			out.println("Alina's multi threaded Server");
			socket.close();
		} catch(IOException e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}
	

}
