package jujube.android.starter.support.rsa.core;

import java.io.IOException;
import java.io.OutputStream;

public class Base64Encoder implements Encoder {
    protected final byte[] encodingTable = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    protected byte padding = 61;
    protected final byte[] decodingTable = new byte[128];

    protected void initialiseDecodingTable() {
        int var1;
        for(var1 = 0; var1 < this.decodingTable.length; ++var1) {
            this.decodingTable[var1] = -1;
        }

        for(var1 = 0; var1 < this.encodingTable.length; ++var1) {
            this.decodingTable[this.encodingTable[var1]] = (byte)var1;
        }

    }

    public Base64Encoder() {
        this.initialiseDecodingTable();
    }

    private boolean ignore(char var1) {
        return var1 == '\n' || var1 == '\r' || var1 == '\t' || var1 == ' ';
    }

    public int decode(String var1, OutputStream var2) throws IOException {
        int var7 = 0;

        int var8;
        for(var8 = var1.length(); var8 > 0 && this.ignore(var1.charAt(var8 - 1)); --var8) {
        }

        if (var8 == 0) {
            return 0;
        } else {
            int var9 = 0;

            int var10;
            for(var10 = var8; var10 > 0 && var9 != 4; --var10) {
                if (!this.ignore(var1.charAt(var10 - 1))) {
                    ++var9;
                }
            }

            for(var9 = this.nextI((String)var1, 0, var10); var9 < var10; var9 = this.nextI(var1, var9, var10)) {
                byte var3 = this.decodingTable[var1.charAt(var9++)];
                var9 = this.nextI(var1, var9, var10);
                byte var4 = this.decodingTable[var1.charAt(var9++)];
                var9 = this.nextI(var1, var9, var10);
                byte var5 = this.decodingTable[var1.charAt(var9++)];
                var9 = this.nextI(var1, var9, var10);
                byte var6 = this.decodingTable[var1.charAt(var9++)];
                if ((var3 | var4 | var5 | var6) < 0) {
                    throw new IOException("invalid characters encountered in base64 data");
                }

                var2.write(var3 << 2 | var4 >> 4);
                var2.write(var4 << 4 | var5 >> 2);
                var2.write(var5 << 6 | var6);
                var7 += 3;
            }

            int var11 = this.nextI(var1, var9, var8);
            int var12 = this.nextI(var1, var11 + 1, var8);
            int var13 = this.nextI(var1, var12 + 1, var8);
            int var14 = this.nextI(var1, var13 + 1, var8);
            var7 += this.decodeLastBlock(var2, var1.charAt(var11), var1.charAt(var12), var1.charAt(var13), var1.charAt(var14));
            return var7;
        }
    }

    private int decodeLastBlock(OutputStream var1, char var2, char var3, char var4, char var5) throws IOException {
        byte var6;
        byte var7;
        if (var4 == this.padding) {
            if (var5 != this.padding) {
                throw new IOException("invalid characters encountered at end of base64 data");
            } else {
                var6 = this.decodingTable[var2];
                var7 = this.decodingTable[var3];
                if ((var6 | var7) < 0) {
                    throw new IOException("invalid characters encountered at end of base64 data");
                } else {
                    var1.write(var6 << 2 | var7 >> 4);
                    return 1;
                }
            }
        } else {
            byte var8;
            if (var5 == this.padding) {
                var6 = this.decodingTable[var2];
                var7 = this.decodingTable[var3];
                var8 = this.decodingTable[var4];
                if ((var6 | var7 | var8) < 0) {
                    throw new IOException("invalid characters encountered at end of base64 data");
                } else {
                    var1.write(var6 << 2 | var7 >> 4);
                    var1.write(var7 << 4 | var8 >> 2);
                    return 2;
                }
            } else {
                var6 = this.decodingTable[var2];
                var7 = this.decodingTable[var3];
                var8 = this.decodingTable[var4];
                byte var9 = this.decodingTable[var5];
                if ((var6 | var7 | var8 | var9) < 0) {
                    throw new IOException("invalid characters encountered at end of base64 data");
                } else {
                    var1.write(var6 << 2 | var7 >> 4);
                    var1.write(var7 << 4 | var8 >> 2);
                    var1.write(var8 << 6 | var9);
                    return 3;
                }
            }
        }
    }

    private int nextI(String var1, int var2, int var3) {
        while(var2 < var3 && this.ignore(var1.charAt(var2))) {
            ++var2;
        }

        return var2;
    }
}