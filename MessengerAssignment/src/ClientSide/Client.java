package ClientSide;


import java.io.IOException;
import java.io.InputStream;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import common.ClientData;
import common.EncryptionManager;
import common.Message;
import common.MessageCode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;



public class Client extends Thread {

	ObservableList<String> contacts = FXCollections.observableArrayList();
	Socket clientSocket;
	private ObjectOutputStream oOutputS;
	private ObjectInputStream oInputS;
	private ClientData user;
	private TextArea ta;
	private ClientGUI clientGUI;
	
	// keys
	private int myPrivateKey; 
	private Double sharedKey;
	int G = 3;
	int P = 17;
	
	 private String secret = ("" + sharedKey);
	 private String salt = "12345678";
	 private IvParameterSpec ivParameterSpec = EncryptionManager.generateIv();
	 private SecretKey key;
	
	
//	constructor throws an error if a connection can't be made
	public Client(ClientGUI gui, ObservableList<String> contacts, Socket clientSocket,ClientData user) throws IOException, ClassNotFoundException {
		this.clientGUI = gui;
		this.contacts = contacts;
		this.clientSocket = clientSocket;
		this.user = user;
		
//		create the output and input streams when connecting to the server
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
		Message connectionReply = (Message) oInputS.readObject();
		processMessage(connectionReply);

//		generate keys
		// create my private key
		myPrivateKey = getRandomKeyInt();
		System.out.println("client public key" + myPrivateKey);
		
		// calculate A and send to the server
		double  A = ((Math.pow(G, myPrivateKey)) % P); 
		System.out.println("A " + A);
		
		Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
		Message generateKeys = new Message(MessageCode.KEYS, "annon", "server", ""+ A , timestamp2);
		oOutputS.writeObject(generateKeys);
		
		// read the reply (with B from the server)
		Message keyReply = (Message) oInputS.readObject();
		processMessage(keyReply);

	}

	public void run() {
		System.out.println("Waiting on message");
		try {
				while(true) {
					// process incoming messages
					Message message = (Message)oInputS.readObject();
					System.out.println(message.toString());
					processMessage(message);	
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void processMessage(Message message) {
		if (message.getCode() == MessageCode.ADD_CONTACT) {
			if (!(contacts.contains(message.getPayload()))){
				contacts.add(message.getPayload());
			}
		} else if (message.getCode() == MessageCode.REMOVE_CONTACT) {
			contacts.remove(message.getPayload());
		} else if (message.getCode() == MessageCode.GET_CONTACTS) {
			// add each contact to the contacts list
			for(int i = 0; i < Integer.parseInt(message.getPayload()); i++ ) {
				
				try {
					ClientData client;
					client = (ClientData) oInputS.readObject();
					
					if (!(contacts.contains(client.getUserName()))){
						contacts.add(client.getUserName());
					}
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			System.out.println("Client: contacts updated");
		} else if (message.getCode() == MessageCode.MESSAGE) {
			// add the message to the messages text area
			ta.appendText("\nFrom " + message.getFromUsername() + ":\n\t" + message.getPayload());
			
			// send a reply to say that the message was recieved
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Message receivedMessage = new Message(MessageCode.MESSAGE_RECEIVED, user.getUserName(), message.getFromUsername(), message.getPayload(), timestamp);
			try {
				oOutputS.writeObject(receivedMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (message.getCode() == MessageCode.MESSAGE_RECEIVED) {
			ta.appendText("\n\t\tSent to " + message.getFromUsername() + ":\n\t\t\t" + message.getPayload());
		}else if (message.getCode() == MessageCode.GET_MESSAGES) {
			processOldMessages(message);
		} else if (message.getCode() == MessageCode.KEYS) {
			generateKey(message);
		} 
	}
	
	//https://www.javatpoint.com/diffie-hellman-algorithm-in-java
	//https://www.geeksforgeeks.org/java-implementation-of-diffie-hellman-algorithm-between-client-and-server/
	private void generateKey(Message message) {
	
		System.out.println(message.getPayload());
		// get B from the server
		double serverB = Double.parseDouble(message.getPayload());
		System.out.println(serverB);
		
		// calculate 
		sharedKey = ((Math.pow(serverB, myPrivateKey)) % P);

		System.out.println("shared key" + sharedKey);
		
//		NOTE!!!!!! IMPORTANT!!!
//  	Couldn't Diffie-Hellman keyshare to work systematically so have hard coded a key here to progress with encryption/decryption.
		sharedKey = (double) 123456789;
		secret = ("" + sharedKey);
		try {
			key = EncryptionManager.getKeyFromPassword(secret,salt);
			oOutputS.writeObject(key);

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

//	login sends the username and password to the server for verification 
//	if the credentials are verified, the server sends back a Client object with the users details.
//	the method returns this client or null if the details can't be verified
	public ClientData login(String username, String password) {
		System.out.println("calling login in client");
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Message loginMessage = new Message(MessageCode.LOGIN, username, "server", password, timestamp);
		
//	   encode the username and password before sending to the server
		try {
			
			String cipherUsername = EncryptionManager.encrypt(username, key, ivParameterSpec);
			String cipherPassword = EncryptionManager.encrypt(password, key, ivParameterSpec);
		    System.out.println(key.toString());
		    System.out.println(cipherUsername + " " + cipherPassword);
		    
		    username = EncryptionManager.decrypt(cipherUsername, key, ivParameterSpec);
		    
		    System.out.println(username);

//		    loginMessage.setFromUsername(cipherUsername);
//		    loginMessage.setFromUsername(cipherPassword);
		    
		} catch (NoSuchAlgorithmException | InvalidKeyException | 
				NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException | 
				IllegalBlockSizeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
// send the login request to the server	
		try {
			oOutputS.writeObject(loginMessage);
			
			Message message = (Message)oInputS.readObject();
			if (message.getPayload().equals("loginSuccess")) {
				
				try {
					user = (ClientData) oInputS.readObject();
					return user;
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				System.out.println("unsuccessful login");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public ClientData register(ClientData tempUser) {
		System.out.println("calling register in client");
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Message registerMessage = new Message(MessageCode.REGISTER, tempUser.getUserName(), "server", "", timestamp);
		
		
//		String plainText = username;
//	    String secret = ("" + sharedKey);
//	    String salt = "12345678";
//	    IvParameterSpec ivParameterSpec = EncryptionManager.generateIv();
//	    SecretKey key;
//		try {
//			key = EncryptionManager.getKeyFromPassword(secret,salt);
//			String cipherText = EncryptionManager.encrypt(plainText, key, ivParameterSpec);
//		    String decryptedCipherText = EncryptionManager.decrypt(
//		      cipherText, key, ivParameterSpec);
//		    
//		    System.out.println(cipherText);
//		    System.out.println(decryptedCipherText);
//		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | 
//				NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException | 
//				IllegalBlockSizeException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
	    
		
		try {
			// lets the server know that we are trying to register a new client and to expect the client object
			oOutputS.writeObject(registerMessage);
			
			// send the client object to be registered
			oOutputS.writeObject(tempUser);
			System.out.println("client sent to server " + tempUser.toString());
			
			// get reply
			user = (ClientData) oInputS.readObject();
			System.out.println("Server returned client " + user.toString());
			
			if (user != null) {
				return user;
			} else {
				System.out.println("unsuccessful registration");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	public void send(String destination, String messageText) {
		System.out.println("calling send message in client");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Message message = new Message(MessageCode.FORWARD_MESSAGE, user.getUserName(), destination, messageText, timestamp );
		try {
			oOutputS.writeObject(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateMessages(String contactUsername, TextArea ta) {
		// clear the message text area and write a heading
		System.out.println("calling updateMessages in client");
		
		this.ta = ta;
		ta.clear();
		ta.appendText("Messages with " + contactUsername);
		
		
		
		// request any old messages from the server
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Message requestMessages = new Message(MessageCode.GET_MESSAGES, user.getUserName(), "server", contactUsername, timestamp);
		
		try {
			oOutputS.writeObject(requestMessages);
			System.out.println("sent request to server");
			// replies are sent to the thread in the Client class
			
			
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void processOldMessages(Message message) {
		
		System.out.println("processing old messages");
		// check there are messages being returned
		if (message.getCode() == MessageCode.GET_MESSAGES) {
			// add each message to the message window
			for(int i = 0; i < Integer.parseInt(message.getPayload()); i++ ) {
				
				Message oldMessage;
				try {
					oldMessage = (Message) oInputS.readObject();
					
					// if it's a message this user sent 
					if (oldMessage.getFromUsername().equals(user.getUserName())) {
						ta.appendText("\n\t\tSent to " + oldMessage.getFromUsername() + ":\n\t\t\t" + oldMessage.getPayload());
						
					} else {
						ta.appendText("\nFrom " + oldMessage.getFromUsername() + ":\n\t" + oldMessage.getPayload());
					}
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		System.out.println("Client: contacts updated");
	}
	
	public void getContacts() {
		try {
			System.out.println("getContacts called");
	//		write to the server
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Message getContactsMessage = new Message(MessageCode.GET_CONTACTS, "server", user.getUserName(), "", timestamp);
			oOutputS.writeObject(getContactsMessage);
			System.out.println("contacts request sent to server");
			

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void logout() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Message logoutMessage = new Message(MessageCode.LOGOUT, user.getUserName(), "server", "", timestamp);
		try {
			oOutputS.writeObject(logoutMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		showLoginScreen();
	}
	
	private int getRandomKeyInt() {
		return (int) Math.floor(Math.random()*100 +1);
	}
	
}
