package view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.plaf.FontUIResource;

import view.components.FontUI;
import view.components.JTextFieldLimit;

/**
 * The JPanel that holds the login pane. Will be added to MainGUI.
 *
 */
@SuppressWarnings("serial")
public class LoginWindow extends JFrame implements ActionListener {
    private final Dimension WIN_SIZE = new Dimension(350, 600);
    private JLabel usernameLabel, welcomeLogo, ipLabel, portNumberLabel;
    private JTextPane invalidUserMsg;
    private JTextField usernameField, ipField, portNumberField;
    private JButton loginButton;
    private String username, ipAddrText, portNumText;

    /*
     * LoginGUI constructor
     */
    public LoginWindow() {
        FontUI.addUIFonts();
        FontUI.setUIFont(new FontUIResource("Open Sans", Font.PLAIN, 12));
        
        setTitle("Welcome to ChatHub!");
        setSize(WIN_SIZE);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Container cont = getContentPane();
        cont.setLayout(new GridBagLayout()); // Use GridBag layout to center everything
        JPanel container = createUI();
        
        add(container); // Add the JPanel
    }
    
    private JPanel createUI() {
        JPanel container = new JPanel();
        container.setPreferredSize(WIN_SIZE);
        
        welcomeLogo = new JLabel();
        welcomeLogo.setSize(new Dimension(300, 100));
        ImageIcon welcomeIcon = new ImageIcon("src/view/resources/welcomelogo.png");
        Image welcomeImg = welcomeIcon.getImage().getScaledInstance(
                welcomeLogo.getWidth(), welcomeLogo.getHeight(), Image.SCALE_FAST);
        welcomeLogo.setIcon(new ImageIcon(welcomeImg));
        
        ipLabel = new JLabel("IP Address");
        portNumberLabel = new JLabel("Port Number");
        usernameLabel = new JLabel("Username");
        
        invalidUserMsg = new JTextPane();
        invalidUserMsg.setContentType("text/html");
        invalidUserMsg.setText("<html><body style=\"font-family:Open Sans;font-size:9px;color:#CC3399\">" +
                "<b>Invalid login:</b><br>" +
                "Can only use alphanumeric characters." +
                "</body></html>");
        invalidUserMsg.setVisible(false);
        invalidUserMsg.setOpaque(false);
        invalidUserMsg.setPreferredSize(new Dimension(200, 30));
        
        usernameField = new JTextField();
        usernameField.setDocument(new JTextFieldLimit(12));
        usernameField.setToolTipText("<html>- Use alphanumeric characters only.<br>" +
        		"- Limited to 12 characters.<br>" +
        		"- If the username is in use, an '_' followed <br>" +
        		"by an integer will be appended.</html>");
        
        ipField = new JTextField("localhost");
        ipField.setToolTipText("<html>- Valid IP addresses only, so make sure<br>" +
        		"the IP Address you submit is correct!<br>" +
        		"- An empty field defaults to localhost.</html>");
 
        portNumberField = new JTextField("4444");
        portNumberField.setToolTipText("<html>- Valid port numbers only, so make sure<br>" +
        		"the Port Number you submit is correct!<br>" +
        		"- An empty field defaults to 4444.</html>");
        
        loginButton = new JButton(" Login", new ImageIcon("src/view/resources/sign-in.png"));
        loginButton.addActionListener(this); 
        loginButton.setToolTipText("Click to login!");

        GroupLayout layout = new GroupLayout(container);
        container.setLayout(layout);

        // Set automatic gap insertion to true
        layout.setAutoCreateGaps(true);      
        layout.setAutoCreateContainerGaps(true);
     
        // Layout components
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                .addComponent(welcomeLogo, Alignment.CENTER)
                .addComponent(invalidUserMsg, Alignment.CENTER)
                .addComponent(usernameLabel)
                .addComponent(usernameField)
                .addComponent(ipLabel)
                .addComponent(ipField)
                .addComponent(portNumberLabel)
                .addComponent(portNumberField)
                .addComponent(loginButton, Alignment.CENTER)
                );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addComponent(welcomeLogo)
                .addGap(50) // Empty space between "Welcome!" and username input
                .addComponent(invalidUserMsg)
                .addComponent(usernameLabel)
                .addComponent(usernameField)
                .addComponent(ipLabel)
                .addComponent(ipField)
                .addComponent(portNumberLabel)
                .addComponent(portNumberField)
                .addComponent(loginButton)
                );
        
        return container;
    }
    /*
     * Getter method to get the IP address
     */
    public synchronized String getIPAddress() {
        return ipAddrText;
    }
    
    /*
     * getter method to get the port number
     */
    public synchronized String getPortNumber() {
        return portNumText;
    }
    
    /*
     * getter method to get the username
     */
    public synchronized String getUsername() {
        return username;
    }
    
    /*
     * processed information passed from the action listeners
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        // Checks whether username is valid (starts with alpha char, can contain A-Za-z_
        // Displays warning msg appropriately
        if (src == loginButton) {
            String fieldText = usernameField.getText().trim();
            
            if (fieldText.matches("[A-Za-z0-9]+")) {
                username = fieldText;
                ipAddrText = (ipField.getText().equals(""))? "localhost" : ipField.getText();
                portNumText = (portNumberField.getText().equals(""))? "4444" : portNumberField.getText();
                invalidUserMsg.setVisible(false);
            } else {
                // Invalid username: display warning msg, clear text field
                invalidUserMsg.setVisible(true);
                usernameField.setText("");
            }
        }
    }
}
