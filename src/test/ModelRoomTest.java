package test;

import static org.junit.Assert.*;

import java.util.List;

import model.Model;
import model.Room;
import model.User;

import org.junit.Test;

import controller.Tuple;

public class ModelRoomTest {
    /** 
     * Test Suite for the Model with actions relating to rooms
     * 1) Create new chatroom
     * 2) Join chatroom
     * 3) Leave chatroom
     * 4) Send messages in chatroom
     * 5) Get message history of chatroom
     * 6) Get list of all chatrooms, active and inactive
     * 7) Makes messages with square brackets [] protocol-safe (changes to parens)
     * 8) Checks that list of users in room is correct for a room
     */
    
	@Test
	// 1) Create new chatroom
	public void newRoomTest() {
		Model theModel = new Model();
		theModel.newRoom("room");
		assertNotNull(Room.find("room"));
		assertNull(Room.find("not_here"));
		assertEquals(theModel.newRoom("room"), "room_2");
		assertNotNull(Room.find("room_2"));
	}
	
	@Test
	// 2) Join chatroom
	public void joinRoomTest() {
		Model theModel = new Model();
		theModel.newUser();
		theModel.newRoom("room");
		assertTrue(theModel.joinRoom("Anonymous", "room"));
		assertFalse(theModel.joinRoom("non-user", "room"));
		assertFalse(theModel.joinRoom("Anonymous", "non-room"));
		theModel.killUser("Anonymous");
		assertFalse(theModel.joinRoom("Anonymous", "room"));
	}
	
	@Test
	// 3) Leave chatroom
	public void leaveRoomTest() {
		Model theModel = new Model();
		theModel.newUser();
		theModel.newRoom("room");
		theModel.joinRoom("Anonymous", "room");
		assertFalse(theModel.leaveRoom("non-user", "room"));
		assertFalse(theModel.leaveRoom("Anonymous", "non-room"));
		assertTrue(theModel.leaveRoom("Anonymous", "room"));
		assertFalse(theModel.leaveRoom("Anonymous", "room"));
		theModel.joinRoom("Anonymous", "room");
		theModel.killUser("Anonymous");
		assertFalse(theModel.leaveRoom("Anonymous", "room"));
	}
	
	@Test
	// 4) Send messages in chatroom
	public void sendMessagesTest(){
		Model theModel = new Model();
		theModel.newUser();
		theModel.newRoom("room");
		assertFalse(theModel.writeMessage("Anonymous", "room", ""));
		theModel.joinRoom("Anonymous", "room");
		assertTrue(theModel.writeMessage("Anonymous", "room", ""));
		assertFalse(theModel.writeMessage("non-user", "room", ""));
		assertFalse(theModel.writeMessage("Anonymous", "non-room", ""));
		theModel.leaveRoom("Anonymous", "room");
		assertFalse(theModel.writeMessage("Anonymous", "room", ""));
		theModel.newUser();
		theModel.newRoom("room2");
		theModel.joinRoom("Anonymous_2", "room2");
		theModel.joinRoom("Anonymous", "room");
		assertFalse(theModel.writeMessage("Anonymous", "room2", ""));
		assertFalse(theModel.writeMessage("Anonymous_2", "room", ""));
	}
	
	@Test
	// 5) Get message history of chatroom
	public void getTranscriptTest(){
		Model theModel = new Model();
		theModel.newUser();
		theModel.newRoom("room");
		theModel.joinRoom("Anonymous", "room");
		theModel.writeMessage("Anonymous", "room", "Hello.");
		theModel.writeMessage("Anonymous", "room", "Bye.");
		String[] transcript = {"Anonymous : Hello.","Anonymous : Bye."};
		//Test Ignores the Timestamps for simplicity of comparisons
		for(int ii=0; ii<theModel.getTranscript("room").size(); ii++){
			String[] splits = theModel.getTranscript("room").get(ii).split(" ");
			String s1 = splits[0]+" "+splits[2]+" "+splits[3];
			String s2 = transcript[transcript.length-1-ii];
			assertEquals(s1,s2);
		}
		
	}
	
	@Test
	// 6) Get list of all chatrooms, active and inactive
	public void getRoomsTest(){
		Model theModel = new Model();
		for(int i=0; i<100; i++)
			theModel.newRoom("room");
		List<Tuple> theRooms = theModel.getRooms();
		for(Tuple t : theRooms)
		{
			assertEquals(t.name.substring(0, 4), "room");
			assertEquals(t.number, 0);
		}
	}
	
	@Test
	// 7) Makes messages with square brackets [] protocol-safe (changes to parens)
	public void makeSafeMessageTest(){
		Model theModel = new Model();
		theModel.newRoom("[room]");
		assertNotNull(Room.find("(room)"));
		assertNull(Room.find("[room]"));
		theModel.newUser();
		theModel.changeAlias("Anonymous", "[user][][]");
		assertNull(User.find("[user][][]"));
		assertNotNull(User.find("(user)()()"));
		theModel.joinRoom("(user)()()", "(room)");
		theModel.writeMessage("(user)()()", "(room)", "[transcript[[[[");
		
		String[] message = theModel.getTranscript("(room)").get(0).split(" ");
		
		assertEquals(message[0]+" "+message[2]+" "+message[3], "(user)()() : (transcript((((");
	}
	
	@Test
	// 8) Checks that list of users in room is correct for a room
	public void getAttendeesTest(){
		Model theModel = new Model();
		theModel.newRoom("room");
		theModel.newRoom("room");
		theModel.newUser();
		theModel.newUser();
		theModel.newUser();
		theModel.newUser();
		theModel.newUser();
		theModel.joinRoom("Anonymous", "room");
		theModel.joinRoom("Anonymous_2", "room");
		theModel.joinRoom("Anonymous_3", "room");
		theModel.joinRoom("Anonymous_4", "room_2");
		theModel.joinRoom("Anonymous_5", "room_2");
		List<String> attendees = theModel.getAttendees("room");
		assertTrue(attendees.size()==3);
	}

}
