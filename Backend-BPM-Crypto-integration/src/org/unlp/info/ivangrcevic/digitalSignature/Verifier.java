package org.unlp.info.ivangrcevic.digitalSignature;

import java.security.*;

/**
 * Created by ivangrcevic on 23/9/16.
 */
public class Verifier {

    static String ALGORITHM = "SHA1withRSA";
    private static String EXCEPTION_TEXT = "Error verifying sinature";

    public static boolean verify(PublicKey publicKey, byte[] sigToVerify) {
        try {

            Signature signature = Signature.getInstance(ALGORITHM);
            signature.initVerify(publicKey);
            return signature.verify(sigToVerify);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("No such algorithm '"+ALGORITHM+"'");
            e.printStackTrace();
            throw new RuntimeException(EXCEPTION_TEXT);
        } catch (InvalidKeyException e) {
            System.out.println("Invalid public key '"+publicKey.toString()+"'");
            e.printStackTrace();
            throw new RuntimeException(EXCEPTION_TEXT);
        } catch (SignatureException e) {
            System.out.println("Exception caught while verifying signature");
            e.printStackTrace();
            throw new RuntimeException(EXCEPTION_TEXT);
        }
    }
}
