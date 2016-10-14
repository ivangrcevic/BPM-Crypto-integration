package org.unlp.info.ivangrcevic.digitalSignature.keysgeneration;

import org.bouncycastle.util.io.pem.PemObject;

/**
 * Created by ivangrcevic on 8/10/16.
 */
public class EncodedKeyPair {

    private PemObject protectedPKCS8prvtKey;
    private PemObject x509EncodedCertificate;

    public PemObject getProtectedPKCS8prvtKey() {
        return protectedPKCS8prvtKey;
    }
    public PemObject getX509EncodedCertificate() {
        return x509EncodedCertificate;
    }
    public EncodedKeyPair(PemObject protectedPKCS8prvtKey, PemObject x509EncodedCertificate) {
        this.protectedPKCS8prvtKey = protectedPKCS8prvtKey;
        this.x509EncodedCertificate = x509EncodedCertificate;
    }

}
