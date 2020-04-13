package jujube.android.starter.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static <T> List<T> transformArray2List(T[] array) {
        ArrayList<T> arrayList = new ArrayList<T>(array.length);
        Collections.addAll(arrayList, array);
        return arrayList;
    }
}
