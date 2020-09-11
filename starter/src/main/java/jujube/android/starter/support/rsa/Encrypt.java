package jujube.android.starter.support.rsa;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import gov.zjch.uploader.support.rsa.core.PemObject;
import gov.zjch.uploader.support.rsa.core.PemReader;

public class Encrypt {

    public static String encryptRsa(String painText, String key) throws Exception{
        PublicKey publicKey = getPublicKey(key);

        // 加密方式，标准jdk的，Android默认RSA/None/NoPadding
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");

        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] cipherData = cipher.doFinal(painText.getBytes());
        return Base64.encodeToString(cipherData, Base64.NO_WRAP);
    }

    private static BufferedReader getPublicKeyFromPEMfile() throws IOException {
        return new BufferedReader(new FileReader("resources/publicKey.pem"));
    }

    private static PublicKey generatePublickeyFromPem(BufferedReader publicKeyPEM) throws IOException, GeneralSecurityException {
        PemReader pp = new PemReader(publicKeyPEM);
        PemObject pem = pp.readPemObject();
        byte[] content = pem.getContent();
        pp.close();

        X509EncodedKeySpec spec = new X509EncodedKeySpec(content);

        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePublic(spec);
    }

    private static PublicKey getPublicKey() throws IOException, GeneralSecurityException {
        BufferedReader publicKeyPEM = getPublicKeyFromPEMfile();
        return generatePublickeyFromPem(publicKeyPEM);
    }

    private static PublicKey getPublicKey(String key) throws IOException, GeneralSecurityException {
        BufferedReader publicKeyPEM = new BufferedReader(new StringReader(key));
        return generatePublickeyFromPem(publicKeyPEM);
    }
}