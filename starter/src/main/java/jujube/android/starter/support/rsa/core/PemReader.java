package jujube.android.starter.support.rsa.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class PemReader extends BufferedReader {
    private static final String BEGIN = "-----BEGIN ";
    private static final String END = "-----END ";

    public PemReader(Reader var1) {
        super(var1);
    }

    public PemObject readPemObject() throws IOException {
        String var1;
        for (var1 = this.readLine(); var1 != null && !var1.startsWith(BEGIN); var1 = this.readLine()) {
        }

        if (var1 != null) {
            var1 = var1.substring(BEGIN.length());
            int var2 = var1.indexOf(45);
            String var3 = var1.substring(0, var2);
            if (var2 > 0) {
                return this.loadObject(var3);
            }
        }

        return null;
    }

    private PemObject loadObject(String var1) throws IOException {
        String var3 = END + var1;
        StringBuffer content = new StringBuffer();
        String var2;

        while ((var2 = this.readLine()) != null) {
            if (var2.contains(var3)) {
                break;
            }
            content.append(var2.trim());
        }

        if (var2 == null) {
            throw new IOException(var3 + " not found");
        } else {
            return new PemObject(Base64.decode(content.toString()));
        }
    }
}