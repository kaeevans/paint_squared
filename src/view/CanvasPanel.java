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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class CanvasPanel extends JPanel{
	private final ChatWindow parent;
    private PrintWriter writer;
    private String convoName;
    public JPanel panel;
    public PadDraw drawPad;

	public CanvasPanel(String convoName, ChatWindow owner, PrintWriter writer){
	    this.convoName = convoName;
	    this.parent = owner;
	    this.writer = writer;

	    Icon iconB = new ImageIcon("blue.gif");
		Icon iconM = new ImageIcon("magenta.gif");
		Icon iconR = new ImageIcon("red.gif");
		Icon iconBl = new ImageIcon("black.gif");
		Icon iconG = new ImageIcon("green.gif");
				
		// Container content = this.parent.getContentPane();
		// content.setLayout(new BorderLayout());
		
		drawPad = new PadDraw();
		
		// content.add(drawPad, BorderLayout.CENTER);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(32, 68));
		panel.setMinimumSize(new Dimension(32, 68));
		panel.setMaximumSize(new Dimension(32, 68));
		
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				drawPad.clear();
			}
		});
		
		JButton redButton = new JButton(iconR);
		redButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				drawPad.red();
			}

		});
		
		JButton blackButton = new JButton(iconBl);
		blackButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				drawPad.black();
			}
		});
		
		JButton magentaButton = new JButton(iconM);
		magentaButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				drawPad.magenta();
			}
		});
		
		JButton blueButton = new JButton(iconB);
		blueButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				drawPad.blue();
			}
		});
		
		JButton greenButton = new JButton(iconG);
		greenButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				drawPad.green();
			}
		});
		blackButton.setPreferredSize(new Dimension(16, 16));
		magentaButton.setPreferredSize(new Dimension(16, 16));
		redButton.setPreferredSize(new Dimension(16, 16));
		blueButton.setPreferredSize(new Dimension(16, 16));
		greenButton.setPreferredSize(new Dimension(16,16));
		
		panel.add(greenButton);
		panel.add(blueButton);
		panel.add(magentaButton);
		panel.add(blackButton);
		panel.add(redButton);
		panel.add(clearButton);
		
		// content.add(panel, BorderLayout.WEST);
	}
}

@SuppressWarnings("serial")
class PadDraw extends JComponent{
	Image image;
	Graphics2D graphics2D;
	int currentX, currentY, oldX, oldY;

	public PadDraw(){
		setDoubleBuffered(false);
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				oldX = e.getX();
				oldY = e.getY();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				currentX = e.getX();
				currentY = e.getY();
				if(graphics2D != null)
				graphics2D.drawLine(oldX, oldY, currentX, currentY);
				repaint();
				oldX = currentX;
				oldY = currentY;
			}

		});
	}

	public void paintComponent(Graphics g){
		if(image == null){
			image = createImage(getSize().width, getSize().height);
			graphics2D = (Graphics2D)image.getGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			clear();

		}
		g.drawImage(image, 0, 0, null);
	}


	public void clear(){
		graphics2D.setPaint(Color.white);
		graphics2D.fillRect(0, 0, getSize().width, getSize().height);
		graphics2D.setPaint(Color.black);
		repaint();
	}
	public void red(){
		graphics2D.setPaint(Color.red);
		repaint();
	}
	public void black(){
		graphics2D.setPaint(Color.black);
		repaint();
	}
	public void magenta(){
		graphics2D.setPaint(Color.magenta);
		repaint();
	}
	public void blue(){
		graphics2D.setPaint(Color.blue);
		repaint();
	}
	public void green(){
		graphics2D.setPaint(Color.green);
		repaint();
	}

}


