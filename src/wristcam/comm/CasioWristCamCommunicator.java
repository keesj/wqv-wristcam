package wristcam.comm;

import java.io.*;
import java.util.*;
//NOTE
//if you are using javax.comm change gnu.io to javax.comm
import gnu.io.*;

import wristcam.gui.*;
/**
 * Core class that handles the communication with the watch
 * @author Kees Jongenburger
 * @version $Id: CasioWristCamCommunicator.java,v 1.1 2002/11/03 11:01:03 keesj Exp $
 **/
public class CasioWristCamCommunicator {
    public static final byte PACKET_START_BYTE = (byte)0xc0;
    public static final byte PACKET_END_BYTE = (byte)0xc1;
    public static final byte ESCAPE_BYTE = (byte)0x7D;
    public static final byte SPACE =(byte)0x20;
    
    static CommPortIdentifier portId;
    static SerialPort serialPort = null;
    //util
    ByteUtil bu = new ByteUtil();
    //shared data
    byte sessionID =(byte)0xff;
    InputStream inputStream;
    OutputStream outputStream;
    
    /**
     * open the serial device
     * @param deviceName the name of the device to open if null the first serial device found
     * wil be used
     **/
    public CasioWristCamCommunicator(String deviceName) {
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier ci ;
        while (portId == null && portList.hasMoreElements()){
            ci =(CommPortIdentifier)portList.nextElement();
            if (ci.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                //if no device was selected take the first one
                //else try to find the richt device
                if ( deviceName == null || ci.getName().equals(deviceName)){
                    portId = ci;
                }
            }
        }
        if (portId ==  null){
            return;
        }
        try {
            serialPort = (SerialPort) portId.open("CasioWristCamCommunicator", 2000);
        } catch (PortInUseException e) {}
        try {
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {}
        
        try {
            serialPort.setSerialPortParams(115200,
            SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1,
            SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {}
        
    }
    
    public void close(){
        //TODO send data to the watch to finish connection..

        serialPort.close();
    }
    
    public synchronized String sendFrame(String data) throws IOException{
        return sendFrame(bu.stringToByteArray(data));
    }
    
    public synchronized String sendFrame(byte[] data) throws IOException{
        int checksum=0;
        
        //buffer the output so we can also display it
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        //write the start packet byte
        baos.write(PACKET_START_BYTE);
        baos.write(sessionID);
        
        checksum += sessionID & 0xff;
        for (int x=0 ; x < data.length; x++){
            byte currentByte = data[x];
            checksum += currentByte & 0xff;
            
            //handle escaping
            if (currentByte == ESCAPE_BYTE || currentByte == PACKET_START_BYTE || currentByte == PACKET_END_BYTE){
                baos.write(ESCAPE_BYTE);
                if ( (SPACE & currentByte) != 0){
                    currentByte = (byte) (currentByte & 0xDF);
                } else {
                    currentByte = (byte) (currentByte | SPACE);
                }
            }
            baos.write(currentByte);
        }
        
        //now add the checksum bytes
        checksum = checksum % 0x10000;
        baos.write( (checksum / 0x100  )& 0xff);
        baos.write( (checksum % 0x100) & 0xff);
        //write the end packet byte
        baos.write(PACKET_END_BYTE);
        
        byte[] output = baos.toByteArray();
        baos.close();
        log("s: " + bu.byteArrayToString(output));
        outputStream.write(output);
        
        outputStream.flush();
        if (inputStream.available() == 0){
            try {Thread.sleep(100); } catch(Exception e){};
        }
	int count =0;
        while(inputStream.available() == 0){
            try {Thread.sleep(400); } catch(Exception e){};
            log("waiting....");
	    count++;
	    if (count ==4){
            log("resend....");
		outputStream.write(output);
		outputStream.flush();
	    	count =0;
	    }
        }
        return bu.byteArrayToString(output);
    }
    
    
    
    public void flushInputStream() throws IOException{
        while(inputStream.available() > 0){
            inputStream.read();
        }
    }
    
    public byte[] readFrame() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean isEscaped = false;
        
        inputStream.read();
        int checksum =0;
        while (true){
            //read one byte
            byte data  = (byte)inputStream.read();
            
            if (isEscaped){
                if ( (SPACE & data) != 0){
                    data = (byte) (data & 0xDF);
                } else {
                    data = (byte) (data | SPACE);
                }
                isEscaped = false;
            } else {
                if (data == PACKET_START_BYTE){
                    isEscaped=true;
                    continue;
                }
                if (data == ESCAPE_BYTE){
                    isEscaped=true;
                    continue;
                }
                if (data == PACKET_END_BYTE){
                    break;
                }
            }
            checksum += data & 0xff;
            baos.write(data);
        }
        
        //check checksum
        byte[] retval =baos.toByteArray();
        checksum -= retval[retval.length -1] & 0xff;
        checksum -= retval[retval.length -2] &0xff ;
        checksum = checksum % 0x10000;
        
        int value = (retval[retval.length -2] &0xff) * 0x100;
        value += retval[retval.length -1] & 0xff;
        value = value % 0x10000;
        
        if ( checksum != value){
            log("Wrong checksum");
        }
        
        log("r: " + bu.byteArrayToString(retval));
        return retval;
    }
    
    public void connect() throws IOException{
        sessionID =(byte) 0xFF;
        
        flushInputStream();
        log("init");
        sendFrame("B3");
        
        byte[] answer = readFrame();
        
        if ( answer[1] !=(byte) 0xA3){
            log("r: " + bu.byteArrayToString(answer) + "  ==> ERROR expecting session request chalange");
            return ;
        }
        
        log("r: " + bu.byteArrayToString(answer) + " ==> session request chalange");
        
        byte[] data =new byte[6];
        
        data[0]= (byte)0x93;
        data[1] = answer[2];
        data[2] = answer[3];
        data[3] = answer[4];
        data[4] = answer[5];
        
        int val = (answer[6] * 256) + answer[7];
        Random random = new Random();
        random.setSeed(val);
        byte tempSessionID = (byte)(1 + (int) (255.0 * random.nextInt()/(Integer.MAX_VALUE + 1.0)));
        data[5] = tempSessionID;
        
        log("send session id");
        sendFrame(data);
        sessionID = tempSessionID;
        
        
        readFrame();
        flushInputStream();
        log("Connected!");
    }
    
    public void waitForInput() throws IOException{
        if (inputStream.available() == 0) log("Wait for input");
        while (inputStream.available() == 0){
            try {Thread.sleep(200); } catch(Exception e){};
            log(".");
        }
    }
    
    public void getImages(ImageStreamHandler imgh) throws IOException{
        log("read images");
        flushInputStream();
        
        
        sendFrame("11");
        readFrame();
        //r: 60 01 00 61
        
        //10 01 ==> get all images
        //10 00 ==> get one image
        log("ask to download multiple pictures");
        sendFrame("10 01");
        byte[] answer = readFrame();
        //r: 60 21 00 81
        if (answer[1] != 0x21){ //21 is ok?
            log("r: " + bu.byteArrayToString(answer) + " ==> error expecting 0x21");
            return;
        }
        
        log("get current information ");
        sendFrame("11");//11 is information?
        answer = readFrame();
        //r: 60 20 07 FA 1C 3D 18 01 F2
        
        if (answer[1] != 0x20){ //frame type 20?
            log("r: " + bu.byteArrayToString(answer) + " ==> error expecting 0x20");
            return;
        }
        int numberOfImages = answer[6] &0xff;
        imgh.setImageCount(numberOfImages);
        log("Threre are " + numberOfImages + " images on the watch");
        
        log("send misc frame");
        sendFrame("32 06");
        answer = readFrame();
        if (answer[1] != 0x41){
            log("r: " + bu.byteArrayToString(answer) + " ==> error expecting 0x41");
            return;
        }
        
        log("get data");
        String [] requestKeys=new String[]{"11","31","51","71","91","B1","D1","F1"};
        byte [] responseKeys =new byte[]{(byte)0x40,(byte)0x42,(byte)0x44,(byte)0x46,(byte)0x48,(byte)0x4A,(byte)0x4C,(byte)0x4E};
        
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int counter =1; // start 0x31
        int byteCount =0;
        while(byteCount < numberOfImages * 7229) {
            
            sendFrame(requestKeys[counter]);
            log(".");
            byte [] data = readFrame();
            if (data[1] != responseKeys[counter] || data[2] != 0x05){
                log("wrong frame");
            } else {
                for (int x =3 ; x < data.length - 2 ; x++){
                    imgh.write(data[x]);
                    byteCount++;
                }
                
            }
            
            counter = (counter +1) %8;
        }
        log("finished downloading");
    }
    
    public void log(String logMessage){
        //System.out.println(logMessage);
    }
}
