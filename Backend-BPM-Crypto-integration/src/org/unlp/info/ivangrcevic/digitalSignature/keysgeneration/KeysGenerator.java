package org.unlp.info.ivangrcevic.digitalSignature.keysgeneration;

import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemGenerationException;
import org.bouncycastle.util.io.pem.PemObject;


import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.util.Date;
import java.util.Random;

/**
 * Created by ivangrcevic on 8/10/16.
 */
public class KeysGenerator {

    private static String KEY_ALGORITHM = "RSA";
    private static String EXCEPTION_ALGORITHM_TEXT = "No such algorithm or provider.";
    private static String EXCEPTION_PBE_TEXT = "Private Key PBE cipher related exception.";

    private static final Random RANDOM = new SecureRandom();

    public static EncodedKeyPair generateKeyPair(String passwordForPrivateKey) {

        /*
        * Source:
        * https://docs.oracle.com/javase/tutorial/security/apisign/step2.html
        * */

        try {

            //get KeyGenerator and random instance and initialize the generator with the secure random
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM, "BC");
            SecureRandom random = SecureRandom.getInstanceStrong();
            keyGen.initialize(1024, random);
            //generate the key pair
            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey priv = pair.getPrivate();
            /*TODO: importante! usar una clave privada que provenga de la CA*/
            PrivateKey caPrivateKey = priv;
            //store keys in files
            PemObject protectedPKCS8prvtKey = protectPrivateKey(priv, passwordForPrivateKey);
            PemObject x509Cert = createCertificate(pair, caPrivateKey);
            return new EncodedKeyPair(protectedPKCS8prvtKey, x509Cert);

        } catch (NoSuchAlgorithmException | NoSuchProviderException | OperatorCreationException
                | IOException e) {

            System.out.println("No such algorithm or provider ");
            e.printStackTrace();
            throw new RuntimeException(EXCEPTION_ALGORITHM_TEXT);

        }

    }

    public static void saveToFile(PemObject pemObject, String fileName, String path) throws IOException {
        JcaPEMWriter pemWrt = new JcaPEMWriter(new FileWriter(path +"/"+ fileName));
        pemWrt.writeObject(pemObject);
        pemWrt.close();
    }

    private static PemObject createCertificate(KeyPair keyPair, PrivateKey caPrivatekey) throws OperatorCreationException, IOException {
        /*
        * Source:
        * http://www.bouncycastle.org/wiki/display/JA1/BC+Version+2+APIs#BCVersion2APIs-ASimpleOperatorExample
        * http://stackoverflow.com/questions/12466489/generating-x-509-certificate-using-bouncy-castle-java-api
        * http://stackoverflow.com/questions/10040977/unable-to-write-csr-generated-using-org-bouncycastle-asn1-pkcs-certificationrequ
        * */
        ContentSigner sigGen = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(caPrivatekey);
        SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(ASN1TaggedObject.fromByteArray(keyPair.getPublic().getEncoded()));
        Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);
        X509v1CertificateBuilder certBuilder = new X509v1CertificateBuilder(
                new X500Name("CN=Test"),
                BigInteger.ONE,
                startDate, endDate,
                new X500Name("CN=Test"),
                subPubKeyInfo
        );
        X509CertificateHolder certHolder = certBuilder.build(sigGen);
        //para leerlo X509CertificateHolder(obj.getContent())
        return new PemObject("CERTIFICATE", certHolder.getEncoded());
    }

    private static PemObject protectPrivateKey(PrivateKey privateKey, String password) throws NoSuchAlgorithmException, OperatorCreationException, PemGenerationException {
        /*
        * Source:
        * http://stackoverflow.com/questions/14597371/encrypt-a-private-key-with-password-using-bouncycastle#
        * */

        JceOpenSSLPKCS8EncryptorBuilder encryptorBuilder = new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.PBE_SHA1_3DES);
        encryptorBuilder.setRandom(SecureRandom.getInstanceStrong());
        encryptorBuilder.setPasssword(password.toCharArray());
        OutputEncryptor outputEncryptor = encryptorBuilder.build();
        JcaPKCS8Generator gen = new JcaPKCS8Generator(privateKey,outputEncryptor);
        return gen.generate();

    }
}
