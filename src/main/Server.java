package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import controller.Controller;
import controller.ClientHandler;

/**
 * Chat server runner.
 */
public class Server {

	static Controller theController;
	static ServerSocket serverSocket;
	public final static int PORT = 4444; 
	
    /**
     * Start a chat server.
     */
    public static void main(String[] args) {
		
    	//First, initialize the server
    	serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("Could not listen on port: "+Integer.toString(PORT)+".");
			System.exit(1);
		}
		
		//Now initialize the model and controller
		theController = new Controller();
		Thread controlThread = new Thread(theController);
		controlThread.start();
		
		try {
			
			while (true) {
				
				// Wait until someone connects.
				System.out.println("Accepting connections...");
				Socket socket = serverSocket.accept();

				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				
				// Initialize, add to controller and run the client handler
		        ClientHandler client = new ClientHandler(theController.getRequestQueue(), reader, writer);
		        theController.addClient(client);
		        Thread thread = new Thread(client);
		        thread.start();
			}
			
		} catch (IOException e) {
			System.err.println("Accept failed.");
			System.exit(1);
		}		
		   	
    }
}
