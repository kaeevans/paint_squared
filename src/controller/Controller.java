package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import model.Model;

/**
 * The controller for our program, following the MVC pattern.
 */
public class Controller implements Runnable{
	
	// Queue the controller used to process incoming client requests
	BlockingQueue<Protocol> requests;
	List<ClientHandler> clients;
	Model theModel;
	
	// Controls the amount of messages visible in a chatroom
	private final static int MSG_BUFFER_SIZE = 100;
	
	/** Constructor for the controller. Establishes the server connection. */
	public Controller(){
		requests = new ArrayBlockingQueue<Protocol>(100000);
		clients = new ArrayList<ClientHandler>();
		theModel = new Model();
	}
	

	/** @returns The number of messages visible in a chatroom */
	public static int getMsgBufferSize() {
		return MSG_BUFFER_SIZE;
	}

	/**
	 * Starts the controller which consumes (and waits for) requests
	 * made to it in the form of Protocol objects, and then modifies
	 * the model and pushes messages to any clients.
	 */
	@Override
	public void run() {
		while(true) {
			try {
				//Get the next command
				Protocol action = requests.take();	
				execute(action);
			} catch(InterruptedException e) {
				System.err.println("Error taking command from queue: "+e.getMessage());
			}
		}
	}

	/** @returns The client-to-server request queue */
	public BlockingQueue<Protocol> getRequestQueue() {
		return requests;
	}
	
	/**
	 * Adds a new client to the client list (initiated by Server when accepting connections)
	 * @param c The client handler of the new client added
	 */
	public void addClient(ClientHandler c)
	{
		String name = theModel.newUser();
		clients.add(c);
		c.alias = name;
		// Give them their starting name
		c.send(new Protocol(Command.ASSIGNED, name));
		// Send a roomlist just to the new client, but send a userlist to everyone (updating users)
		List<Tuple> rooms = theModel.getRooms();
		c.send(new Protocol(Command.ROOMLIST, rooms));
		sendUserList();
	}
	
	/**
	 * Removes a client, given an alias.
	 * @param alias The username of the client to remove
	 * @returns Whether the remove was successful
	 */
	public boolean removeClient(String alias) {
		boolean success = false;
		for(ClientHandler c : clients) {
			if(c.alias.equals(alias)) {
				success = true;
				clients.remove(c);
				break;
			}
		}
		return(success);	
	}
	
	/** Sends server-client protocol of the all users online to all clients */
	public void sendUserList() {
		List<String> users = theModel.getUsers();
		for(ClientHandler c : clients)
			c.send(new Protocol(users));		
	}
	
	/** 
	 * Sends server-client protocol of all users in the chatroom roomname
	 * @param roomname
	 */
	public void sendUsersInRoomList(String roomname)
	{
	    // Get users in the chatroom
		List<String> users = theModel.getAttendees(roomname);
		Set<String> userSet = new HashSet<String>(users.size());
		for (String s: users)
			userSet.add(s);
		// Send USERSINROOM protocol to those users
		for(ClientHandler c : clients){
			if(userSet.contains(c.alias))
                c.send(new Protocol(userSet, roomname));
		}
	}
	
	/**
	 * Executes the provided protocol by querying the model as well as sending
	 * messages to any clients who need updated information
	 * @param p The client-server protocol to execute 
	 */
	public void execute(Protocol p)
	{
		boolean success = true;
		switch(p.command) {
    		// Create a new chatroom, send the client a message that it has been invited,
    		// Send a message to all clients the updated ROOMLIST
    		case CREATE:
    		    // Get creating user to join room, tell client that user has joined
    			String name = theModel.newRoom(p.nameField);
    			success = theModel.joinRoom(p.source.alias, name);
    			p.source.send(new Protocol(Command.INVITED, name));
    			
    			// Start new message history for chatrom
    			List<String> transcript  = new ArrayList<String>();
                p.source.send(new Protocol(Command.TRANSCRIPT, p.nameField, transcript)); 
    
                // Send updated room list, user list, and users in the room list
    			List<Tuple> rooms = theModel.getRooms();
    			for(ClientHandler c : clients)
    				c.send(new Protocol(Command.ROOMLIST, rooms));
    			sendUserList();
    	        sendUsersInRoomList(p.nameField);        
    
    			break;
    			
    		// Join an existing chatroom, send the client a message that it has been invited, send them the transcript
    		// Send a message to all clients the updated ROOMLIST
    		case JOIN:
    			success = theModel.joinRoom(p.source.alias, p.nameField);
    			p.source.send(new Protocol(Command.INVITED, p.nameField)); // Notify invite was successful
    			
    			rooms = theModel.getRooms();
    			transcript  = theModel.getTranscript(p.nameField);
    			p.source.send(new Protocol(Command.TRANSCRIPT, p.nameField, transcript)); 
    			
    			for(ClientHandler c : clients)
    			    c.send(new Protocol(Command.ROOMLIST, rooms));
    			sendUserList();
    			sendUsersInRoomList(p.nameField);				
    			break;
    			
    		// Exit an existing chatroom, send the ROOMLIST and USERSINROOM to all clients
    		case LEAVE:
    			success = theModel.leaveRoom(p.source.alias, p.nameField);
    			rooms = theModel.getRooms();
    			for(ClientHandler c : clients)
    				c.send(new Protocol(Command.ROOMLIST, rooms));		
    			sendUserList();
    			sendUsersInRoomList(p.nameField);
    			break;
    			
    		// Change the alias of the client, send back the ASSIGNED name to the client and
    		// send a message to all clients the updated USERLIST
    		case ALIAS:
    			name = theModel.changeAlias(p.source.alias, p.nameField);
    			p.source.alias = name;
    			p.source.send(new Protocol(Command.ASSIGNED, name));
    			sendUserList();
    			break;
    			
    		// Add the message to the chatroom, from the client. Send the transcript to all
    		// clients inside the room
    		case MSG:
    			success = theModel.writeMessage(p.source.alias, p.nameField, p.textField);
    			List<String> users = theModel.getAttendees(p.nameField);
    			transcript = theModel.getTranscript(p.nameField, getMsgBufferSize());
    			for(ClientHandler c : clients) {
    				if(users.contains(c.alias))
    					c.send(new Protocol(Command.TRANSCRIPT, p.nameField, transcript));
    			}
    			break;
    			
    		// When client threads die, they send a kill request to remove the user and
    		// should update all the clients with the new USERLIST and ROOMLIST (because capacity may have changed)
    		case KILL:
    			success = theModel.killUser(p.nameField);
    			rooms = theModel.getRooms();
    			for(ClientHandler c : clients)
    				c.send(new Protocol(Command.ROOMLIST, rooms));	
    			// Remove the client from the list of clients
    			success = success && removeClient(p.nameField);	
    			sendUserList();
    			// Notify each room with updated users in room list
    			for (Tuple room: rooms)
    			    sendUsersInRoomList(room.name);
    			break;	
    			
    		// Interpret an empty transcript command as a request for the transcript	
    		case TRANSCRIPT:
    			transcript = theModel.getTranscript(p.nameField, getMsgBufferSize());
    			p.source.send(new Protocol(Command.TRANSCRIPT, p.nameField, transcript));
    			break;
    			
    		// Interpret an empty roomlist command as a request for the roomlist	
    		case ROOMLIST:
    			rooms = theModel.getRooms();
    			p.source.send(new Protocol(Command.ROOMLIST, rooms));
    			break;
    		
    		case INVITEMANY: 
    			for(ClientHandler c: clients) {
    			    // Self-add each invited client to chatroom
    				if(p.messageField.contains(c.alias)) 
    					c.queueCommand(new Protocol(Command.JOIN, p.nameField));
    			}
    			break;
    			
    		// Warning message for all protocols which the server cannot execute
    		default:
    			System.err.println("Warning, server should not be receiving message: "+p.command.toString());
    			break;
		}
		
		// If any of the model queries failed, send a warning
		if(!success)
			System.err.println("Warning, error attempting to execute command: "+p.command.toString());
	}
}
