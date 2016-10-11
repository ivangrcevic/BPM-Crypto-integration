package org.unlp.info.ivangrcevic.digitalSignature;

import org.unlp.info.ivangrcevic.digitalSignature.keysgeneration.*;
import org.unlp.info.ivangrcevic.digitalSignature.verifier.Verifier;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by ivangrcevic on 25/9/16.
 */
public class Main {

    public static void main (String[] args) {

        Calendar now = Calendar.getInstance();
        String suffix = "" + now.get(Calendar.YEAR) +"-"+ (now.get(Calendar.MONTH)+1) +"-"+ now.get(Calendar.DAY_OF_MONTH)
                +"_"+ now.get(Calendar.HOUR_OF_DAY) +"-"+ now.get(Calendar.MINUTE) +"-"+ now.get(Calendar.SECOND);
        String userID = "prueba"+ suffix;

        EncodedKeyPair keyPair = KeysGenerator.generateKeyPair("changeit");

        try {
            KeysGenerator.saveFile(keyPair.getProtectedPKCS8prvtKey(), userID + ".key", "../generatedKeys");
            KeysGenerator.saveFile(keyPair.getX509EncodedPublicKey(), userID + ".pub", "../generatedKeys");
        } catch (IOException e){
            System.out.println("IO Exception while saving key file. ");
        }

        //Verifier.verify(null, null);
    }

}
