package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import model.Model;
import model.User;

import org.junit.Test;

public class ModelUserTest {
    /** 
     * Test Suite for the Model with actions relating to users
     * 1) Create new user
     * 2) Kill user (logout from system)
     * 3) Change alias/username
     * 4) Get list of users online
     */
    
	@Test
	// 1) Create new user
	public void newUserTest() {
		Model theModel = new Model();
		theModel.newUser();
		assertNotNull(User.find("Anonymous"));
		theModel.newUser();
		assertNotNull(User.find("Anonymous_2"));
		assertEquals(theModel.newUser(), "Anonymous_3");
	}
	
	@Test
	// 2) Kill user (logout from system)
	public void killUserTest() {
		Model theModel = new Model();
		String alias = theModel.newUser();
		assertTrue(theModel.killUser(alias));
		assertFalse(theModel.getUsers().contains(alias));
	}

	@Test 
	// 3) Change alias/username
	public void changeAliasTest() {
		Model theModel = new Model();
		theModel.newUser();
		assertNotNull(User.find("Anonymous"));
		theModel.changeAlias("Anonymous", "newName");
		assertNull(User.find("Anonymous"));
		assertNotNull(User.find("newName"));
		theModel.newUser();
		theModel.changeAlias("Anonymous", "newName");
		assertNotNull(User.find("newName_2"));
	}
	
	@Test
	// 4) Get list of users online
	public void getUsersTest(){
		Model theModel = new Model();
		for(int i=0; i<100; i++)
			theModel.newUser();
		List<String> theUsers = theModel.getUsers();
		assertTrue(theUsers.contains("Anonymous"));
		for(int i=2; i<101; i++)
			assertTrue(theUsers.contains("Anonymous_"+Integer.toString(i)));
	}	
}
