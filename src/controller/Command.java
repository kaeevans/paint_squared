package controller;

/**
 * Enums to identify what kind of Protocol object will be sent.
 */
public enum Command {
	NONE, CREATE, JOIN, LEAVE, MSG, ALIAS, INVITED, ASSIGNED, TRANSCRIPT, 
	ROOMLIST, USERLIST, USERSINROOM, KILL, INVITEMANY, COLOR_FILL;
}
