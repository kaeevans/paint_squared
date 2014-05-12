package model;

import java.util.Date;
/*
 * Message class used to store messages
 */
public class Message {

	private String alias;
	private String text;
	private final String timestamp = new Date().toString().split(" ")[3];
	
	/*
	 * Constructor
	 */
	public Message(String a, String t) {
		alias = a;
		text = t;
	}
	
	@Override
	/*
	 * toString method
	 */
	public String toString()
	{
		return(alias+" ("+timestamp+") : "+text);
	}
	
	/*
	 * getter to get the alias
	 */
	public String getAlias(){
		return alias;
	}
	
	public String getTimeStamp(){
		return timestamp;
	}
	
	/*
	 * getter to get the text
	 */
	public String getText() {
		return text;
	}
		
	/**
	 * Replaces any '[' or ']' with '(', ')' to ensure no protocol mishaps
	 * @param text
	 * @return
	 */
	public static String makeSafe(String text)
	{
		text = text.replaceAll("\\[", "(");
		text = text.replaceAll("\\]", ")");
		text = text.replaceAll("`", "'");
		return(text);
	}
}
