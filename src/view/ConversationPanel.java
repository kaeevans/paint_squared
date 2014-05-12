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
    private JList listOfUsers, messageHistory;
    private DefaultListModel listOfUsersModel, messageHistoryModel;
    private final JLabel messageLabel, usersLabel;
    private final JTextField newMessageHere;
    private final JButton sendMessage, inviteUsers, leaveConvo;
    private final ChatWindow parent;
    private PrintWriter writer;
    private String convoName;
    private InviteUsersDialog inviteUsersDialog;
   

    public ConversationPanel(String convoName, ChatWindow owner, PrintWriter writer){
        this.parent = owner;
        this.writer = writer; 
        this.convoName = convoName;
        
        inviteUsersDialog = new InviteUsersDialog(parent, 
                "Invite users to Chatroom: "+this.convoName, 
                Dialog.ModalityType.DOCUMENT_MODAL, 
                this.convoName);

        listOfUsersModel = new DefaultListModel();
        listOfUsers = new JList(listOfUsersModel);
        listOfUsers.setToolTipText("List of users currently in this chatroom.");
        
        usersLabel = new JLabel("Users in Conversation");
        usersLabel.setFont(new Font("Open Sans", Font.BOLD, 12));

        leaveConvo = new JButton(" Leave Chat", new ImageIcon("src/view/resources/close.png"));
        leaveConvo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                removeFromConversation();
            }
        });
        
        messageLabel = new JLabel("Message History");
        messageLabel.setFont(new Font("Open Sans", Font.BOLD, 12));
        
        messageHistoryModel = new DefaultListModel(); 
        messageHistory = new JList(messageHistoryModel);
        messageHistory.setToolTipText("<html>Here is all the message history <br>" +
        		"associated with this chat room!</html>");

        newMessageHere = new JTextField();
        newMessageHere.setPreferredSize(new Dimension(200, 30));
        newMessageHere.setToolTipText("<html>Type the message you <br>" +
        		"would like to send here!</html>");
        newMessageHere.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (!newMessageHere.getText().equals(""))
                    sendMessage();
            }
        });

        sendMessage = new JButton("Send");
        sendMessage.setToolTipText("Click to send your message!");
        sendMessage.setPreferredSize(new Dimension(70, 30));
        sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (!newMessageHere.getText().equals(""))
                    sendMessage();
            }
        });
        
        inviteUsers = new JButton(" Invite Users", new ImageIcon("src/view/resources/invite.png"));
        inviteUsers.setToolTipText("<html>Click to invite more <br>" +
        		"users to this chat room!</html>");
        inviteUsers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                inviteUsersDialog.setVisible(true);
            }
        });

        //adding in a scroller in case there are a ton of users / a long message history
        JScrollPane scrollPaneOne = new JScrollPane(listOfUsers);
        scrollPaneOne.setPreferredSize(new Dimension(230, 550));
        JScrollPane scrollPaneTwo = new JScrollPane(messageHistory);
        scrollPaneTwo.setPreferredSize(new Dimension(580, 400));
        JScrollPane typedMsgPane = new JScrollPane(newMessageHere, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        //setting automatic gap insertion to true
        layout.setAutoCreateGaps(true);      
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(usersLabel)
                        .addComponent(scrollPaneOne)
                        .addComponent(inviteUsers, Alignment.CENTER))
                .addGroup(layout.createParallelGroup(Alignment.CENTER)
                        .addComponent(leaveConvo, Alignment.TRAILING)
                        .addComponent(scrollPaneTwo)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(typedMsgPane)
                                .addComponent(sendMessage)))
        );
        
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(usersLabel)
                        .addComponent(leaveConvo, Alignment.TRAILING))
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(scrollPaneOne)
                                .addComponent(inviteUsers))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(scrollPaneTwo)
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(typedMsgPane)
                                        .addComponent(sendMessage, Alignment.CENTER))))
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
                	listOfUsersModel.clear();
                    for(String s: update.userList){
                    	listOfUsersModel.addElement(s);
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

}
