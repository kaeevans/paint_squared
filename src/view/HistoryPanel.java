package view;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import controller.Protocol;
/*
 * The history panel (when the user clicks the See History Button).
 */
@SuppressWarnings("serial")
public class HistoryPanel extends JPanel {
    private HistoryWindow parent;
    private String convoName;
    private JList msgHistory;
    private DefaultListModel msgHistoryModel;
    private JScrollPane msgHistoryPane;
    private JButton closeBtn;
    private JLabel msgHistoryLabel;

    public HistoryPanel(String convoName, HistoryWindow owner){
        this.parent = owner;
        this.convoName = convoName;
        setSize(HistoryWindow.PANEL_SIZE);
        
        closeBtn = new JButton(" Close Message", new ImageIcon("src/view/resources/close.png"));
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeTab();
            }
        });
        msgHistoryModel = new DefaultListModel();
        msgHistory = new JList(msgHistoryModel);
        msgHistoryPane = new JScrollPane(msgHistory);
        msgHistoryLabel = new JLabel("Message History");
        msgHistoryLabel.setFont(new Font("Open Sans", Font.BOLD, 12));
        
        // Set up GUI
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);      
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(msgHistoryLabel)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(closeBtn)
                    .addComponent(msgHistoryPane))
        );
        layout.setVerticalGroup(layout.createParallelGroup()
                .addComponent(msgHistoryLabel)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(closeBtn)
                    .addComponent(msgHistoryPane))
        );
        
        add(closeBtn);
        add(msgHistoryPane);
    }
    
    /*
     * method to remove a tab
     */
    private void removeTab() {
        parent.removeConversation(convoName);
        if (parent.getNumTabs() == 0)
            parent.setVisible(false);
    }
    /*
     * Processes a message from the server to update
     */
    public void update(final Protocol update)
    {
        switch(update.command) {
            // Change the transcript pane
            case TRANSCRIPT:
                SwingUtilities.invokeLater(new Runnable() {
                    public void run(){
                        msgHistoryModel.clear();
                        for(int ii=update.messageField.size()-1; ii>-1; ii--)
                            msgHistoryModel.addElement(update.messageField.get(ii));
                    }
                });
                break;
    
             default:
                System.err.println("Warning, attempted to update chatroom with protocol type: "+update.command.toString());
                break;
        }
    }

}
