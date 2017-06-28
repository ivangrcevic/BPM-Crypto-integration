package org.unlp.info.ivangrcevic.digitalSignature;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.unlp.info.ivangrcevic.digitalSignature.verifier.Verifier;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.security.PublicKey;
import java.security.Security;

/**
 * Created by ivangrcevic on 6/15/17.
 */
public class VerifierMain {

    static String signatureB64 = "jIDtp58tw6sJG+4IvATSTJb0/tXaDwxbja46zx+TwthxsIH/MuGq8hoWoMDWx0ZGRuBGFyYXRKhP4SL0sH1UzyAj+OU+zGOpSFzt+oV8CifZZmErfXT7zB8f1LlFYi7rG5CrRcX6kZY0XunUFCf8Rss4IFAF0wtVQJYUdSwY9+U=";
    static String textToVerify = "Hola probando";


    public static void main (String[] args) {

        try{

            Security.addProvider(new BouncyCastleProvider());

            PublicKey publicKey = Verifier.loadPublicKey("../generatedKeys/prueba2017-6-15_17-39-7.pem");

            /*Path fileLocation = Paths.get("../generatedKeys/signature");
            byte[] signature = Files.readAllBytes(fileLocation);*/
            byte[] signature = Base64.decode(signatureB64);

            boolean result = Verifier.verify(publicKey, signature, textToVerify);

            if (result) {
                System.out.println("Verificada OK!     :)");
            } else {
                System.out.println("Falló verificación :(");
            }

        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        } catch (Base64DecodingException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }

}
