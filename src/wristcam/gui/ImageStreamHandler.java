package wristcam.gui;

import java.io.*;
import java.util.*;

public class ImageStreamHandler {
    Vector wqvImages;
    Vector streams;
    
    int imageCount =-1;
    boolean finished= false;
    
    int byteCount =0;
    
    WQVImagesPanel wqvImagesPanel = null;
    /** Creates a new instance of ImageStreamHandler */
    public ImageStreamHandler() {
    }
    
    
    public ImageStreamHandler(WQVImagesPanel wqvImagesPanel){
        this.wqvImagesPanel = wqvImagesPanel;
    }
    
    public void setImageCount(int count){
        System.err.println("image count:" + count);
        imageCount =count;
        wqvImages = new Vector();
        streams = new Vector();
        
        for (int x = 0 ; x < imageCount ; x++){
            WQVImage image = new WQVImage();
            PipedInputStream in = new PipedInputStream();
            PipedOutputStream out  = new PipedOutputStream();
            try {
                in.connect(out);
            } catch (IOException ioe){
                System.out.println(ioe);
                ioe.printStackTrace();
            };
            image.setInputStream(in);
            wqvImages.addElement(image);
            streams.addElement(out);
            if (wqvImagesPanel != null){
                wqvImagesPanel.addImage(new WQVImagePanel(image));
                
            }
        }
        if (wqvImagesPanel != null){
            wqvImagesPanel.validate();
        }
    }
    
    
    public void write(byte data){
        //get the right outputSrteam
        int imageSize = 24 +5 + (60*120);
        try{
            //System.err.println("bytecount = "+ byteCount +" to  stream nummber " + (byteCount  / imageSize));
            OutputStream out = (OutputStream)streams.elementAt( byteCount / imageSize );
            out.write(data);
            out.flush();
        } catch (IOException ioe){
            System.out.println(ioe);
            ioe.printStackTrace();
        };
        byteCount ++;
    }
    
    public Vector getWQVImages(){
        return wqvImages;
    }
}
