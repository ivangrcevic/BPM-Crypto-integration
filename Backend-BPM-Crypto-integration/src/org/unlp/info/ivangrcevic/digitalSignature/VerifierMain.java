package org.unlp.info.ivangrcevic.digitalSignature;

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

/**
 * Created by ivangrcevic on 6/15/17.
 */
public class VerifierMain {

    public static void main (String[] args) {

        try{
            PublicKey publicKey = Verifier.loadPublicKey("../generatedKeys/prueba20170615.pem");
            Path fileLocation = Paths.get("../generatedKeys/signature");
            byte[] signature = Files.readAllBytes(fileLocation);
            Verifier.verify(publicKey, signature);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }

}
