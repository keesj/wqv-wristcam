package wristcam.gui;
import java.awt.*;
import java.io.*;

/**
 * Simple viewer for WQFImages
 * @author Kees Jongenburger
 * @version $Id: ImageTest.java,v 1.1 2002/11/03 11:01:01 keesj Exp $
 **/
public class ImageTest extends Frame{
    Image image ;
    public ImageTest(String fileName){
        setSize(300,300);
        try {
            WQVImage wqvImage = new WQVImage();
            wqvImage.initializeImageFromInputStream(new FileInputStream(fileName));
            image = wqvImage.getImage();
        } catch(java.io.IOException ioe){
            System.err.println("IOE" + ioe.getMessage());
        }
        show();
    }
    
    public void paint(Graphics g){
        update(g);
    }
    
    public void update(Graphics g){
        Dimension dim = getSize();
        g.drawImage(image,0,0,dim.width,dim.height,this);
    }
    
    /**
     * simple viewer for images
     **/
    public static void main(String[] argv){
        if (argv.length != 1){
            System.err.println("Usage: " + ImageTest.class.getName() + " image to view");
            System.exit(1);
        }
        new ImageTest(argv[0]);
    }
}
