package view;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import controller.Command;
import controller.Protocol;
import controller.Tuple;

/*
 * The ChatWindowGUI class makes the GUI to chat with people on. Has a tabbed 
 * so more conversations can be added easily. 
 */
@SuppressWarnings("serial")
public class ChatWindow extends JFrame {
    private final JTabbedPane messagePane;
    private PrintWriter writer;

    /*
     * ChatWindowGUI constructor
     */
    public ChatWindow(PrintWriter writer){
        this.writer = writer;
        //set size of JFrame window
        setSize(800, 600);
        setTitle("Chat Window");
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                // Remove user from all conversations
                exitAllConversations();
                ChatWindow.this.setVisible(false);
                ChatWindow.this.dispose();
            }
        });
        
        messagePane = new JTabbedPane();
        messagePane.setPreferredSize(new Dimension(770, 550));
        
        add(messagePane);
        pack();
    }
    
    /*
     * method to get the number of tabs
     */
    public int getNumTabs() {
        synchronized(messagePane) {
            return messagePane.getTabCount();
        }
    }
    
    // ----- ACTIONS ----- //
    /**
     * Removes user from all conversations the user is in. Removes all tabs on 
     * the message pane so messagePane.getTabCount() should = 0.
     */
    private void exitAllConversations() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                synchronized(messagePane) {
                    int numTabs = messagePane.getTabCount();
                    // Leave chatrooms
                    for (int i = 0; i < numTabs; i++) {
                        String roomname = messagePane.getTitleAt(i);
                        send(new Protocol(Command.LEAVE, roomname));
                    }
                    // Remove chat tabs in window
                    messagePane.removeAll();
                }
            }
        });
    }
    
    /**
     * Adds a new conversation (ConversationPanel) to the chat window as a tab.
     * @param chatroom The chatroom's name
     * @param newConvo The new conversation (a JPanel) to add as a tab
     */
    public void addConversation(String chatroom, ConversationPanel newConvo) {
        messagePane.addTab(chatroom, newConvo);
        messagePane.setSelectedIndex(messagePane.indexOfTab(chatroom));
        toFront();
    }
    
    /*
     * removes a specific chat room tab from the conversation window
     */
    public void removeConversation(String roomname) {
        messagePane.removeTabAt(messagePane.indexOfTab(roomname));
    }
    
    // ----- SERVER AND CLIENT COMMUNICATION ----- //
    /**
     * Sends a protocol message to the server
     */
    public void send(Protocol p)
    {
        writer.print("["+p.command+"]");
        switch(p.command) {
            case USERLIST:
                for(String s : p.userList)
                    writer.print("["+s+"]");
                break;
            case ROOMLIST:
                for(Tuple t : p.roomList)
                    writer.print("["+t.name+"`"+t.number+"]");
                break;
            case MSG: case TRANSCRIPT: 
                writer.print("["+p.nameField+"]["+p.textField+"]");
                break;
            case INVITEMANY:
        		writer.print("["+p.nameField+"]");
    			for(String s : p.messageField){
    				writer.print("["+s+"]");
    			}
    			break;
            default:
                writer.print("["+p.nameField+"]");
                break;
        }
        writer.print("\n");
        writer.flush();
    }
    
    /**
     * Processes a message from the server to update
     * @param update
     */
    public void update(final Protocol update)
    {
        switch(update.command) {
            case USERSINROOM:
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        String room = update.textField;
                        synchronized(messagePane) {
                            int tabIndex = messagePane.indexOfTab(room);
                            if (tabIndex != -1) {
                                ConversationPanel convo = (ConversationPanel) messagePane.getComponentAt(tabIndex);
                                convo.update(update);
                            }
                        }
                    }
                });
                break;
            case TRANSCRIPT:
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        String room = update.nameField;
                        synchronized(messagePane) {
                            int tabIndex = messagePane.indexOfTab(room);
                            if (tabIndex != -1) {
                                ConversationPanel convo = (ConversationPanel) messagePane.getComponentAt(tabIndex);
                                convo.update(update);
                            }
                        }
                    }
                });
                break;
                
            case USERLIST:
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < messagePane.getTabCount(); i++) {
                            ConversationPanel convo = (ConversationPanel) messagePane.getComponentAt(i);
                            convo.update(update);
                        }
                    }
                });
                break;
                
            default:
                System.err.println("Warning, client cannot parse command, "+update.command);
                break;
            }
    }
    /*
     * method to check if chat room tab with specified name already exists
     */
    public boolean containsConvo(String roomName){
        return messagePane.indexOfTab(roomName)>-1;
    }
}