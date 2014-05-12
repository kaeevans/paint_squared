package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import controller.Command;
import controller.Protocol;
/*
 * Invited users window
 */
@SuppressWarnings("serial")
public class InviteUsersDialog extends JDialog implements ActionListener {
    private final JLabel welcomeText, onlineText, invitedText;
    private final JList onlineList, invitedList;
    private final JButton okButton, cancelButton, moveLeftButton, moveRightButton;
    private DefaultListModel onlineListModel, invitedListModel;

    private String roomName;
    private ChatWindow parent;
    
    public InviteUsersDialog(ChatWindow parent, String string, ModalityType documentModal, String roomName){
        super(parent, string, documentModal);
        this.parent = parent;
        this.roomName = roomName;
        
        welcomeText = new JLabel("Invite Users to Chatroom: "+roomName);
        welcomeText.setFont(new Font("Open Sans", Font.BOLD, 12));
        onlineText = new JLabel("Available to Invite");
        onlineText.setFont(new Font("Open Sans", Font.BOLD, 12));
        invitedText = new JLabel("Invited");
        invitedText.setFont(new Font("Open Sans", Font.BOLD, 12));

        onlineListModel = new DefaultListModel();
        onlineList = new JList(onlineListModel);
        onlineList.setToolTipText("This is a list of Online Users!");

        invitedListModel = new DefaultListModel();
        invitedList = new JList(invitedListModel);
        invitedList.setToolTipText("This is a list of the people you are choosing to invite to the conversation!");

        okButton = new JButton("Okay");
        okButton.setToolTipText("Click this button to send invites to the people on the invite list!");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                inviteSpecifiedUsers();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setToolTipText("Click this button if you have decided you do not want to invite users anymore!");

        moveLeftButton = new JButton(" << ");
        moveLeftButton.setToolTipText("Click on an invited User and then press this button to remove them from the list of people to be invited to the conversation.");
        moveLeftButton.addActionListener(this);

        moveRightButton = new JButton(" >> ");
        moveRightButton.addActionListener(this);
        moveRightButton.setToolTipText("Click on an online User and then press this button to add them to the list of people to be invited to the conversation");


        this.setPreferredSize(new Dimension(500,300));
        JPanel panel = new JPanel();

        JScrollPane scrollPaneOnline = new JScrollPane(onlineList);
        scrollPaneOnline.setPreferredSize(new Dimension(200, 100));
        JScrollPane scrollPaneInvited = new JScrollPane(invitedList);
        scrollPaneInvited.setPreferredSize(new Dimension(200, 100));

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);      
        layout.setAutoCreateContainerGaps(true);

        SequentialGroup topRow = layout.createSequentialGroup()
                .addComponent(welcomeText);

        SequentialGroup secondRow = layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.CENTER)
                        .addComponent(onlineText)
                        .addComponent(scrollPaneOnline)
                        .addComponent(okButton))
                        .addGroup(layout.createParallelGroup(Alignment.CENTER)
                                .addComponent(moveRightButton, Alignment.CENTER)
                                .addComponent(moveLeftButton, Alignment.CENTER))
                                .addGroup(layout.createParallelGroup(Alignment.CENTER)
                                        .addComponent(invitedText)
                                        .addComponent(scrollPaneInvited)
                                        .addComponent(cancelButton));

        SequentialGroup thirdRow = layout.createSequentialGroup()
                .addComponent(okButton)
                .addComponent(cancelButton);

        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.CENTER)
                .addGroup(topRow)
                .addGroup(secondRow)
                .addGroup(thirdRow));

        ParallelGroup second = layout.createParallelGroup(Alignment.CENTER)
                    .addGroup(layout.createSequentialGroup()
                            .addComponent(onlineText)
                            .addComponent(scrollPaneOnline))
                    .addGroup(layout.createSequentialGroup()
                            .addComponent(moveRightButton)
                            .addComponent(moveLeftButton))
                    .addGroup(layout.createSequentialGroup()
                            .addComponent(invitedText)
                            .addComponent(scrollPaneInvited));

        ParallelGroup third = layout.createParallelGroup()
                .addComponent(okButton)
                .addComponent(cancelButton);

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addComponent(welcomeText)
                .addGroup(second)
                .addGroup(third));

        add(panel);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == okButton) {
            
        } else if (src == cancelButton) {
            dispose();
        } else if (src == moveRightButton) {
            Object[] usersToMove = onlineList.getSelectedValues();
            for (Object user: usersToMove) {
                if (!invitedListModel.contains(user)){
                    invitedListModel.addElement(user);
                }
            }
        } else if (src == moveLeftButton) {
            Object[] usersToMove = invitedList.getSelectedValues();
            for (Object user: usersToMove) {
                invitedListModel.removeElement(user);
            }
        }
    }

    /*
     * Processes a message from the server to update
     */
    public void update(final Protocol update){
        switch(update.command){
        case USERLIST:
            SwingUtilities.invokeLater(new Runnable() {
                public void run(){
                    onlineListModel.clear();
                    for(String s: update.userList){
                        onlineListModel.addElement(s);
                    }
                    for (Object user: invitedListModel.toArray()){
                        if (!onlineListModel.contains(user)){
                            invitedListModel.removeElement(user);
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
    /*
     * method to invite selected users
     */
    private void inviteSpecifiedUsers(){
        List<String> listOfInvited = new ArrayList<String>();
        for (Object user: invitedListModel.toArray()){
            listOfInvited.add(user.toString());
        }
    		
        send(new Protocol(Command.INVITEMANY,roomName,listOfInvited));
        setVisible(false);
    }

    /*
     *  Sends a protocol message to the server
     */
    public void send(Protocol p)
    {
    	parent.send(p);
    }


}
