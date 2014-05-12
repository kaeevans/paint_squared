package main;

import controller.Command;
import controller.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import view.HomeWindow;
import view.LoginWindow;


/**
 * GUI chat client runner.
 */
public class Client{
    // Add in home view and chatwindow
    private String HOSTNAME, PORTNUMBER;
    PrintWriter writer;
    BufferedReader reader;
    Socket clientSocket;
    
    private LoginWindow loginWindow;
    private HomeWindow homeWindow;
    
    public static void main(String[] args) {
    	 Client GUIClient = new Client();
    	 GUIClient.run();
    }

	public void run() {
		try {
		    // Create instance of GUI client
            loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
            
            // Wait for a non-null IP address and port number before connecting
            boolean readyToConnect = (loginWindow.getIPAddress() != null)
                    && (loginWindow.getPortNumber() != null);
            
            while (!readyToConnect) {
                readyToConnect = (loginWindow.getIPAddress() != null)
                        && (loginWindow.getPortNumber() != null);
            }
            
            HOSTNAME = loginWindow.getIPAddress();
            PORTNUMBER = loginWindow.getPortNumber();
            
            // Connect to socket
            clientSocket = new Socket(HOSTNAME, Integer.parseInt(PORTNUMBER));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            // Close Login window and open Home window
            loginWindow.dispose();
            homeWindow = new HomeWindow("", writer);
            homeWindow.setVisible(true);
            
            // Tell Server to connect this new user
            send(new Protocol(Command.ALIAS, loginWindow.getUsername()));
            
    		// Run the client communication (listen for server updates)
    		try{
    			String line = reader.readLine();
    			while(line != null)
    			{
    				line = line.trim();
    				if(line.length() > 0 && loginWindow != null && homeWindow != null)
    				{
    				    Protocol command = new Protocol(line);
    					homeWindow.update(command);
    					line = reader.readLine();
    				}
    			}
    			reader.close();
    			writer.close();
    			System.out.println("Closing client...");
    			
    		} catch(IOException e) {
    			System.err.println("Error while trying to listen to server"+e.getMessage());
    		}    		
        } catch (UnknownHostException e) {
            System.err.println("Couldn't locate the host "+HOSTNAME+".");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Sorry, server is currently offline!");
            System.exit(1);
        }
	}
	
	/**
     * Sends a protocol message to the server
     */
    private void send(Protocol p)
    {
        writer.print("["+p.command+"]");
        switch(p.command) {
            default:
                writer.print("["+p.nameField+"]");
        }
        
        writer.print("\n");
        writer.flush();
    }
}
