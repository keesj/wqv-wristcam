package wristcam.comm;
import java.util.*;

public class ByteUtil {
    
    /** Creates a new instance of ByteUtil */
    public ByteUtil() {
    }
    
    public static String byteToString(byte b){
        return Integer.toString( ( b & 0xff ) + 0x100, 16 /* radix */ ) .substring( 1 ).toUpperCase();
    }
    
    public String byteArrayToString(byte[] bytes){
        return byteArrayToString(bytes,bytes.length);
        
    }
    public String byteArrayToString(byte[] bytes,int length){
        StringBuffer sb = new StringBuffer();
        
        for (int x =0 ; x < length ; x++){
            sb.append(byteToString(bytes[x]));
            if (x < length -1) sb.append(" ");
        }
        return sb.toString();
    }
    
    public byte[] stringToByteArray(String string){
        StringTokenizer st = new StringTokenizer(string," ");
        byte[] retval =new byte[st.countTokens()];
        int counter = 0;
        while(st.hasMoreTokens()){
            String byteString = st.nextToken();
            retval[counter] = stringToByte(byteString);
            counter++;
        }
        return retval;
    }
    
    public static byte stringToByte(String s){
        int value = Integer.parseInt(s.trim(),16 /* radix */);
        byte b = (byte)value;
        return b;
    }
    
}
