package wristcam.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import java.util.*;
/**
 * @author Kees Jongenburger
 * @version $Id: Application.java,v 1.1 2002/11/03 11:01:01 keesj Exp $
 **/
public class Application extends JFrame implements ActionListener{
    JButton exitButton;
    JButton downloadButton;
    JButton navigateButton;

    JTabbedPane tabPane ;
    JToolBar toolBar;
    
    String device= null;
    
    public Application(){
        setName("WQV-Casio Wristcam image reader");
	setTheme();
        
        tabPane = new JTabbedPane();
        
	//
	toolBar = new JToolBar ();
	toolBar.setLayout(new BorderLayout());
	exitButton = new JButton(new ImageIcon(Application.class.getResource("resources/exit.gif")));
	exitButton.addActionListener(this);
	exitButton.setToolTipText("exit the application");
	downloadButton = new JButton(new ImageIcon(Application.class.getResource("resources/download.gif")));
	downloadButton.addActionListener(this);
	downloadButton.setToolTipText("download all images from your watch");
	navigateButton = new JButton(new ImageIcon(Application.class.getResource("resources/navigation.gif")));
	navigateButton.addActionListener(this);
	navigateButton.setToolTipText("navigate existing picture and edit the meta-data");
	JPanel pan = new JPanel();
	pan.setLayout(new FlowLayout());
	pan.add(downloadButton);
	pan.add(navigateButton);
	toolBar.add(pan,BorderLayout.WEST);
	//toolBar.add(navigateButton,BorderLayout.EAST);
	toolBar.add(exitButton,BorderLayout.EAST);

        getContentPane().add(toolBar,BorderLayout.NORTH);

        getContentPane().add(tabPane,BorderLayout.CENTER);
        pack();
        setSize(800,600);
        show();
    }
    
    
    public static void main(String[] argv) throws IOException{
        Application app= new Application();
    }
    
    public void actionPerformed(ActionEvent actionEvent){
        if (actionEvent.getSource() == downloadButton){
                WQVImagesPanel wqvImagesPanel = new WQVImagesPanel();
		JScrollPane sp = new JScrollPane(wqvImagesPanel);
                tabPane.addTab("downloaded images",sp);
                new ImageDownloadingThread(wqvImagesPanel,device);
        } else if (actionEvent.getSource() == navigateButton){
		NavigationPanel nav  = new NavigationPanel("/home/keesj/wristcam/keep");
		JScrollPane sp = new JScrollPane(nav);
                tabPane.addTab("navigation",sp);
        } else if (actionEvent.getSource() == exitButton){
		System.exit(0);
        }
        
    }
    public void setTheme(){
        javax.swing.plaf.metal.MetalTheme theme = new HelixTheme();
            javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(theme);
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                SwingUtilities.updateComponentTreeUI(this);
            } catch(Exception e) {
                System.out.println(e);
            }
    }
}
