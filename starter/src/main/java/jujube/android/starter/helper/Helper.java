package jujube.android.starter.helper;

import java.util.UUID;

public class Helper {

    public static String uuid() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return  str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
    }

}
