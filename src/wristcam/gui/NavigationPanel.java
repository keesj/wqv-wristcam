package wristcam.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;

/**
 *
 * @author Kees Jongenburger
 * @version $Id: NavigationPanel.java,v 1.1 2002/11/03 11:01:01 keesj Exp $
 */
public class NavigationPanel extends JPanel{
    WQVImagesPanel wqvImagesPanel;
    
    public NavigationPanel(String directory) {
        setLayout(new BorderLayout());
        
        wqvImagesPanel = new WQVImagesPanel();
        add(wqvImagesPanel,BorderLayout.CENTER);
        try {
            readDir(new File(directory));
        } catch (IOException ioe){
            System.err.println("navigationPane exception " + ioe.getMessage());
        }
    }
    
    public void readDir(File directory) throws IOException{
        //imageCanvas.removeAll();
        if (directory.isDirectory()){
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
        }
	wqvImagesPanel.validate();
    }
}
