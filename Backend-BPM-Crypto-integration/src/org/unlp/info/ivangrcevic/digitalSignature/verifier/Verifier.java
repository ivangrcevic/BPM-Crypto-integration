package org.unlp.info.ivangrcevic.digitalSignature.verifier;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;

import java.io.*;
import java.security.*;

/**
 * Created by ivangrcevic on 23/9/16.
 */
public class Verifier {

    /*
    * https://stackoverflow.com/questions/11787571/how-to-read-pem-file-to-get-private-and-public-key
    * */

    static String ALGORITHM = "SHA1withRSA";
    private static String EXCEPTION_TEXT = "Error verifying sinature";

    public static PublicKey loadPublicKey (String fileName) throws IOException {
        File initialFile = new File(fileName);
        InputStream res = new FileInputStream(initialFile);
        Reader fRd = new BufferedReader(new InputStreamReader(res));
        PEMParser pemParser = new PEMParser(fRd);
        X509CertificateHolder certificateHolder = (X509CertificateHolder)pemParser.readObject();
        SubjectPublicKeyInfo publicKeyInfo = certificateHolder.getSubjectPublicKeyInfo();

        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        return converter.getPublicKey(publicKeyInfo);
    }

    public static boolean verify(PublicKey publicKey, byte[] sigToVerify, String textToVerify) {
        try {

            Signature signature = Signature.getInstance(ALGORITHM);
            signature.initVerify(publicKey);

            /*FileInputStream datafis = new FileInputStream(args[2]);
            BufferedInputStream bufin = new BufferedInputStream(datafis);*/
            BufferedInputStream bufin = new BufferedInputStream(new ByteArrayInputStream(textToVerify.getBytes()));

            byte[] buffer = new byte[1024];
            int len;
            while (bufin.available() != 0) {
                len = bufin.read(buffer);
                signature.update(buffer, 0, len);
            }
            bufin.close();

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
        } catch (IOException e) {
            System.out.println("Exception caught reading text to verify");
            e.printStackTrace();
            throw new RuntimeException(EXCEPTION_TEXT);
        }
    }
}
