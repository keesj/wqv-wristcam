package wristcam.gui;
import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.image.*;

/**
 * Class to handle images from a casio WQV-2 wrist cam.<BR>
 * When transfering data to the computer by means of the pad2 the wacht uses this format.
 * Casio images are stored with the .BIN extention. the image format
 * is quit simple.<BR>
 * try to keep your image in this format it has useluff meta-data
 * <UL>
 *    <LI>The image format has a contant size of 7229 bytes.</LI>
 *    <LI>The first 24 bytes a the image title(ascii)</LI>
 *    <LI>The next 5 bytes are the date and time
 *       <UL>
 *          <LI>year starting from 2000 (int value of byte + 2000 = year) 0..255</LI>
 *          <LI>month (int value of byte = month) 0..11</LI>
 *          <LI>day of month (int value of byte = day) 0..31</LI>
 *          <LI>hour of day(int value of byte = hour) 0..24</LI>
 *          <LI>minutes of day(int value of byte = minutes) 0..59</LI>
 *       </UL>
 *    </LI>
 *    <LI>The next 120 * 60 bytes are the actual image.
 *         The image is 120 times 120 pixels with 16 greyscales
 *         each pixel is stored in 4 bits so one byte contains 2 pixels
 *         the value of the 4 bits is the darkness so 0 (0x0) is white and 16 (0xf) is black.
 *         The order is different from what I would expect (the pixel in the 4 MSB is right  of the pixel in the LSB)
 *    </LI>
 * </UL>
 *
 * @author Kees Jongenburger
 * @version $Id: WQVImage.java,v 1.1 2002/11/03 11:01:02 keesj Exp $
 **/
public class WQVImage  implements Runnable{
    /**
     * holder for the title stored in the image
     **/
    byte[] title =new byte[24];
    /**
     * holder for the date stored in the image
     **/
    byte[] date =new byte[5];
    /**
     * grayscale image data 1 byte = 1 pixel
     **/
    byte[] imagedata = new byte[120*120];
    
    /**
     * image source
     **/
    MemoryImageSource memoryImageSource;
    
    Image image;
    /**
     * inputStream
     **/
    InputStream threadInputStream;
    
    
    /**
     *
     **/
    File sourceFile = null;
    
    public WQVImage(File file) throws IOException{
        init();
        this.sourceFile = file;
        initializeImageFromInputStream(new FileInputStream(file));
    }
    
    public WQVImage(){
        init();
    }
    
    private void init(){
        /**
         * grey where index 0 is white and index 15 is black
         **/
        byte[] grey = {
            (byte)0xFF, (byte)0xEE, (byte)0xDD, (byte)0xCC,
            (byte)0xBB, (byte)0xAA, (byte)0x99, (byte)0x88,
            (byte)0x77, (byte)0x66, (byte)0x55, (byte)0x44,
            (byte)0x33, (byte)0x22, (byte)0x11, (byte)0x00};
            //create a new colorModel for the image
            IndexColorModel colorModel = new IndexColorModel(4 /*bits*/,16/*colors*/,grey/*red*/,grey/*green*/,grey/*blue*/);
            memoryImageSource =new MemoryImageSource(120,120,colorModel,imagedata,0,120);
            memoryImageSource.setAnimated(true);
    }
    
    public void setTitle(String titleString){
        char[] c = titleString.toCharArray();
        for (int x =0 ; x < 24 ; x++){
            if (c.length < x){
                this.title[x] =(byte) c[x] ;
            } else {
                this.title[x] = (byte)0x20;;
            }
        }
    }
    
    public String getTitle(){
        return new String(title);
    }
    
    
    
    public byte[] getDate(){
        return date;
    }
    
    public String getDateString(){
        StringBuffer sb = new StringBuffer();
        sb.append("200" + date[0]) ; //year
        sb.append(" " + date[1]) ; //month
        sb.append("/" + date[2]) ; //day of month
        sb.append(" " + date[3]) ; //our
        sb.append(":" + date[4]) ; //minute
        return sb.toString();
    }
    
    public byte[] getImageData(){
        return imagedata;
    }
    
    public void setImageData(byte[] imagedata){
        for (int x =0 ; x < imagedata.length; x++){
            this.imagedata[x] = imagedata[x];
        }
    }
    
    public void setInputStream(InputStream in){
        threadInputStream = in;
        new Thread(this).start();
    }
    public void run(){
        try {
            initializeImageFromInputStream(threadInputStream);
        } catch (Exception e){
            System.err.println("expeption initialize thread{"+ e.getMessage() +"}");
        }
    }
    
    /**
     * @param in the inputstream pointing to the image
     * @return an java.awt.Image created from the input stream, the image has 2 properties(title and date);
     **/
    public void initializeImageFromInputStream(InputStream in) throws IOException{
        while(in.available() < 29){
            try {Thread.sleep(300);} catch(Exception ioe){};
        }
        in.read(title);
        
        in.read(date);
        
        
        //read the image row by row and store every pixel is a separate
        //byte
        byte[] row = new byte[60];
        for (int y =0;  y < 120 ; y++){
            while(in.available() < 60){
                try {Thread.sleep(300);} catch(Exception ioe){};
            }
            in.read(row);
            for (int x =0 ; x < 120 ; x+=2){
                byte left = (byte)((row[x/2] & 0xf0) >> 4);
                byte right = (byte)(row[x/2] & 0x0f );
                imagedata[y*120 + x] = right ;
                imagedata[y*120 + x +1 ] = left;
            }
            memoryImageSource.newPixels(0,y,120,1);
            
        }
        //memoryImageSource.newPixels(0,0,120,120,true);
        //memoryImageSource.setFullBufferUpdates(fullbuffers
	//memoryImageSource.imageComplete();
	
        
        in.close();
    }
    
    /**
     * @return an java.awt.Image created from the input stream, the image has 2 properties(title and date);
     **/
    public Image getImage() {
        if (image ==null){
            
            Properties props = new Properties();
            props.put("title", getTitle());
            props.put("date", getDateString());
            image =Toolkit.getDefaultToolkit().createImage(memoryImageSource);
            
        } 
        return image;
    }
    
    public void saveImageToBin(String fileName){
        try {
            File file = new File(fileName);
            OutputStream out = new FileOutputStream(file);
	    out.write(title);
	    out.write(date);
	    for (int x =0 ; x  < 120 *120 ; x+=2){
		byte left = imagedata[x];
		byte right = imagedata[x+ 1];
		byte rightup = (byte) (right << 4);
                byte data = (byte)(left | rightup);
		out.write(data);
	    }
	    out.flush();
	    out.close();

        } catch (IOException ioe){
	}
    }
    /**
     * saves the image to the xpm format. if you don't know what the xpm format is
     * just look at the xmp in an text editor.
     * The image can later be processed using a tool like imagemagick
     **/
    public void saveImageToXpm(String fileName){
        String[] pixels = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
        try {
            File file = new File(fileName);
            OutputStream out = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(out);
            pw.println("/* XPM */");
            pw.println("static char * image_xpm[] = {");
            pw.println("	\"120 120 16 1\",");
            pw.println("	\"0      c #FFFFFF\",");
            pw.println("	\"1      c #EEEEEE\",");
            pw.println("	\"2      c #DDDDDD\",");
            pw.println("	\"3      c #CCCCCC\",");
            pw.println("	\"4      c #BBBBBB\",");
            pw.println("	\"5      c #AAAAAA\",");
            pw.println("	\"6      c #999999\",");
            pw.println("	\"7      c #888888\",");
            pw.println("	\"8      c #777777\",");
            pw.println("	\"9      c #666666\",");
            pw.println("	\"a      c #555555\",");
            pw.println("	\"b      c #444444\",");
            pw.println("	\"c      c #333333\",");
            pw.println("	\"d      c #222222\",");
            pw.println("	\"e      c #111111\",");
            pw.println("	\"f      c #000000\",");
            byte[] data = getImageData();
            for(int y =0 ; y < 120 ; y++){
                pw.print("\"");
                for(int x =0 ; x < 120 ; x++){
                    int index = data[ (y * 120) + x] &0xff;
                    //System.err.println(index);
                    pw.print(pixels[index]);
                }
                if (y < 119){
                    pw.println("\",");
                }
            }

            pw.println("\"}");
            pw.flush();
	    out.close();
        } catch (IOException ioe){
            
        }
    }
    
    /**
     * the main method provides a way to convert bin files to xpm files
     **/
    public static void main(String[] argv) throws Exception{
        if (argv.length != 2){
            System.err.println("usage: " + WQVImage.class.getName() +" in.bin out.xpm");
            System.exit(1);
        }
        File file = new File(argv[0]);
        InputStream in = new FileInputStream(file);
        WQVImage image  = new WQVImage();
        image.initializeImageFromInputStream(in);
        image.saveImageToXpm(argv[1]);
    }
}
