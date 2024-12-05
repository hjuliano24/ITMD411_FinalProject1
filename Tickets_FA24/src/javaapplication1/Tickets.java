package javaapplication1;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null;

	// Main menu object items
	private JMenu mnuFile = new JMenu("File");
	private JMenu mnuAdmin = new JMenu("Admin");
	private JMenu mnuTickets = new JMenu("Tickets");

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit;
	JMenuItem mnuItemUpdate;
	JMenuItem mnuItemDelete;
	JMenuItem mnuItemCreateTicket;
	JMenuItem mnuItemViewTicket;
	JMenuItem mnuItemClose;
	private Component frame;

	public Tickets(Boolean isAdmin) throws SQLException {

		chkIfAdmin = isAdmin;
		createMenu();
		prepareGUI();

	}

	private void createMenu() {

		/* Initialize sub menu items **************************************/

		// initialize sub menu item for File main menu
		mnuItemExit = new JMenuItem("Exit");
		// add to File main menu item
		mnuFile.add(mnuItemExit);

		// initialize first sub menu items for Admin main menu
		mnuItemUpdate = new JMenuItem("Update Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemUpdate);

		// initialize second sub menu items for Admin main menu
		mnuItemDelete = new JMenuItem("Delete Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemDelete);

		// initialize first sub menu item for Tickets main menu
		mnuItemCreateTicket = new JMenuItem("Create Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemCreateTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemViewTicket = new JMenuItem("View Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemClose = new JMenuItem("Close Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemClose);

		// initialize second sub menu item for Tickets main menu

		// initialize any more desired sub menu items below

		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
		mnuItemUpdate.addActionListener(this);
		mnuItemDelete.addActionListener(this);
		mnuItemCreateTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);
		mnuItemClose.addActionListener(this);

		/*
		 * continue implementing any other desired sub menu items (like
		 * for update and delete sub menus for example) with similar
		 * syntax & logic as shown above
		 */

	}

	private void prepareGUI() throws SQLException {

		// create JMenu bar
		JMenuBar bar = new JMenuBar();
		bar.add(mnuFile); // add main menu items in order, to JMenuBar

		// only shows admin menu if your an admin
		if (chkIfAdmin == true) {
			bar.add(mnuAdmin);
		}

		bar.add(mnuTickets);

		// add menu bar components to frame
		setJMenuBar(bar);

		addWindowListener(new WindowAdapter() {
			// define a window close operation
			public void windowClosing(WindowEvent wE) {
				System.exit(0);
			}
		});
		// set frame options
		setSize(400, 400);
		getContentPane().setBackground(Color.WHITE);
		setLocationRelativeTo(null);
		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// implement actions for sub menu items
		if (e.getSource() == mnuItemExit) {
			System.exit(0);
		} else if (e.getSource() == mnuItemCreateTicket) {

			// get ticket information
			String ticketName = JOptionPane.showInputDialog(null, "Enter your name");
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");

			// insert ticket information to database

			int id = dao.insertRecords(ticketName, ticketDesc);

			// display results if successful or not to console / dialog box
			if (id != 0) {
				System.out.println("Ticket ID : " + id + " created successfully!!!");
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
			} else
				System.out.println("Ticket cannot be created!!!");
		}

		else if (e.getSource() == mnuItemViewTicket) {

			// retrieve all tickets details for viewing in JTable
			try {

				// Clears all rows and columns in the JTable

				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == mnuItemDelete) {
			String ticket_id_str = JOptionPane.showInputDialog(null, "Enter ticket id:");
			int ticket_id = Integer.parseInt(ticket_id_str);

			// pop up before deleting
			Object[] options = { "Yes", "No" };
			int result = JOptionPane.showOptionDialog(null, "Are you sure? ", "Delete",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (result == 0) {
				int id = dao.deleteRecords(ticket_id);
			} else {
				System.out.println("id is not valid");
			}
			// Shows new popup window on top of the existing one
			// display results if successful or not to console / dialog box

		} else if (e.getSource() == mnuItemUpdate) {
			String ticket_id_str = JOptionPane.showInputDialog(null, "Enter ticket id:");
			int ticket_id = Integer.parseInt(ticket_id_str);

			String input = JOptionPane.showInputDialog(frame, "Enter new Description:", "Input Required",
					JOptionPane.PLAIN_MESSAGE);
			dao.updateRecords(ticket_id, input);

		} else if (e.getSource() == mnuItemClose) {
			String ticket_id_str = JOptionPane.showInputDialog(null, "Enter ticket id:");
			int ticket_id = Integer.parseInt(ticket_id_str);
			dao.closeRecords(ticket_id);
		}

		// additional: refresh button
		/*
		 * continue implementing any other desired sub menu items (like for update and
		 * delete sub menus for example) with similar syntax & logic as shown above
		 */

	}

}
