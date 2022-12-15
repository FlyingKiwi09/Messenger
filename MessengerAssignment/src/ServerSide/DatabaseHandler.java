package ServerSide;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.SortedSet;

import common.ClientData;
import common.Message;
import common.MessageCode;

public class DatabaseHandler {
	
	private Connection connection = null;

	public DatabaseHandler() throws ClassNotFoundException, SQLException {
//		set up the database connection
		String databaseUser = "root";
    	String databasePass = "";
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url = "jdbc:mysql://localhost/messenger";
		connection = DriverManager.getConnection(url, databaseUser, databasePass);
	
	}

	public ClientData validateLogin(String username, String password) {
		
		try {
			// query the database for a user with the username and password
	        String statement = "SELECT * FROM users WHERE users.username = ? AND users.password = ?";
			PreparedStatement pst = connection.prepareStatement(statement);
			
			pst.setString(1, username);
			pst.setString(2, password);
	
			ResultSet rs =	pst.executeQuery();
			
			if (rs.next()) {	
			 return new ClientData(rs.getString("username"), rs.getString("firstname"), rs.getString("lastname"), "");
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void saveMessage(Message message) {
		
		try {
			String statement = "INSERT INTO `messages` (sender_username, receiver_username, message, `timestamp`) VALUES (?, ?, ?, ?)";
			PreparedStatement pst = connection.prepareStatement(statement);
			
			// note the to and from usernames appear backwards because the message is the reply that confirms the message was recieved and not the original message that was sent.
			pst.setString(1, message.getToUsername());
			pst.setString(2, message.getFromUsername());
			pst.setString(3, message.getPayload());
			pst.setTimestamp(4, message.getTimestamp());
			
			int i = pst.executeUpdate();
			
			if (i > 0) {
				System.out.println("inserted " + i + "row");
			} else {
				System.out.println("row not inserted");
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Message> getMessages(String userUsername, String contactUsername) {
		
		ArrayList<Message> messages = new ArrayList<Message>();
		
		

		System.out.println(userUsername + contactUsername);
		try {
			// query the database 
	        String statement = "SELECT * FROM messages WHERE messages.sender_username = ? AND messages.receiver_username = ?";
			PreparedStatement pst;
			pst = connection.prepareStatement(statement);
			
			// for messages sent to the contact from the user
			pst.setString(1, userUsername);
			pst.setString(2, contactUsername);

			ResultSet sentMessages = pst.executeQuery();
	
			
			while(sentMessages.next()) {
				String sender = sentMessages.getString("sender_username");
				String receiver = sentMessages.getString("receiver_username");
				String payLoad = sentMessages.getString("message");
				Timestamp timestamp = sentMessages.getTimestamp("timestamp");
				
				Message message = new Message(MessageCode.MESSAGE, sender, receiver, payLoad, timestamp);
				
				messages.add(message);
			}
			
		
			
//			 for messages received by the user from the contact
			// query the database 
	        String receivedStatement = "SELECT * FROM messages WHERE messages.sender_username = ? AND messages.receiver_username = ?";
			PreparedStatement pstReceived;
			pstReceived = connection.prepareStatement(receivedStatement);
			
			pstReceived.setString(1, contactUsername);
			pstReceived.setString(2, userUsername);
			
			ResultSet recievedMessages =	pstReceived.executeQuery();
			
			while(recievedMessages.next()) {
				String sender = recievedMessages.getString("sender_username");
				String receiver = recievedMessages.getString("receiver_username");
				String payLoad = recievedMessages.getString("message");
				Timestamp timestamp = recievedMessages.getTimestamp("timestamp");
				
				Message message = new Message(MessageCode.MESSAGE, sender, receiver, payLoad, timestamp);
				
				messages.add(message);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		messages.sort(new MessageComparator());
		
		return messages;
		
	}
}
