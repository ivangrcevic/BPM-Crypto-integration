package org.unlp.info.ivangrcevic.digitalSignature.keysgeneration;

/**
 * Created by ivangrcevic on 8/10/16.
 */
public class EncodedKeyPair {

    private byte[] protectedPKCS8prvtKey;
    private byte[] x509EncodedPublicKey;

    public byte[] getProtectedPKCS8prvtKey() {
        return protectedPKCS8prvtKey;
    }
    public byte[] getX509EncodedPublicKey() {
        return x509EncodedPublicKey;
    }
    public EncodedKeyPair(byte[] protectedPKCS8prvtKey, byte[] x509EncodedPublicKey) {
        this.protectedPKCS8prvtKey = protectedPKCS8prvtKey;
        this.x509EncodedPublicKey = x509EncodedPublicKey;
    }

}
