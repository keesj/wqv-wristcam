package wristcam.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;

/**
 *
 * @author Kees Jongenburger
 * @version $Id: NavigationPanel.java,v 1.2 2002/11/12 22:14:42 keesj Exp $
 */
public class NavigationPanel extends JPanel implements ActionListener{
    WQVImagesPanel wqvImagesPanel;
    JTextField textField;
    
    public NavigationPanel(String directory) {
        setLayout(new BorderLayout());
        textField = new JTextField(directory);
        textField.addActionListener(this);
        add(textField,BorderLayout.NORTH);
        wqvImagesPanel = new WQVImagesPanel();
        add(wqvImagesPanel,BorderLayout.CENTER);
        try {
            readDir(new File(directory));
        } catch (IOException ioe){
            System.err.println("navigationPane exception " + ioe.getMessage());
        }
    }
    
    public void readDir(File directory) throws IOException{
        if (directory.isDirectory()){
            wqvImagesPanel.removeAll();
            File[] files = directory.listFiles();
            for (int x =0 ; x < files.length ; x++){
                File file = files[x];
                if (file.isFile()){
                    if (file.getName().toLowerCase().endsWith(".bin")){
                        if (file.length() == 7229){
                            WQVImage image = new WQVImage(file);
                            
                            wqvImagesPanel.addImage(new WQVImagePanel(image));
                        }
                    }
                }
            }
            //wqvImagesPanel.validate();
            validate();
            repaint();
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == textField){
            
            try {
                readDir(new File(textField.getText()));
            } catch (Exception ex){
                System.err.println("error:" + ex.getMessage());
            };
            System.err.println(textField.getText());
        }
    }
    
}
