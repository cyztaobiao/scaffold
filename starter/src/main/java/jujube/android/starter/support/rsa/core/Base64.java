package jujube.android.starter.support.rsa.core;

import java.io.ByteArrayOutputStream;

public class Base64 {
    private static final Encoder encoder = new Base64Encoder();

    public Base64() {
    }

    public static byte[] decode(String var0) {
        int var1 = var0.length() / 4 * 3;
        ByteArrayOutputStream var2 = new ByteArrayOutputStream(var1);

        try {
            encoder.decode(var0, var2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return var2.toByteArray();
    }
}