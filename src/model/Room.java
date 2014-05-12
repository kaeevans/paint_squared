package model;

import model.User;
import model.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import controller.Controller;

/*
 * Room class
 * Stores information about the chat rooms
 */
public class Room {
	
	String name;
	private Set<User> attendees;
	private List<Message> transcript;
	private static Map<String, Room> allRooms = new LinkedHashMap<String, Room>(); 
	
	/**
	 * Generates a new room, with the default name, but attaches a suffix to
	 * guarantee uniqueness
	 * @param defaultName
	 */
	public Room(String defaultName)
	{
		attendees = new HashSet<User>();
		transcript = new LinkedList<Message>();
		
		int n = 1;
		String called = defaultName;
		while(allRooms.get(called) != null) {
			n++;
			called = defaultName + "_" + Integer.toString(n); 
		}
		name = called;
		allRooms.put(called, this);
	}
	
	//Getter to get the roommname
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the n newest messages in the transcript
	 * @return
	 */
	public List<String> getNewestMessages(int num)
	{
		List<String> messages = new ArrayList<String>();
		int n = 0;
		for(Message m : transcript)
		{
			if(num > 0 && n >= num)
				break;
			n++;
			messages.add(m.toString());
		}
		return(messages);
	}
	
	/**
	 * Adds a message (to the beginning of the list)
	 * @param m
	 */
	private void addMessage(Message m)
	{
		transcript.add(0, m);
		if(transcript.size()>Controller.getMsgBufferSize()){
			for(int ii=transcript.size(); ii>Controller.getMsgBufferSize(); ii--)
				transcript.remove(transcript.size()-1);
		}
	}
	
	/**
	 * This adds a message iff the user is in
	 * the room, returns false if not.
	 */
	public boolean write(User u, Message m)
	{
		if(attendees.contains(u))
		{
			addMessage(m);
			return(true);
		}
		return(false);
	}
	
	//Adds the user to the room
	public void join(User u)
	{
		attendees.add(u);
	}
	
	/**
	 * Exits the specified user from the room
	 * @param u - User to leave
	 * @return False if the user wasn't in the room
	 */
	public boolean leave(User u)
	{
		if(attendees.contains(u))
		{
			attendees.remove(u);
			return true;
		}
		return false;
	}
	
	//Gives the set of attendees
	public Set<User> attendees() {
		return(attendees);
	}
	
	//Finds the room, given a roomname
	public static Room find(String roommname)
	{
		return(allRooms.get(roommname));
	}
	
	//clear the rooms
	public static void resetRooms()
	{
		allRooms.clear();
	}

}
