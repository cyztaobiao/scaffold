package jujube.android.starter.support.rsa.core;

public class PemObject {
    private byte[] content;

    public PemObject(byte[] var3) {
        this.content = var3;
    }
    public byte[] getContent() {
        return this.content;
    }
}