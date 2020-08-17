package jujube.android.starter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ContextUtils {

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    public static boolean checkAppPermission(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        boolean granted = false;
        for (String permission : permissions) {
            granted = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return granted;
    }

    public static void copyAssets(Context context, String asset_filename, String target_directory) throws Exception {
        AssetManager assetManager = context.getAssets();

        InputStream source = assetManager.open(new File(asset_filename).getPath());
        File destinationFile = new File(target_directory, asset_filename);

        if(destinationFile.exists()){
            return;
        }

        destinationFile.getParentFile().mkdirs();
        OutputStream destination = new FileOutputStream(destinationFile);
        byte[] buffer = new byte[1024];
        int nread;

        while ((nread = source.read(buffer)) != -1) {
            if (nread == 0) {
                nread = source.read();
                if (nread < 0)
                    break;
                destination.write(nread);
                continue;
            }
            destination.write(buffer, 0, nread);
        }
        destination.close();
    }

    public static Drawable getSelectableItemBackground(Activity activity) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activity.getTheme();
        theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
        int[] attribute = new int[]{android.R.attr.selectableItemBackground};
        TypedArray typedArray = theme.obtainStyledAttributes(typedValue.resourceId, attribute);
        return typedArray.getDrawable(0);
    }

    public static Drawable getSelectableItemBackgroundBorderless(Activity activity) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activity.getTheme();
        theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true);
        int[] attribute = new int[]{android.R.attr.selectableItemBackgroundBorderless};
        TypedArray typedArray = theme.obtainStyledAttributes(typedValue.resourceId, attribute);
        return typedArray.getDrawable(0);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static void hideSoftInput(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void showSoftInput(Activity activity, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return null != info && info.isConnected();
        }
        return false;
    }

    public static String getAppName(Context context) {
        String appName = "";
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appName = pi.applicationInfo.loadLabel(context.getPackageManager()).toString();
            if (appName.length() <= 0) {
                return "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }
}