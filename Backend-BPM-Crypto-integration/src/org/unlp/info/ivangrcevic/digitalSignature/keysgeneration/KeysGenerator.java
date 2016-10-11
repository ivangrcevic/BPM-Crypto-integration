package org.unlp.info.ivangrcevic.digitalSignature.keysgeneration;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.DatatypeConverter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

/**
 * Created by ivangrcevic on 8/10/16.
 */
public class KeysGenerator {

    private static String KEY_ALGORITHM = "RSA";
    private static String RANDOM_ALGORITHM = "SHA1PRNG";
    private static String EXCEPTION_ALGORITHM_TEXT = "No such algorithm or provider.";
    private static String EXCEPTION_PBE_TEXT = "Private Key PBE cipher related exception.";
    private static String PBE_ALGORITHM = "PBEWithSHA1AndDESede";

    private static final Random RANDOM = new SecureRandom();

    public static EncodedKeyPair generateKeyPair(String passwordForPrivateKey) {

        /*
        * Source:
        * https://docs.oracle.com/javase/tutorial/security/apisign/step2.html
        * */

        try {

            //get KeyGenerator and random instance and initialize the generator with the secure random
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom random = SecureRandom.getInstanceStrong();
            keyGen.initialize(1024, random);
            //generate the key pair
            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey priv = pair.getPrivate();
            PublicKey pub = pair.getPublic();
            //store keys in files
            byte[] protectedPKCS8prvtKey = protectPrivateKey(priv, passwordForPrivateKey);
            byte[] x509EncodedPublicKey = encodePublicKey(pub);
            return new EncodedKeyPair(protectedPKCS8prvtKey, x509EncodedPublicKey);

        } catch (NoSuchAlgorithmException e) {

            System.out.println("No such algorithm or provider '"+RANDOM_ALGORITHM+"'");
            e.printStackTrace();
            throw new RuntimeException(EXCEPTION_ALGORITHM_TEXT);

        }

    }

    public static void saveFile(byte[] protectedPKCS8prvtKey, String fileName, String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(path + "/"+fileName);
        fos.write(protectedPKCS8prvtKey);
        fos.close();
    }

    private static byte[] encodePublicKey(PublicKey publicKey) {
        /*
        * Source:
        * http://snipplr.com/view/18368/
        * */
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        return x509EncodedKeySpec.getEncoded();
    }

    private static byte[] protectPrivateKey(PrivateKey privateKey, String password) {
        /*
        * Source:
        * http://stackoverflow.com/questions/34386901/java-write-and-read-password-based-encrypted-private-key
        * Importante para hacer funcionar la protección de la clave privada con PBE, sino falla
        * esto pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec); por "IllegalKeySize".
        * http://stackoverflow.com/questions/6481627/java-security-illegal-key-size-or-default-parameters
        * */

        // unencrypted PKCS#8 private key (DER)
        byte[] encodedPrivateKey = privateKey.getEncoded();

        String base64 = DatatypeConverter.printBase64Binary(encodedPrivateKey);
        String lines = "";
        for (int i = 0; i < base64.length(); i = i + 64) {
            lines += base64.substring(i, (i+64)<= base64.length() ? (i+64) : base64.length())+ "\n";
        }
        String pemFormatted = "-----BEGIN RSA PRIVATE KEY-----\n" +
                lines+
                "\n-----END RSA PRIVATE KEY-----\n";

        return pemFormatted.getBytes();

//        try {
//            // create a random salt (16 bytes)
//            byte[] salt = new byte[8];
//            RANDOM.nextBytes(salt);
//
//            // create PBE key from password
//            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 20);
//            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
//            SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PBE_ALGORITHM);
//            SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);
//
//            // encrypt private key
//            Cipher pbeCipher = Cipher.getInstance(PBE_ALGORITHM);
//            pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
//
//            // Encrypt the encoded Private Key with the PBE key
//            byte[] cipherText = pbeCipher.doFinal(encodedPrivateKey);
//
//            // Now construct  PKCS #8 EncryptedPrivateKeyInfo object
//            AlgorithmParameters algparms = AlgorithmParameters.getInstance(PBE_ALGORITHM);
//            algparms.init(pbeParamSpec);
//            EncryptedPrivateKeyInfo encinfo = new EncryptedPrivateKeyInfo(algparms, cipherText);
//
//            // Encoded PKCS#8 encrypted key
//            byte[] encryptedPkcs8 = encinfo.getEncoded();
//
//            return encryptedPkcs8;
//
//        } catch (GeneralSecurityException | IOException e) {
//            System.out.println("Exception caught while trying to PBE protect the private key.");
//            e.printStackTrace();
//            throw new RuntimeException(EXCEPTION_PBE_TEXT);
//        }

    }
}
