package wristcam.gui;

import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
/**
 * minimal theme based on the xmms Helix-Sawfish  theme
 * @author Kees Jongenburger
 * @version $Id: HelixTheme.java,v 1.1 2002/11/03 11:01:01 keesj Exp $
 **/
public class HelixTheme extends DefaultMetalTheme{
    public String getName() { return "Helix Theme";};
    private final ColorUIResource primary1     = new ColorUIResource(43, 82, 124);
    private final ColorUIResource primary2     = new ColorUIResource(182, 207, 237);//active stuff light blue
    private final ColorUIResource primary3     = new ColorUIResource(189, 218, 189);
    
    /**
    private final ColorUIResource secondary3   = new ColorUIResource(222, 218, 222);
    private final ColorUIResource secondary2   = new ColorUIResource(156, 178, 205);
    private final ColorUIResource secondary1   = new ColorUIResource(189, 218, 189);
    **/

    private final ColorUIResource secondary1   = new ColorUIResource(0, 0, 0);//borders / default text
    private final ColorUIResource secondary2   = new ColorUIResource(109, 124, 142);//black (borders)
    private final ColorUIResource secondary3   = new ColorUIResource(222,218,213);//light grey  (moost backgrounds)
    
    // the functions overridden from the base class => DefaultMetalTheme
    
    protected ColorUIResource getPrimary1() { return primary1; }
    protected ColorUIResource getPrimary2() { return primary2; }
    protected ColorUIResource getPrimary3() { return primary3; }
    
    protected ColorUIResource getSecondary1() { return secondary1; }
    protected ColorUIResource getSecondary2() { return secondary2; }
    protected ColorUIResource getSecondary3() { return secondary3; }

    protected ColorUIResource getWhite(){ return new ColorUIResource(217, 249, 217);};
    //ok
    //public ColorUIResource getControl() { return new ColorUIResource(156, 178, 205); }
}
