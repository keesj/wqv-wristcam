package wristcam.gui;

import java.awt.*;
import javax.swing.*;

import java.util.*;

public class WQVImagePanel extends JPanel{
    
    Image offScreenImage = null;
    Graphics offScreenGraphics = null;
    Dimension offScreenSize = null;
    
    
    WQVImage wqvImage ;
    public WQVImagePanel(WQVImage image){
        super();
        this.wqvImage = image;
    }
    
    
    public Dimension getPreferredSize(){
        return new Dimension(122,144);
    }
    public Dimension getMaximumSize(){
        return new Dimension(122,144);
    }
    public Dimension getMinimumSize(){
        return new Dimension(122,144);
    }
    public Dimension getSize(){
        return new Dimension(122,144);
    }

    public void paint(Graphics g){
        update(g);
    }
    
    public void update(Graphics g){
        Dimension dim = getSize();
        
        if((offScreenImage==null) || (dim.width != offScreenSize.width) || (dim.height != offScreenSize.height)){
            if (offScreenImage != null){
                offScreenImage.flush();
            }
            offScreenImage = createImage(dim.width, dim.height);
            offScreenSize = dim;
            offScreenGraphics = offScreenImage.getGraphics();
        }
        
            //offScreenGraphics.setColor(new Color(140,240,240));
        offScreenGraphics.clearRect(0,0,dim.width,dim.height);
        
        int x =1;
        int y =1;
        
        Image image= wqvImage.getImage();
        if (image != null){
            offScreenGraphics.drawImage(image, x , y , this);

	    offScreenGraphics.setColor(Color.black);
            offScreenGraphics.drawString(wqvImage.getDateString(), x , y + 136 );
        }
        //draw a border
        offScreenGraphics.setColor(Color.black);
        //top
        offScreenGraphics.drawLine(x -1 ,y -1, x + 120 , y-1);
        //left
        offScreenGraphics.drawLine(x -1 ,y + 120, x -1 , y -1);
        //right
        offScreenGraphics.drawLine(x +120 ,y -1, x + 120 , y + 120);
        //bottom
        offScreenGraphics.drawLine(x +120 ,y + 120, x -1 , y + 120);
        offScreenGraphics.setColor(new Color(120,120,120));
        //right
        offScreenGraphics.drawLine(x +120 +1 ,y -1, x + 120 +1 , y + 120 +1);
        //bottom
        offScreenGraphics.drawLine(x +120+ 1 ,y + 120 +1 , x -1 , y + 120+ 1);
        
        g.drawImage(offScreenImage,0,0,this);
    }
}
