package wristcam.gui;

import java.awt.*;
import javax.swing.*;
/**
 *
 * @author  keesj
 */
public class WQVImagesPanel extends JPanel{
    public WQVImagesPanel() {
        super();
	setBackground(Color.white);
	setLayout(new TableLayout(5));
    }

    public void addImage(WQVImagePanel panel){
        add(panel);
        
    }
}
