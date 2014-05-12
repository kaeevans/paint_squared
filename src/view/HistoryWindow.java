package view;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import controller.Protocol;

@SuppressWarnings("serial")
public class HistoryWindow extends JFrame {
    public static final Dimension PANEL_SIZE = new Dimension(550, 300);
    private final JTabbedPane historyPane;

    public HistoryWindow() {
        //set size of JFrame window
        this.setSize(600, 350);
        this.setTitle("Message History");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                historyPane.removeAll();
                HistoryWindow.this.setVisible(false);
                HistoryWindow.this.dispose();
            }
        });
        
        historyPane = new JTabbedPane();
        historyPane.setPreferredSize(PANEL_SIZE);
        
        add(historyPane);
    }
    /*
     * method to get the number of tabs
     */
    public int getNumTabs() {
        synchronized(historyPane) {
            return historyPane.getTabCount();
        }
    }
    
    /**
     * Adds a new conversation (ConversationView) to the chat window as a tab.
     * @param chatroom The chatroom's name
     * @param newConvo The new conversation (a JPanel) to add as a tab
     */
    public void addConversation(String chatroom, HistoryPanel newConvo) {
        historyPane.addTab(chatroom, newConvo);
        historyPane.setSelectedIndex(historyPane.indexOfTab(chatroom));
        toFront();
    }
    
    /*
     * method to remove a conversation
     */
    public void removeConversation(String roomname) {
        historyPane.removeTabAt(historyPane.indexOfTab(roomname));
    }
    
    /**
     * Processes a message from the server to update
     * @param update
     */
    public void update(final Protocol update)
    {
        switch(update.command) {
            case TRANSCRIPT:
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        String room = update.nameField;
                        synchronized(historyPane) {
                            int tabIndex = historyPane.indexOfTab(room);
                            if (tabIndex != -1) {
                                HistoryPanel convo = (HistoryPanel) historyPane.getComponentAt(tabIndex);
                                convo.update(update);
                            }
                        }
                    }
                });
                break;
                
            default:
                System.err.println("Warning, client cannot parse command, "+update.command);
                break;
            }
    }
}