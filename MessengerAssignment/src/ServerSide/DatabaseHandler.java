package ServerSide;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import common.ClientData;

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
}
