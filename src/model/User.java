package model;

import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

public class User {
	
	private String alias;
	private static Map<String, User> allUsers = new LinkedHashMap<String, User>();
	//This is a list of error codes which are also off-limits as alias choices
	private static List<String> errorCodes = new ArrayList<String>(Arrays.asList("ERROR 1: UNKNOWN USER",
																				 "ERROR 2: USER WAS KILLED",
																				 "__UNASSIGNED__"));
	
	/**
	 * Creates a new user, and gives it the default name Anonymous with a
	 * suffix to guarantee uniqueness
	 */
	public User()
	{
		int n = 1;
		String defaultName = "Anonymous"; 
		String name = defaultName;
		while(allUsers.get(name) != null || errorCodes.contains(name))
		{
			n++;
			name = defaultName + "_" + Integer.toString(n); 
		}
		alias = name;
		allUsers.put(name, this);
	}
	
	/** @return Return the alias of this User */
	public String getAlias() {
		return alias;
	}
	
	/**
	 * Requests the specified alias change. If this alias is taken,
	 * this will automatically add a suffix to guarantee uniqueness
	 */
	public String requestAlias(String newAlias)
	{
		allUsers.remove(alias);
		int n = 1;
		String name = newAlias;
		while(allUsers.get(name) != null) {
			n++;
			name = newAlias + "_" + Integer.toString(n); 
		}
		alias = name;
		allUsers.put(name, this);
		return(alias);
	}
	
	/**
	 * Deletes a user from the username --> User database
	 * @param name The username to delete
	 * @return Whether the delete was successful
	 */
	public static boolean delete(String name)
	{
		if(allUsers.containsKey(name)) {
			allUsers.remove(name);
			return true;
		}
		return false;
	}
	
	/**
	 * Find a specified User by its username
	 * @param name The username to search by
	 * @returns The associated User with username
	 */
	public static User find(String name) {
		return(allUsers.get(name));
	}
	
	/** Clears our map of username --> User objects */
	public static void resetUsers() {
		allUsers.clear();
	}
	

}
