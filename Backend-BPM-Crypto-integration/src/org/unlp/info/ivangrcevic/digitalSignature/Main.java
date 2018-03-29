package org.unlp.info.ivangrcevic.digitalSignature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.unlp.info.ivangrcevic.digitalSignature.keysgeneration.*;

import java.io.IOException;
import java.security.Security;
import java.util.Calendar;

/**
 * Created by ivangrcevic on 25/9/16.
 */
public class Main {

    public static void main (String[] args) {

        Security.addProvider(new BouncyCastleProvider());

        Calendar now = Calendar.getInstance();
        String suffix = "" + now.get(Calendar.YEAR) +"-"+ (now.get(Calendar.MONTH)+1) +"-"+ now.get(Calendar.DAY_OF_MONTH)
                +"_"+ now.get(Calendar.HOUR_OF_DAY) +"-"+ now.get(Calendar.MINUTE) +"-"+ now.get(Calendar.SECOND);
        String userID = "prueba"+ suffix;

        EncodedKeyPair keyPair = KeysGenerator.generateKeyPair("Test", "changeit");

        try {
            //http://serverfault.com/questions/9708/what-is-a-pem-file-and-how-does-it-differ-from-other-openssl-generated-key-file
            KeysGenerator.saveToFile(keyPair.getProtectedPKCS8prvtKey(), userID + ".key", "../generatedKeys");
            KeysGenerator.saveToFile(keyPair.getX509EncodedCertificate(), userID + ".pem", "../generatedKeys");
            /* Para verificar:
             * https://kb.wisc.edu/middleware/page.php?id=4064
             */
        } catch (IOException e){
            System.out.println("IO Exception while saving key file. ");
        }

        //Verifier.verify(null, null);
    }

}
