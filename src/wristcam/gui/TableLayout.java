package wristcam.gui;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
/**
 * table layout will act the same as GridLayout width two differances
 * the size of the differen rows is variable
 * <pre>
 *  ------------------------------------
 *  | label | content                  |
 *  ------------------------------------
 *  | label | the large content        |
 *  |       | component                |
 *  ------------------------------------
 * </pre>
 * the size depends on the size of the components
 * @author Kees Jongenburger
 * @version $Id: TableLayout.java,v 1.1 2002/11/03 11:01:02 keesj Exp $
 **/
public class TableLayout implements LayoutManager{
    public static boolean debug = false;
    int width;
    /**
     * @param width width in "pieces"
     **/
    public TableLayout(int width){
        this.width = width;
    }
    public void debug(String data){
        if (debug){
            System.err.println("LayoutManager.debug(" + data + ")");
        }
    }
    
    public void layoutContainer(Container container){
        synchronized (container.getTreeLock()) {
            int componentCount = container.getComponentCount();
            Dimension[] dims = new Dimension[componentCount];
            //get all the preferredSizes and put them in an array
            for (int x = 0 ; x < componentCount ; x++){
                Component comp = container.getComponent(x);
                dims[x] = comp.getPreferredSize();
            }
            
            int[] widths = new int[width];
            //for each row find the width
            for (int x = 0 ; x < componentCount ; x++){
                int compwidth = dims[x].width;
                
                if (compwidth > widths[ x % width]){
                    widths[x % width] = compwidth;
                    debug("setting width["+ (x %width) +"] to "+ compwidth +".");
                }
                
            }
            int y =0;
            int maxY =0 ;
            for (int x =0 ; x < componentCount ; x++){
                int startX =0;
                for (int startXCounter =0 ; startXCounter < x %width; startXCounter ++){
                    startX += widths[startXCounter];
                }
                if (maxY < dims[x].height){
                    maxY = dims[x].height;
                }
                if (x != 0 && x % width  == 0) {
                    y += maxY;
                    maxY = 0;
                }
                Component comp = container.getComponent(x);
                comp.setBounds(startX,y,dims[x].width,dims[x].height);
                debug("setting startx of component number "+ x +" to "+ startX +" y is "+ y +".");
                
                
            }
        }
    };
    
    public Dimension getSize(Container container){
        int componentCount = container.getComponentCount();
        Dimension[] dims = new Dimension[componentCount];
        //get all the preferredSizes and put them in an array
        for (int x = 0 ; x < componentCount ; x++){
            Component comp = container.getComponent(x);
            dims[x] = comp.getPreferredSize();
        }
        
        int[] widths = new int[width];
        //for each row find the width by searching down and finding the widest
        // component
        for (int x = 0 ; x < componentCount ; x++){
            int compwidth = dims[x].width;
            
            if (compwidth > widths[ x % width]){
                widths[x % width] = compwidth;
                debug("setting width["+ (x %width) +"] to row "+ compwidth +".");
            }
        }
        
        int[] heights = new int[(componentCount / width) + 1];
        //for each row find the width by searching down and finding the widest
        // component of a row and adding it to the higest
        for (int x = 0 ; x < componentCount ; x++){
            int compheight = dims[x].height;
            if (compheight > heights[ (x / width) ]){
                heights[(x / width)] = compheight;
                debug("setting height["+ compheight +"] to row "+ (x / width) +".");
            }
        }
        
        int y =0;
        for (int heightCounter =0 ; heightCounter < (componentCount / width) + 1; heightCounter ++){
            y += heights[heightCounter];
        }
        
        int x = 0;
        for (int k = 0; k < width;k++){
            x =+ widths[k];
        }
        
        return new Dimension(x, y);
    }
    
    public void addLayoutComponent(String data,Component component){
        debug("addLayoutComponent");
    };
    
    public void removeLayoutComponent(Component component){
        debug("removeLayoutComponent");
    };
    
    public Dimension preferredLayoutSize(Container container){
        return getSize(container);
    };
    
    public Dimension minimumLayoutSize(Container container){
        return getSize(container);
    };
}
