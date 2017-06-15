package org.unlp.info.ivangrcevic.digitalSignature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Strings;
import org.unlp.info.ivangrcevic.digitalSignature.keysgeneration.KeysGenerator;
import org.unlp.info.ivangrcevic.digitalSignature.verifier.Verifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.Security;

/**
 * Created by ivangrcevic on 6/15/17.
 */
public class VerifierMain {

    static String signatureString = "FãÍ¨\u009Fý4\u0007£¨æo´õM\u001B[MµÿÓùp\\\u0018LØÝ=\u0092lÒ\u0098~\u008A\u0085\u001BÊÝ¨\u008B\u001BÃ 7 ¾º\u008Dµ\u0003>þ\u0001\u0089cQÈ\u0097Ü\u0091ì²ª8\u001D\bñ§¨\u000F(zuóö¤Æu[\n" +
            "¤\u001Bá½\\ÙJ+\u007F´;+\u0089úrb7\u008A9%P4á\u001D\u0096\u0084³\u008Fb8Þx@:ms\u0081±m\t\u008E\u0001ë\u001DPøÉ";
    static String textToVerify = "Hola como estás?";


    public static void main (String[] args) {

        try{

            Security.addProvider(new BouncyCastleProvider());

            PublicKey publicKey = Verifier.loadPublicKey("../generatedKeys/prueba2017-6-15_17-39-7.pem");

            /*Path fileLocation = Paths.get("../generatedKeys/signature");
            byte[] signature = Files.readAllBytes(fileLocation);*/
            byte[] signature = signatureString.getBytes();

            boolean result = Verifier.verify(publicKey, signature, textToVerify);

            if (result) {
                System.out.println("Verificada OK!     :)");
            } else {
                System.out.println("Falló verificación :(");
            }

        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }

}
