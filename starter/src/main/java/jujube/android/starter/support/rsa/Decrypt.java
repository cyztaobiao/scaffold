package jujube.android.starter.support.rsa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import gov.zjch.uploader.support.rsa.core.PemObject;
import gov.zjch.uploader.support.rsa.core.PemReader;

public class Decrypt {

    public String getDecryptString(String cipherText) throws IOException, GeneralSecurityException {
        PrivateKey privateKey = getPrivateKey();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] cipherData = cipher.doFinal(Base64.getDecoder().decode(cipherText.getBytes()));
        return new String(cipherData);
    }

    private static PrivateKey getPrivateKey() throws IOException, GeneralSecurityException {
        BufferedReader privKeyPEM = getPrivateKeyFromPEMfile();

        return generatePrivateKeyFromPem(privKeyPEM);
    }

    private static BufferedReader getPrivateKeyFromPEMfile() throws IOException {
        return new BufferedReader(new FileReader("resources/private_key.pem"));
    }

    private static PrivateKey generatePrivateKeyFromPem(BufferedReader privateKeyPem) throws IOException, GeneralSecurityException {
        PemReader pp = new PemReader(privateKeyPem);

        PemObject pem = pp.readPemObject();
        byte[] content = pem.getContent();
        pp.close();

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(content);

        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePrivate(spec);
    }

}
