package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {

	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() throws SQLException {
		// Database table
		final String createTicketsTable = "CREATE TABLE IF NOT EXISTS hjuli_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200)), ticket_status VARCHAR(20) DEFAULT 'open')";
		final String createUsersTable = "CREATE TABLE IF NOT EXISTS hjuli_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";
		// checks if user is 0
		final String checkUser = "SELECT * FROM hjuli_users WHERE uid > 0";
		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			System.out.println("Created tables in given database...");

			// executes check user queries
			ResultSet rs = statement.executeQuery(checkUser);
			// add users to user table if no users
			if (rs.next()) {
				// users exits no need to add User
			} else {
				addUsers();
			}

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into hjuli_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDesc) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("Insert into hjuli_tickets" + "(ticket_issuer, ticket_description) values(" + " '"
					+ ticketName + "','" + ticketDesc + "')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	public ResultSet readRecords() {

		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM hjuli_tickets");
			// connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}

	// updateRecords implementation
	public void updateRecords(int ticket_id, String newDesc) {
		// - Update record (fields?) by some ticketnum
		String query = "UPDATE hjuli_tickets SET ticket_description = ? WHERE ticket_id = ?";
		try (PreparedStatement stmt = connect.prepareStatement(query)) {
			stmt.setString(1, newDesc);
			stmt.setInt(2, ticket_id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error updating ticket: " + e.getMessage());
		}
	}

	// deleteRecords implementation
	public int deleteRecords(int ticket_id) {
		// Delete record by some ticketnum.
		String delete = "DELETE FROM hjuli_tickets WHERE ticket_id = ?";
		try (PreparedStatement stmt = connect.prepareStatement(delete)) {
			stmt.setInt(1, ticket_id);
			int rowsDeleted = stmt.executeUpdate();

			if (rowsDeleted > 0) {
				System.out.println("Deleted ticket successfully!");
			} else {
				System.out.println("No tickets deleted.");
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error Deleting ticket: " + e.getMessage());
		}
		return 0;
	}

	// close ticket
	public int closeRecords(int ticket_id) {
		// open to close
		String close = "UPDATE hjuli_tickets SET ticket_status = 'close' WHERE ticket_id = ?";
		try (PreparedStatement stmt = connect.prepareStatement(close)) {
			stmt.setInt(1, ticket_id);
			int rowsClose = stmt.executeUpdate();

			if (rowsClose > 0) {
				System.out.println("Changed ticket status");
			} else {
				System.out.println("Can't change ticket status.");
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error Closing ticket: " + e.getMessage());
		}
		return 0;
	}

}
