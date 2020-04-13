package jujube.android.starter.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ActivityUtils {

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId, String fName) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment, fName);
        transaction.commit();
    }

    public static void addFragmentToActivityStack(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId, String fName) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment, fName);
        transaction.addToBackStack(fName);
        transaction.commit();
    }

}
