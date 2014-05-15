package view;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import controller.Command;
import controller.Protocol;
import controller.Tuple;

/*
 * The ChatWindow class makes the GUI to chat with people on.
 */
@SuppressWarnings("serial")
public class ConversationPanel extends JPanel {
    private JList userList, messageHistory;
    private DefaultListModel userListModel, messageHistoryModel;
    private final JLabel usersLabel, canvasLabel;
    private final JTextField newMessageHere;
    private final JButton sendMessageButton, inviteUsersButton, leaveChatButton, clearCanvasButton;
    private final ChatWindow parent;
    private PrintWriter writer;
    private String convoName;
    private InviteUsersDialog inviteUsersDialog;
    private CanvasPanel canvas;

    public ConversationPanel(String convoName, ChatWindow owner, PrintWriter writer){
    this.parent = owner;
    this.writer = writer; 
    this.convoName = convoName;

    // leftmost vertical panel of the gui
    usersLabel = new JLabel("<html>Users in<br>Chatroom</html>");
    usersLabel.setFont(new Font("Open Sans", Font.BOLD, 12));
    usersLabel.setPreferredSize(new Dimension(70, 60));

    userListModel = new DefaultListModel();
    userList = new JList(userListModel);
    userList.setToolTipText("List of users currently in this chatroom.");
    //adding in a scroller in case there are a ton of users
    JScrollPane scrollPaneOne = new JScrollPane(userList);
    scrollPaneOne.setPreferredSize(new Dimension(70, 450));
    
    inviteUsersButton = new JButton(" Invite Users", new ImageIcon("src/view/resources/invite.png"));
    inviteUsersButton.setToolTipText("<html>Click to invite more <br>" +
        "users to this chat room!</html>");
    inviteUsersButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
        inviteUsersDialog.setVisible(true);
        }
    });
    inviteUsersButton.setPreferredSize(new Dimension(70, 40));

    inviteUsersDialog = new InviteUsersDialog(parent, 
        "Invite users to Chatroom: "+this.convoName, 
        Dialog.ModalityType.DOCUMENT_MODAL, 
        this.convoName);

    // middle vertical panel of the gui
    leaveChatButton = new JButton(" Leave Chat", new ImageIcon("src/view/resources/close.png"));
    leaveChatButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
        removeFromConversation();
        }
    });
    leaveChatButton.setPreferredSize(new Dimension(250, 60));

    messageHistoryModel = new DefaultListModel(); 
    messageHistory = new JList(messageHistoryModel);
    messageHistory.setToolTipText("<html>Here is all the message history <br>" +
        "associated with this chat room!</html>");
    //adding in a scroller in case there is a long message history
    JScrollPane scrollPaneTwo = new JScrollPane(messageHistory);
    scrollPaneTwo.setPreferredSize(new Dimension(250, 400));

    newMessageHere = new JTextField();
    newMessageHere.setPreferredSize(new Dimension(180, 90));
    newMessageHere.setToolTipText("<html>Type the message you <br>" +
        "would like to send here!</html>");
    newMessageHere.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
        if (!newMessageHere.getText().equals(""))
            sendMessage();
        }
    });
    //adding in a scroller in case someone writes a long message
    JScrollPane typedMsgPane = new JScrollPane(newMessageHere, 
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    sendMessageButton = new JButton("Send");
    sendMessageButton.setToolTipText("Click to send your message!");
    sendMessageButton.setPreferredSize(new Dimension(50, 90));
    sendMessageButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
        if (!newMessageHere.getText().equals(""))
            sendMessage();
        }
    });

    // rightmost vertical panel of the gui
    canvasLabel = new JLabel("Canvas");
    canvasLabel.setFont(new Font("Open Sans", Font.BOLD, 12));
    canvasLabel.setPreferredSize(new Dimension(450, 60));

    canvas = new CanvasPanel(this.convoName, this.parent, this.writer);
    // canvas = new JTextField();
    canvas.setPreferredSize(new Dimension(450, 450));
    canvas.setToolTipText("<html>Draw here!</html>");
    // canvas.addActionListener(new ActionListener() {
    //     @Override
    //     public void actionPerformed(ActionEvent arg0) {
    //   if (!canvas.getText().equals(""))
    //       sendMessage();
    //     }
    // });
    //paint();
    clearCanvasButton = new JButton(" Clear Canvas", new ImageIcon("src/view/resources/close.png"));
    clearCanvasButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
        }
    });
    clearCanvasButton.setPreferredSize(new Dimension(450, 40));

/////////////////////////////// gui layout /////////////////////////////
    GroupLayout layout = new GroupLayout(this);
    setLayout(layout);

    //setting automatic gap insertion to true
    layout.setAutoCreateGaps(true);      
    layout.setAutoCreateContainerGaps(true);
    
    layout.setHorizontalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addComponent(usersLabel)
            .addComponent(scrollPaneOne)
            .addComponent(inviteUsersButton, Alignment.CENTER))
        .addGroup(layout.createParallelGroup()
            .addComponent(leaveChatButton,Alignment.TRAILING)
            .addComponent(scrollPaneTwo)
            .addGroup(layout.createSequentialGroup()
                .addComponent(typedMsgPane)
                .addComponent(sendMessageButton)))
        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
            .addComponent(canvasLabel)
            .addComponent(canvas)
            .addComponent(clearCanvasButton))
    );
    
    layout.setVerticalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addComponent(usersLabel)
            .addComponent(leaveChatButton, Alignment.TRAILING)
            .addComponent(canvasLabel, Alignment.TRAILING))
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrollPaneOne)
                .addComponent(inviteUsersButton))
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrollPaneTwo)
                .addGroup(layout.createParallelGroup()
                    .addComponent(typedMsgPane)
                    .addComponent(sendMessageButton, Alignment.CENTER)))
            .addGroup(layout.createSequentialGroup()
                .addComponent(canvas)
                .addComponent(clearCanvasButton))
        )
    );
    }
    
    // ----- ACTIONS ----- //
    /*
     * method to send protocol when a user leaves a room
     */
    private void removeFromConversation() {
    send(new Protocol(Command.LEAVE, convoName));
    parent.removeConversation(convoName);
    if (parent.getNumTabs() == 0)
        parent.setVisible(false);
    }

    /*
     * method to send a send message protocol
     */
    private void sendMessage(){
    send(new Protocol(Command.MSG, convoName, newMessageHere.getText()));
    newMessageHere.setText("");
    }
    
    // ----- SERVER AND CLIENT COMMUNICATION ----- //
    /*
     * Sends a protocol message to the server
     */
    public void send(Protocol p) {
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
        case MSG: //case TRANSCRIPT:
        writer.print("["+p.nameField+"]["+p.textField+"]");
        break;
        default:
        writer.print("["+p.nameField+"]");
        break;
    }
    writer.print("\n");
    writer.flush();
    }
    /*
     * Processes a message from the server to update
     */
    public void update(final Protocol update)
	{
    switch(update.command){
    case USERSINROOM:
        SwingUtilities.invokeLater(new Runnable() {
        public void run(){
        	userListModel.clear();
            for(String s: update.userList){
            	userListModel.addElement(s);
            }
        }
        });
        break;
    //Change the transcript pane
    case TRANSCRIPT:
        SwingUtilities.invokeLater(new Runnable() {
        public void run(){
        	messageHistoryModel.clear();
            for(int ii=update.messageField.size()-1; ii>-1; ii--){
            	messageHistoryModel.addElement(update.messageField.get(ii));
            }
            int lastIndex = messageHistoryModel.getSize() - 1;
            if (lastIndex >= 0) {
            	messageHistory.ensureIndexIsVisible(lastIndex);
            }

        }
        });
        break;

    case USERLIST:
    SwingUtilities.invokeLater(new Runnable() {
        public void run(){
           inviteUsersDialog.update(update);
        }
        });
    break;
     default:
    	System.err.println("Warning, attempted to update chatroom with protocol type: "+update.command.toString());
    	break;
    }
	}
    // public void paint(){
    //     Icon iconB = new ImageIcon("blue.gif");
    //     //the blue image icon
    //     Icon iconM = new ImageIcon("magenta.gif");
    //     //magenta image icon
    //     Icon iconR = new ImageIcon("red.gif");
    //     //red image icon
    //     Icon iconBl = new ImageIcon("black.gif");
    //     //black image icon
    //     Icon iconG = new ImageIcon("green.gif");
    //     //finally the green image icon
    //     //These will be the images for our colors.
        
    //     JFrame frame = new JFrame("Paint It");
    //     //Creates a frame with a title of "Paint it"
        
    //     Container content = frame.getContentPane();
    //     //Creates a new container
    //     content.setLayout(new BorderLayout());
    //     //sets the layout
        
    //     final CanvasPanel drawPad = new CanvasPanel();
    //     //creates a new CanvasPanel, which is pretty much the paint program
        
    //     content.add(drawPad, BorderLayout.CENTER);
    //     //sets the CanvasPanel in the center
        
    //     JPanel panel = new JPanel();
    //     //creates a JPanel
    //     panel.setPreferredSize(new Dimension(32, 68));
    //     panel.setMinimumSize(new Dimension(32, 68));
    //     panel.setMaximumSize(new Dimension(32, 68));
    //     //This sets the size of the panel
        
    //     JButton clearButton = new JButton("Clear");
    //     //creates the clear button and sets the text as "Clear"
    //     clearButton.addActionListener(new ActionListener(){
    //         public void actionPerformed(ActionEvent e){
    //             drawPad.clear();
    //         }
    //     });
    //     //this is the clear button, which clears the screen.  This pretty
    //     //much attaches an action listener to the button and when the
    //     //button is pressed it calls the clear() method
        
    //     JButton redButton = new JButton(iconR);
    //     //creates the red button and sets the icon we created for red
    //     redButton.addActionListener(new ActionListener(){
    //         public void actionPerformed(ActionEvent e){
    //             drawPad.red();
    //         }

    //     });
    //     //when pressed it will call the red() method.  So on and so on =]
        
    //     JButton blackButton = new JButton(iconBl);
    //     //same thing except this is the black button
    //     blackButton.addActionListener(new ActionListener(){
    //         public void actionPerformed(ActionEvent e){
    //             drawPad.black();
    //         }
    //     });
        
    //     JButton magentaButton = new JButton(iconM);
    //     //magenta button
    //     magentaButton.addActionListener(new ActionListener(){
    //         public void actionPerformed(ActionEvent e){
    //             drawPad.magenta();
    //         }
    //     });
        
    //     JButton blueButton = new JButton(iconB);
    //     //blue button
    //     blueButton.addActionListener(new ActionListener(){
    //         public void actionPerformed(ActionEvent e){
    //             drawPad.blue();
    //         }
    //     });
        
    //     JButton greenButton = new JButton(iconG);
    //     //green button
    //     greenButton.addActionListener(new ActionListener(){
    //         public void actionPerformed(ActionEvent e){
    //             drawPad.green();
    //         }
    //     });
    //     blackButton.setPreferredSize(new Dimension(16, 16));
    //     magentaButton.setPreferredSize(new Dimension(16, 16));
    //     redButton.setPreferredSize(new Dimension(16, 16));
    //     blueButton.setPreferredSize(new Dimension(16, 16));
    //     greenButton.setPreferredSize(new Dimension(16,16));
    //     //sets the sizes of the buttons
        
    //     panel.add(greenButton);
    //     panel.add(blueButton);
    //     panel.add(magentaButton);
    //     panel.add(blackButton);
    //     panel.add(redButton);
    //     panel.add(clearButton);
    //     //adds the buttons to the panel
        
    //     content.add(panel, BorderLayout.WEST);
    //     //sets the panel to the left
        
    //     frame.setSize(300, 300);
    //     //sets the size of the frame
    //     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //     //makes it so you can close
    //     frame.setVisible(true);
    //     //makes it so you can see it
    // }

}
