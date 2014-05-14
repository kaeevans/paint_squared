package test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import controller.Command;
import controller.Protocol;
import controller.Tuple;

/*
 * Testing creation of Protocol messages
 * Client - Server Protocol Tests for each type of Token
 */


public class ProtocolTest {
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@Before
	public void setUpStreams() {
	    System.setErr(new PrintStream(errContent));
	}
	
    @Test
    //testing CREATE token
    public void create_test_protocol(){
        Protocol p = new Protocol("[CREATE][roommake]");
        assertEquals(p.command, Command.CREATE);
        assertEquals(p.nameField, "roommake");
    }

    @Test
    //testing NONE token
    public void none_test_protocol(){
        Protocol p = new Protocol("[NONE]");
        assertEquals(p.command, Command.NONE);
    }

    @Test
    //testing JOIN token
    public void join_test_protocol(){
        Protocol p = new Protocol("[JOIN][roomjoin]");
        assertEquals(p.command, Command.JOIN);
        assertEquals(p.nameField, "roomjoin");
    }

    @Test
    //testing LEAVE token
    public void leave_test_protocol(){
        Protocol p = new Protocol("[LEAVE][roomleft]");
        assertEquals(p.command, Command.LEAVE);
        assertEquals(p.nameField, "roomleft");
    }

    @Test
    //testing MSG token
    public void msg_test_protocol(){
        Protocol p = new Protocol("[MSG][roomname][User: Here's an example of a possible message]");
        assertEquals(p.command, Command.MSG);
        assertEquals(p.textField, "User: Here's an example of a possible message");
    }

    @Test
    //testing ALIAS token
    public void alias_test_protocol(){
        Protocol p = new Protocol("[ALIAS][newAlias]");
        assertEquals(p.command, Command.ALIAS);
        assertEquals(p.nameField, "newAlias");
    }

    @Test
    //testing INVITED token
    public void invited_test_protocol(){
        Protocol p = new Protocol("[INVITED][roomInvite]");
        assertEquals(p.command, Command.INVITED);
        assertEquals(p.nameField, "roomInvite");
    }

    @Test
    //testing ASSIGNED token
    public void assigned_test_protocol(){
        Protocol p = new Protocol("[ASSIGNED][nameAssigned]");
        assertEquals(p.command, Command.ASSIGNED);
        assertEquals(p.nameField, "nameAssigned");
    }

    @Test
    //testing TRANSCRIPT token
    public void transcript_test_protocol(){
        Protocol p = new Protocol("[TRANSCRIPT][[roomname][User: example]");
        assertEquals(p.command, Command.TRANSCRIPT);
        assertEquals(p.nameField, "roomname");
        ArrayList<String> mylist = new ArrayList<String>();
        mylist.add("User: example");
        assertEquals(p.messageField, mylist);
    }

    @Test
    //testing ROOMLIST token
    public void roomlist_test_protocol(){
        Protocol p = new Protocol("[ROOMLIST][room1`16]");
        ArrayList<Tuple> mylist = new ArrayList<Tuple>();
        mylist.add(new Tuple("room1`16"));
        assertEquals(p.command, Command.ROOMLIST);
        assertEquals(p.roomList,mylist);
    }

    @Test
    //testing USERLIST token
    public void userlist_test_protocol(){
        Protocol p = new Protocol("[USERLIST][[user1][user2]");
        assertEquals(p.command, Command.USERLIST);
        ArrayList<String> mylist = new ArrayList<String>();
        mylist.add("user1");
        mylist.add("user2");
        assertEquals(p.userList, mylist);
    }

    	@Test
        //testing USERSINROOM token
        public void usersinroom_test_protocol(){
            Protocol p = new Protocol("[USERSINROOM][[roomname][User1]");
            assertEquals(p.command, Command.USERSINROOM);
            assertEquals(p.textField, "roomname");
            ArrayList<String> mylist = new ArrayList<String>();
            mylist.add("User1");
            assertEquals(p.userList, mylist);
        }

    @Test
    //testing KILL token
    public void kill_test_protocol(){
        Protocol p = new Protocol("[KILL][name]");
        assertEquals(p.command, Command.KILL);
        assertEquals(p.nameField, "name");
    }

    @Test
    //testing INVITEMANY token
    public void invitemany_test_protocol(){
        Protocol p = new Protocol("[INVITEMANY][roomname][user1][user2]");
        assertEquals(p.command, Command.INVITEMANY);
        ArrayList<String> mylist = new ArrayList<String>();
        mylist.add("user1");
        mylist.add("user2");
        assertEquals(p.messageField, mylist);
    }
    
    @Test
    // Test color_fill protocol
    public void color_fill_test_protocol(){
    	Protocol p = new Protocol("[COLOR_FILL][roomname][color][gridX][gridY]");
    	assertEquals(p.command, Command.COLOR_FILL);
    	assertEquals(p.nameField, "roomname");
    	assertEquals(p.colorField, "color");
    	assertEquals(p.gridX, "gridX");
    	assertEquals(p.gridY, "gridY");
    }
    
    @Test
    //Test some corrupted protocols
    public void corrupt_protocol(){
        Protocol p = new Protocol("[LAZERGUN][huh?][wellthisisweird]");
        assertEquals(p.command, Command.NONE);
        
        //command should correctly be set to none. All other commands will write to System.err
        //TODO: perhaps automate System.err checking using ByteArrayOutputStream or similar
    }
}
