package model;

import model.Room;
import model.Message;
import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import controller.Tuple;
/*
 * Model class 
 * handles back end operations
 */
public class Model {
	
	private Set<User> users; 
	private Set<Room> rooms;
	
	/*
	 * Constructor
	 */
	public Model()
	{
		User.resetUsers();
		Room.resetRooms();
		users = new HashSet<User>();
		rooms = new HashSet<Room>();
	}
	
	/**
	 * Create a new chatroom
	 */
	public String newRoom(String requestedName)
	{
		requestedName = Message.makeSafe(requestedName);
		Room r = new Room(requestedName);
		rooms.add(r);
		return(r.getName());
	}
	
	/**
	 * Creates a new user, all users begin with the alias
	 * 'Anonymous_#' where # is a unique suffix
	 */
	public String newUser()
	{
		User u = new User();
		users.add(u);
		return(u.getAlias());
	}
	
	/**
	 * Removes the user from the model (the user can still be found,
	 * but
	 * @param alias
	 * @return
	 */
	public boolean killUser(String alias)
	{
		User user = User.find(alias);
		boolean success = true;
		if(users.contains(user))
		{
			users.remove(user);
			for(Room r : rooms)
				r.leave(user);
			success = User.delete(alias);
		}
		return(success);
	}
	
	/**
	 * Attempts an alias change. Returns error string if the
	 * provided user was not found, otherwise returns the
	 * new alias name (with a possible suffix)
	 */
	public String changeAlias(String name, String newName)
	{
		newName = Message.makeSafe(newName);
		User found = User.find(name);
		String newAlias = "ERROR 1: UNKNOWN USER";
		if(found != null)
		{
			newAlias = found.requestAlias(newName);
		}
		if(!users.contains(found))
			newAlias = "ERROR 2: USER WAS KILLED";
		return(newAlias);
	}
	
	/**
	 * Tries to add the named user to the named room,
	 * returns true if successful, false if impossible
	 */
	public boolean joinRoom(String alias, String roomname)
	{
		boolean success = true;
		User user = User.find(alias);
		Room room = Room.find(roomname);
		if(user == null){
			System.err.println("Warning, tried to join a non-existent user to the room.");
			success = false;
		}
		else if(room == null){
			System.err.println("Warning, tried to join a room which doesn't exist.");
			success = false;
		}
		else if(!users.contains(user)){
			System.err.println("Warning, tried to join a killed user to a room.");
			success = false;
		}
		else if(!rooms.contains(room)){
			System.err.println("Warning, tried to join a killed room.");
			success = false;
		}
		else
			room.join(user);
		
		return(success);
	}
	
	/**
	 * Tries to remove the named user to the named room,
	 * returns true if successful, false if impossible
	 */
	public boolean leaveRoom(String alias, String roomname)
	{
		boolean success = true;
		User user = User.find(alias);
		Room room = Room.find(roomname);
		if(user == null){
			System.err.println("Warning, tried to exit a non-existent user from a room.");
			success = false;
		}
		else if(room == null){
			System.err.println("Warning, tried to exit a room which doesn't exist.");
			success = false;
		}
		else if(!users.contains(user)){
			System.err.println("Warning, tried to exit a killed user from a room.");
			success = false;
		}
		else if(!rooms.contains(room)){
			System.err.println("Warning, tried to exit a destroyed room.");
			success = false;
		}
		else{
			boolean left = room.leave(user);
			if(!left)
			{
				System.err.println("Warning, tried to exit from a room you weren't an attendee of.");
				success = false;
			}
		}
		return(success);
	}	
	
	/**
	 * Attempts to write the given message from the given alias to the given roomname,
	 * returns true if successful, false if there was an error (and outputs a warning)
	 */
	public boolean writeMessage(String alias, String roomname, String messageText)
	{
		boolean success = true;
		User user = User.find(alias);
		Room room = Room.find(roomname);
		if(user == null){
			System.err.println("Warning, tried to write from a non-existent user.");
			success = false;
		}
		else if(room == null){
			System.err.println("Warning, tried to write to a room which doesn't exist.");
			success = false;
		}
		else if(!users.contains(user)){
			System.err.println("Warning, tried to write from a killed user.");
			success = false;
		}
		else if(!rooms.contains(room)){
			System.err.println("Warning, tried to write to a destroyed room.");
			success = false;
		}
		else{
			messageText = Message.makeSafe(messageText);
			Message message = new Message(alias, messageText);
			boolean written = room.write(user, message);
			if(!written)
				System.err.println("Warning, tried to write to room where not attendee.");
			success = success&&written;
		}
		
		return(success);		
	}
	
	/**
	 * Gets the transcript from a given room, if numMessages is not provided,
	 * returns the entire transcript.
	 */
	public List<String> getTranscript(String roomname, int numMessages)
	{
		//String transcript;
		List<String> transcript = new ArrayList<String>();
		Room room = Room.find(roomname);
		if(room == null){
			System.err.println("Warning, tried to recover transcript from non-existent room.");	
			transcript.add("ERROR: Room does not exist.");
		}
		else if(!rooms.contains(room)){
			System.err.println("Warning, tried to recover transcript from destroyed room.");
			transcript.add("ERROR: Room was destroyed.");
		}
		transcript = room.getNewestMessages(numMessages);
		return(transcript);
	}
	
	public List<String> getTranscript(String roomname)
	{
		return getTranscript(roomname, -1);
	}
	
	/**
	 * Returns a list of all roomnames currently available
	 */
	public List<Tuple> getRooms()
	{
		List<Tuple> roomnames = new ArrayList<Tuple>();
		for(Room r : rooms)
			roomnames.add(new Tuple(r.getName(), r.attendees().size()));
		return roomnames;
	}
	
	public List<Room> getRoomList(){
		List<Room> rooms = new ArrayList<Room>();
		for(Room r : rooms)
			rooms.add(r);
		return rooms;
	}

	/**
	 * Returns a list of all aliases currently in use
	 */
	public List<String> getUsers()
	{
		List<String> usernames = new ArrayList<String>();
		for(User u : users)
			usernames.add(u.getAlias());
		return usernames;
	}
	
	/**
	 * Returns a list of all users inside the specified chatroom
	 */
	public List<String> getAttendees(String roomname)
	{
		Room room = Room.find(roomname);
		if(roomname != null)
		{
			List<String> usernames = new ArrayList<String>();
			Set<User> users = room.attendees();
			for(User u : users)
				usernames.add(u.getAlias());
			return(usernames);
		}else{
			System.err.println("Warning, tried to get attendees from room which doesn't exist.");
			return(null);
		}
	}
}
