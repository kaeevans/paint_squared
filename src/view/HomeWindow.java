package view;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import view.components.JTextFieldLimit;

import controller.Command;
import controller.Protocol;
import controller.Tuple;

/**
 * The JPanel that holds the home pane. Will be added to MainGUI.
 */
@SuppressWarnings("serial")
public class HomeWindow extends JFrame {
    private final Dimension WIN_SIZE = new Dimension(350, 600);
    private String username;
    private JScrollPane userListPane, roomsListPane, historyListPane;
    private JTextField newChatField;
    private JLabel userListLabel, chatroomsLabel, convoHistoryLabel, 
                    usernameLabel, welcomeLabel, hoverInstructionMsg;
    private JButton createConvoButton, joinConvoButton, logoutButton, inactiveHistoryButton, activeHistoryButton;
    private JList userList, roomList, pastConvosList;
    private DefaultListModel userListModel, roomListModel, pastConvosListModel;
    private HistoryWindow historyWindow;
    private JTextPane invalidChatName;

    private PrintWriter writer;
    private ChatWindow chatWindow;
    private ArrayList<ConversationPanel> chatTabs;
    
    public HomeWindow(String username, PrintWriter writer) {
        this.username = username;  
        this.writer = writer;
        this.chatTabs = new ArrayList<ConversationPanel>();
        
        chatWindow = new ChatWindow(this.writer); // Instantiate Chat window
        historyWindow = new HistoryWindow();
        
        setSize(WIN_SIZE);
        setTitle("ChatHub");
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // Add Window Listener to allow logout on window close
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                // Confirm logout
                int logout = JOptionPane.showConfirmDialog(
                        HomeWindow.this, 
                        "Logout now?", 
                        "Logging out...",
                        JOptionPane.YES_NO_OPTION);
                // Logout if yes!
                if (logout == JOptionPane.YES_OPTION) {
                    logout();
                    HomeWindow.this.setVisible(false);
                    HomeWindow.this.dispose();
                }
            }
        });
        
        Container cont = getContentPane();
        cont.setLayout(new GridBagLayout()); // Use GridBag layout to center everything
        JPanel container = createUI();
        
        add(container); // Add the JPanel
        pack();
    }
    
    private JPanel createUI() {
        JPanel container = new JPanel();
        container.setMinimumSize(WIN_SIZE);
        
        pastConvosListModel = new DefaultListModel();
        pastConvosList = new JList(pastConvosListModel);
        pastConvosList.setToolTipText("Empty chatrooms.");
        pastConvosList.setLayoutOrientation(JList.VERTICAL_WRAP);
        pastConvosList.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            { if (e.getClickCount() == 2) showMessageHistory(false); }
        });
        
        userListModel = new DefaultListModel();
        userList = new JList(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setToolTipText("Users currently logged in.");
        userList.setLayoutOrientation(JList.VERTICAL_WRAP);
        
        roomListModel = new DefaultListModel();
        roomList = new JList(roomListModel);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setToolTipText("Active chatrooms, with >0 users.");
        roomList.setLayoutOrientation(JList.VERTICAL_WRAP);
        roomList.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e)
            { if (e.getClickCount() == 2) joinConversation(); }
        });
        
        userListPane = new JScrollPane(userList);
        userListPane.setPreferredSize(new Dimension(300, 75));
        roomsListPane = new JScrollPane(roomList);
        roomsListPane.setPreferredSize(new Dimension(300, 75));
        historyListPane = new JScrollPane(pastConvosList);
        historyListPane.setPreferredSize(new Dimension(300, 75));
        
        hoverInstructionMsg = new JLabel("<html>Hover over components for<br>" +
        		"instructions on ChatHub!</html>");
        welcomeLabel = new JLabel("You are logged in as:");
        welcomeLabel.setFont(new Font("Open Sans", Font.BOLD, 12));
        welcomeLabel.setForeground(new Color(255,112,112));
        usernameLabel = new JLabel(this.username);
        usernameLabel.setFont(new Font("Open Sans", Font.BOLD, 14));
        usernameLabel.setForeground(new Color(43,135,255));
        
        userListLabel = new JLabel("Users Online");
        userListLabel.setFont(new Font("Open Sans", Font.BOLD, 12));
        convoHistoryLabel = new JLabel("Inactive (Empty) Chatrooms");
        convoHistoryLabel.setFont(new Font("Open Sans", Font.BOLD, 12));
        chatroomsLabel = new JLabel("Active Chatrooms");
        chatroomsLabel.setFont(new Font("Open Sans", Font.BOLD, 12));
        
        invalidChatName = new JTextPane();
        invalidChatName.setContentType("text/html");
        invalidChatName.setText("<html><body style=\"font-family:Open Sans;font-size:9px;color:#CC3399\">" +
                "<b>Invalid chat room name:</b><br>" +
                "Must include only " +
                "letters and digits" +
                "</body></html>");
        invalidChatName.setVisible(false);
        invalidChatName.setOpaque(false);
        invalidChatName.setPreferredSize(new Dimension(150, 30));
        
        logoutButton = new JButton(" Logout", new ImageIcon("src/view/resources/sign-out.png"));
        logoutButton.setFocusPainted(false);
        logoutButton.setToolTipText("Click to logout!");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                logout();                
            }
        });
        
        inactiveHistoryButton = new JButton(" History", new ImageIcon("src/view/resources/view-history.png"));
        inactiveHistoryButton.setToolTipText("<html>Select an Inactive Chatroom and <br>" +
        		"click to view the conversation history.</html>");
        inactiveHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) 
            { showMessageHistory(false); }
        });
        activeHistoryButton = new JButton(" History", new ImageIcon("src/view/resources/view-history.png"));
        activeHistoryButton.setToolTipText("<html>Select an Active Chatroom and <br>" +
        		"click to view the conversation history.</html>");
        activeHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) 
            { showMessageHistory(true); }
        });
        
        createConvoButton = new JButton (" Create Chat", new ImageIcon("src/view/resources/new-chat.png"));
        createConvoButton.setToolTipText("<html>- Enter chatroom name. <br>" +
        		"- Alphanumeric characters only, 20 character limit. <br>" +
        		"- If the name is taken, an \"_\" and number will be appended.</html>");
        createConvoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) 
            { createNewChatroom(); }
        });
        
        joinConvoButton = new JButton(" Join", new ImageIcon("src/view/resources/join-convo.png"));
        joinConvoButton.setToolTipText("<html>Select an Active Chatroom and <br>click this to join the conversation.</html>");
        joinConvoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) 
            { joinConversation(); }
        });
        newChatField = new JTextField();
        newChatField.setDocument(new JTextFieldLimit(20));
        newChatField.setToolTipText("<html>- Enter chatroom name. <br>" +
                "- Alphanumeric characters only, 20 character limit. <br>" +
                "- If the name is taken, an \"_\" and number will be appended.</html>");
        
        GroupLayout layout = new GroupLayout(container);
        container.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        JSeparator sep1 = new JSeparator();
        JSeparator sep2 = new JSeparator();
        JSeparator sep3 = new JSeparator();
        
        // Horizontal layout     
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(welcomeLabel)
                                .addComponent(usernameLabel)
                                .addComponent(hoverInstructionMsg)
                                .addComponent(newChatField))
                        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                                .addComponent(logoutButton)
                                .addComponent(createConvoButton)))
                
                .addComponent(invalidChatName)
                .addComponent(sep3)
                .addComponent(userListLabel)
                .addComponent(userListPane)
                .addComponent(sep1)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(chatroomsLabel)
                        .addGap(35)
                        .addComponent(joinConvoButton)
                        .addComponent(activeHistoryButton))
                .addComponent(roomsListPane)
                .addComponent(sep2)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(convoHistoryLabel)
                        .addGap(55)
                        .addComponent(inactiveHistoryButton))
                .addComponent(historyListPane)
        );
        
        // Vertical layout
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(Alignment.CENTER)
                    .addGroup(layout.createSequentialGroup()
                            .addComponent(welcomeLabel)
                            .addComponent(usernameLabel))
                    .addComponent(logoutButton, Alignment.TRAILING))
            .addComponent(hoverInstructionMsg)
            .addGap(20)
            .addGroup(layout.createParallelGroup(Alignment.CENTER)
                    .addComponent(newChatField)
                    .addComponent(createConvoButton, Alignment.TRAILING))
            .addComponent(invalidChatName)
            .addGap(20)
            .addComponent(sep3)
            .addGap(20)
            .addComponent(userListLabel)
            .addComponent(userListPane)
            .addGap(20)
            .addComponent(sep1)
            .addGap(20)
            .addGroup(layout.createParallelGroup(Alignment.CENTER)
                    .addComponent(chatroomsLabel)
                    .addComponent(joinConvoButton, Alignment.TRAILING)
                    .addComponent(activeHistoryButton, Alignment.TRAILING))
            .addComponent(roomsListPane)
            .addGap(20)
            .addComponent(sep2)
            .addGap(20)
            .addGroup(layout.createParallelGroup(Alignment.CENTER)
                    .addComponent(convoHistoryLabel)
                    .addComponent(inactiveHistoryButton, Alignment.TRAILING))
            .addComponent(historyListPane)
            .addGap(20)
        );
        
        return container;
    }
    
    /**
     * ACTIONS
     */
    private void createNewChatroom() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String desiredRoomname = newChatField.getText().trim();
                if (desiredRoomname.matches("[A-Za-z0-9]+")) {
                    send(new Protocol(Command.CREATE, desiredRoomname));
                    invalidChatName.setVisible(false);
                }                
                else
                    invalidChatName.setVisible(true);
                newChatField.setText("");
            }
        });
    }
    /*
     * method to join a conversation
     */
    private void joinConversation() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String roomToJoin = (String) roomList.getSelectedValue();
                if (roomToJoin != null) {
                    send(new Protocol(Command.JOIN, roomToJoin));
                    //send(new Protocol(Command.JOIN, rawTag.roomname));
                }
            }
        });
    }
    
    /*
     * method to show message history
     */
    private void showMessageHistory(final boolean isActive) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String room;
                if (isActive) room = (String) roomList.getSelectedValue();
                else room = (String) pastConvosList.getSelectedValue();
                
                if (room != null) {
                    HistoryPanel convo = new HistoryPanel(room, historyWindow);
                    if (historyWindow.getNumTabs() == 0) {
                        historyWindow.addConversation(room, convo);
                        historyWindow.setVisible(true);
                    } else {
                        historyWindow.addConversation(room, convo);
                    }
                    send(new Protocol(Command.TRANSCRIPT, room));
                }
            }
        });
    }
    
    /*
     * method to log out
     */
    private void logout() {
        send(new Protocol(Command.KILL, username));
        System.exit(0);
    }
    
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
            //We've received confirmation that we've joined a chatroom
            case INVITED:
                SwingUtilities.invokeLater(new Runnable() {
                    public void run () {
                        ConversationPanel convo = new ConversationPanel(update.nameField, chatWindow, HomeWindow.this.writer); 
                        chatTabs.add(convo);
                        if (!chatWindow.containsConvo(update.nameField)){
                            if (chatWindow.getNumTabs() == 0) {
                                chatWindow.addConversation(update.nameField, convo);
                                chatWindow.setVisible(true);
                            } else {
                                chatWindow.addConversation(update.nameField, convo);
                            }
                        }   
                        
                        //Now query the server for the transcript since if we were delayed in setup, the
                        //transcript may have already been sent and discarded
                        writer.println("[TRANSCRIPT]["+update.nameField+"][none]");
                        writer.println("[ROOMLIST][none`0]");
                        writer.flush();                 
                    }
                });
                break;
            
            //We've been assigned the alias, so update our alias and all labels which display it
            case ASSIGNED:
                SwingUtilities.invokeLater(new Runnable() {
                    public void run(){
                        username = update.nameField;
                        usernameLabel.setText(username);
                    }
                });
                break;
        
            
            //We've been given an updated transcript, clear out the old transcript and replace it with the new one
            case TRANSCRIPT:
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.update(update);
                        historyWindow.update(update);
                    }
                });
                break;
            
            //We've been given an updated roomlist, change the current roomlist and update the display panel
            case ROOMLIST:
                SwingUtilities.invokeLater(new Runnable() {
                    public void run(){
                        roomListModel.clear();
                        pastConvosListModel.clear();
                        for(Tuple t: update.roomList){
                        	if(t.number>0)
                        		roomListModel.addElement(t.name);
                        	else
                        		pastConvosListModel.addElement(t.name);
                        }
                    }
                });
                break;
               
            //We've been given an updated userlist, change the current userlist and update the display panel
            case USERLIST:
                SwingUtilities.invokeLater(new Runnable() {
                    public void run(){
                        userListModel.clear();
                        for(String s: update.userList)
                        	userListModel.addElement(s);
                        chatWindow.update(update);
                    }
                });
                break;
                
            case USERSINROOM:
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.update(update);
                    }
                });
                break;
                
            case COLOR_FILL: // likely uneeded in the HomeWindow
            	//perform coloring updates
            	break;
                
            default:
                System.err.println("Warning, client cannot parse command, "+update.command);
                break;
            }
    }
    
//    /**
//     * Removes all invisible windows from chatWindows (closed)
//     */
//    public void cleanChatWindows()
//    {
//        List<ChatroomPane> toRemove = new ArrayList<ChatroomPane>();
//        for(ChatroomPane c : chatWindows)
//            if(!c.isVisible())
//                toRemove.add(c);
//        for(ChatroomPane c : toRemove)
//            chatWindows.remove(c);
//    }
//      
//    class RoomString {
//        public String roomname;
//        public String display;
//        public RoomString(Tuple t)
//        {
//            roomname = t.name;
//            display = t.name + " (" + t.number + ")";
//        }
//        @Override
//        public String toString(){
//            return display;
//        }   
}
