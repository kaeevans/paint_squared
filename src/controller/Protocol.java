package controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * The object that the server and client transmit to communicate with each other.
 */
public class Protocol {

	public Command command;
	public String nameField, textField, colorField;
	public String gridX, gridY;
	public List<String> messageField, userList;
	public List<Tuple> roomList;
	public ClientHandler source;

	/**
	 * This command will parse a protocol from a given line of text, if the protocol is invalid
	 * will throw a warning.
	 * @param text The text protocol to parse
	 */
	public Protocol(String text)
	{
		command = Command.NONE;
		nameField = ""; 
		textField = "";
		userList = new ArrayList<String>();
		roomList = new ArrayList<Tuple>();
		messageField = new ArrayList<String>();

		//====Coloring logic====================
		
		// COLOR_FILL protocol:  [COLOR_FILL][roomname][color][gridX][gridY]
		// corresponding fields: [command][nameField][colorField][gridX][gridY]
		colorField = ""; // New field to specify color. Could piggyback on textField. Separated for clarity.
		
		//Following Will be converted to ints corresponding to appropriate square in grid
		gridX = ""; //X position on grid to be colored 
		gridY = ""; //Y position on grid to be colored
		
		//======end Coloring logic===============


		// First tokenize the string on '[' and ']'
		String[] rawTokens = text.split("\\[|\\]");
		List<String> tokens = new ArrayList<String>();
		// Remove all 0-length tokens and make iterable
		for(String tok: rawTokens)
			if(tok.length() > 0)
				tokens.add(tok);

		// Parse tokens
		for(int i = 0; i < tokens.size(); i++) {
			String token = tokens.get(i);
			// Parse the COMMAND header
			if(command == Command.NONE) {
				if(token.equals("CREATE"))
					command = Command.CREATE;
				else if(token.equals("JOIN"))
					command = Command.JOIN;
				else if(token.equals("ALIAS"))
					command = Command.ALIAS;
				else if(token.equals("LEAVE"))
					command = Command.LEAVE;
				else if(token.equals("MSG"))
					command = Command.MSG;
				else if(token.equals("INVITED"))
					command = Command.INVITED;
				else if(token.equals("ASSIGNED"))
					command = Command.ASSIGNED;
				else if(token.equals("TRANSCRIPT"))
					command = Command.TRANSCRIPT;
				else if(token.equals("ROOMLIST"))
					command = Command.ROOMLIST;
				else if(token.equals("USERLIST"))
					command = Command.USERLIST;
				else if(token.equals("KILL"))
					command = Command.KILL;
				else if (token.equals("USERSINROOM"))
					command = Command.USERSINROOM;
				else if (token.equals("INVITEMANY"))
					command = Command.INVITEMANY;
				else if (token.equals("COLOR_FILL")) //color fill for grid square
					command = Command.COLOR_FILL;
				else
					System.err.println("Warning, ["+token+"] is an unsupported command.");
			} else if((command == Command.CREATE || command == Command.JOIN || command == Command.ALIAS ||
					command == Command.INVITED || command == Command.ASSIGNED || command == Command.LEAVE ||
					command == Command.MSG || command == Command.TRANSCRIPT || command == Command.KILL) && nameField.length() == 0) {
				nameField = token;
			} else if((command == Command.MSG) && nameField.length() > 0) {
				textField = token;
			} else if ((command == Command.COLOR_FILL)){ //logic for Color_Fill
				if (i == 1) nameField = token;
				else if (i==2) colorField = token;
				else if (i==3) gridX = token;
				else gridY = token;	
			} else if (((command == Command.TRANSCRIPT) && nameField.length() > 0) || (command == Command.INVITEMANY)) {
				if (i == 1) nameField = token;
				else messageField.add(token); 
			}
			else if(command == Command.USERLIST) {
				userList.add(token);
			} else if (command == Command.USERSINROOM) {
				if (i == 1) textField = token;
				else userList.add(token);
			} else if(command == Command.ROOMLIST) {
				roomList.add(new Tuple(token));
			}
		}
	}

	// Alternate protocols for server-generated requests on the model
	/**
	 * @param action The given action of the protocol
	 * @param data The augmented data for the protocol
	 */
	public Protocol(Command action, String data) {
		nameField = data;
		command = action;		
	}

	/**
	 * @param action The given action of the protocol
	 * @param data The list of rooms for the protocol
	 */
	public Protocol(Command action, List<Tuple> rooms) {
		command = action;
		roomList = rooms;
	}

	/**
	 * Creates a USERLIST protocol
	 * @param users List of the usernames as Strings
	 */
	public Protocol(List<String> users) {
		command = Command.USERLIST;
		userList = users;
	}

	/**
	 * Creates a USERSINROOM protocol
	 * @param userSet Set of the usernames as Strings, or the 
	 * @param roomname
	 */
	public Protocol(Set<String> userSet, String roomname) {
		command = Command.USERSINROOM;
		textField = roomname;
		userList = new ArrayList<String>();
		for(String s : userSet) {
			userList.add(s);
		}
	}

	/**
	 * @param action The command of the protocol
	 * @param name The name field of the protocol
	 * @param text A list of text tokens for the protocol
	 */
	public Protocol(Command action, String name, List<String> text) {
		command = action;
		nameField = name;
		messageField = (text == null)? new ArrayList<String>() : text;
	}

	/**
	 * @param action The command of the protocol
	 * @param name The name field of the protocol
	 * @param text The text field of the protocol
	 */
	public Protocol(Command action, String name, String text) {
		command = action;
		nameField = name;
		textField = text;
	}
	
	/**
	 * @param action The command of the protocol
	 * @param name The name field of the protocol
	 * @param color The color field of the protocol
	 * @param gridX The gridX field of the protocol
	 * @param gridY the gridY field of the protocol
	 */
	public Protocol(Command action, String name, String color, String gridX, String gridY){
		command = action;
		nameField = name;
		colorField = color;
		this.gridX = gridX;
		this.gridY = gridY;
	}

}
