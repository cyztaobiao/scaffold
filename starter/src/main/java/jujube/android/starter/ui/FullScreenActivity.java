package jujube.android.starter.ui;

import android.graphics.Color;
import android.os.Build;
import android.view.View;

import jujube.android.starter.mvvm.AbsViewModel;

public abstract class FullScreenActivity<T extends AbsViewModel> extends SoftKeyboardCompatActivity<T> {

    private static final int FULL_SCREEN_FLAG = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    private static final int FULL_SCREEN_NAVIGATION_FLAG = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    private static final int FULL_SCREEN_LIGHT_FLAG = FULL_SCREEN_FLAG | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

    private static final int FULL_SCREEN_LIGHT_NAVIGATION_FLAG = FULL_SCREEN_NAVIGATION_FLAG | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

    private boolean darkStatusBarEnabled = true;
    private boolean hideNavigation = true;

    public void setDarkStatusBarEnabled(boolean darkStatusBarEnabled) {
        this.darkStatusBarEnabled = darkStatusBarEnabled;
    }

    public void setHideNavigation(boolean hideNavigation) {
        this.hideNavigation = hideNavigation;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && darkStatusBarEnabled) {
            getWindow().getDecorView().setSystemUiVisibility(hideNavigation ? FULL_SCREEN_LIGHT_FLAG : FULL_SCREEN_LIGHT_NAVIGATION_FLAG);
        }else {
            getWindow().getDecorView().setSystemUiVisibility(hideNavigation ? FULL_SCREEN_FLAG : FULL_SCREEN_NAVIGATION_FLAG);
        }
    }


    @Override
    protected void activityConfig() {
        super.activityConfig();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && darkStatusBarEnabled) {
            getWindow().getDecorView().setSystemUiVisibility(hideNavigation ? FULL_SCREEN_LIGHT_FLAG : FULL_SCREEN_LIGHT_NAVIGATION_FLAG);
        }else {
            getWindow().getDecorView().setSystemUiVisibility(hideNavigation ? FULL_SCREEN_FLAG : FULL_SCREEN_NAVIGATION_FLAG);
        }
    }

    @Override
    protected void onKeyboardHidden() {
        super.onKeyboardHidden();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && darkStatusBarEnabled) {
            getWindow().getDecorView().setSystemUiVisibility(hideNavigation ? FULL_SCREEN_LIGHT_FLAG : FULL_SCREEN_LIGHT_NAVIGATION_FLAG);
        }else {
            getWindow().getDecorView().setSystemUiVisibility(hideNavigation ? FULL_SCREEN_FLAG : FULL_SCREEN_NAVIGATION_FLAG);
        }
    }
}
