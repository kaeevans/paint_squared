package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

import controller.Tuple;

/**
 * NOTE: This object is the *server* representation of each client
 *       this class is only used by the *server* application, not
 *       the GUI clients.
 */
public class ClientHandler implements Runnable{
	//The client shares the requests queue with the controller, adding new requests
	//as they are retrieved from the socket
	BlockingQueue<Protocol> requests;
	BufferedReader read;
	PrintWriter write;
	
	public String alias; //This is the alias the client is currently using
	
	/**
	 * Creates a new client which uses the protocol, alias and read/write sockets it is given
	 * @param theRequests The queue of requests
	 * @param toRead Reader for incoming requests
	 * @param toWrite Writer for outgoing requests
	 */
	public ClientHandler(BlockingQueue<Protocol> theRequests, BufferedReader toRead, PrintWriter toWrite)
	{
		requests = theRequests;
		alias = "__UNASSIGNED__"; //In practice, we should never see this alias (sign something has gone wrong!)
		read = toRead;
		write = toWrite;
	}
	
	/**
	 * The client will attempt to write the protocol message back toward the GUI Client application
	 * This is used for updating the client information when changes occur
	 * @param p The outgoing protocol to write out
	 */
	public void send(Protocol p)
	{
		write.print("["+p.command+"]");
		switch(p.command)
		{
		case USERLIST:
			for(String s : p.userList)
				write.print("["+s+"]");
			break;
		case ROOMLIST:
			for(Tuple t : p.roomList)
				write.print("["+t.name+"`"+t.number+"]");
			break;
		case MSG: case TRANSCRIPT: case INVITEMANY:
			write.print("["+p.nameField+"]");
			for(String s : p.messageField){
				write.print("["+s+"]");
			}
			break;
		case USERSINROOM:
		    write.print("["+p.textField+"]");
			for(String s: p.userList)
				write.print("["+s+"]");
			break;
		case COLOR_FILL:
			write.print("["+p.nameField+"]");
			write.print("["+p.colorField+"]");
			write.print("["+p.gridX+"]");
			write.print("["+p.gridY+"]");
			break;
			
		default:
			write.print("["+p.nameField+"]");
		}
		write.print("\n");
		write.flush();
	}

	/**
	 * Runs the socket for each client attached to the server, retrieving new requests
	 */
	@Override
	public void run() {
		try{
			String line = read.readLine();
			while(line != null) {
				line = line.trim();
				if(line.length()>0)
				{
					Protocol command = new Protocol(line);
					command.source = this;
					queueCommand(command);
					line = read.readLine();
				}
			}
			read.close();
			write.close();
			//Tell the controller to kill this client in the model
			Protocol command = new Protocol(Command.KILL, alias);
			queueCommand(command);
			
		} catch(IOException e) {
			System.err.println("Client, "+alias+" experienced a reading error: "+e.getMessage());
		}
		
	}
	
	/**
	 * Queues a new protocol into the requests, also catches errors and does
	 * basic assertions on the integrity of the protocol
	 * @param command The command to queue
	 */
	public void queueCommand(Protocol command)
	{
		if(command.command != Command.NONE) {
			try {
			    command.source = this;
				requests.put(command);
			} catch(InterruptedException e) {
				System.err.println("Unable to add request to queue. Request discarded.");
			}
		}		
	}
	
	
}
