package wristcam.gui;

import java.util.*;
import java.io.*;


import wristcam.comm.*;

public class ImageDownloadingThread implements Runnable{
	WQVImagesPanel qwvImagesPanel;
        String device;
	public ImageDownloadingThread(WQVImagesPanel qwvImagesPanel,String device){
		this.qwvImagesPanel= qwvImagesPanel;
                this.device = device;
		new Thread(this).start();
	}
	public void run(){
		try {
		CasioWristCamCommunicator comm = new CasioWristCamCommunicator(device);
		comm.connect();
		ImageStreamHandler img = new ImageStreamHandler(qwvImagesPanel);
		comm.getImages(img);
		comm.close();
		Vector images = img.getWQVImages();
		String startString ="0";
		for (int x = 0 ; x < images.size(); x++){
			if (x ==10) startString="";
			WQVImage image = (WQVImage)images.elementAt(x);
			image.saveImageToXpm(startString + x + ".xpm");
			image.saveImageToBin(startString + x + ".bin");
		}
            } catch (IOException ioe){
                System.err.println("IO exception while downloading images");
            }
	}
}
