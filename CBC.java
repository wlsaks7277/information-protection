package ECB;

import java.security.*;
import javax.crypto.*;
import java.io.*;

public class ECB{
    public static void main(String[] args) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getinstance("DES");
        keyGenerator.init(new SecureRandom());
        SecretKey secretKey = keyGenerator.generateKey();

        FileInputSteram inFile = new FileInputStream("images.bmp");
        FileOutStream outFile = new FileOutputStream("CBC.bmp");

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.Encrypt_mode,secretKey);

        byte[] input = new byte[64];
        int bytesRead;

        while((bytesRead = inFile.Read(input))!=-1){
            byte[] output=cipher.update(input,0,bytesRead);
            if(output!=null) outFile.write(output);        
        }

        byte[] output = cipher.doFinal();
        if(output !=null) outFile.write(output);
        inFile.close();
        outFile.flush();
        outFile.close();
    }
}