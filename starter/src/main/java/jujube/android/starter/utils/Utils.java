package jujube.android.starter.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Utils {
    public static <T> List<T> transformArray2List(T[] array) {
        ArrayList<T> arrayList = new ArrayList<T>(array.length);
        Collections.addAll(arrayList, array);
        return arrayList;
    }

    public static String uuid() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return  str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
    }
}
