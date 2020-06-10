package jujube.android.widgets.slidemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;

import java.lang.reflect.Field;

public class SlidingMenu extends HorizontalScrollView {

    private View mLeftMenu;
    private View mContent;
    private View mRightMenu;

    private int mLeftMenuWidth;
    private int mContentWidth;
    private int mRightMenuWidth;
    private int mMenuPadding = 72;

    private boolean once = false;
    private boolean isLeftOpen;
    private boolean isRightOpen;

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager vm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        vm.getDefaultDisplay().getMetrics(outMetrics);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!once) {
            ViewGroup wrapper = (ViewGroup) getChildAt(0);
            mLeftMenu = wrapper.getChildAt(0);
            mContent = wrapper.getChildAt(1);
            mRightMenu = wrapper.getChildAt(2);

            mLeftMenuWidth = mLeftMenu.getMeasuredWidth();
            mContentWidth = mContent.getLayoutParams().width = getMeasuredWidth();
            mRightMenuWidth = mRightMenu.getMeasuredWidth();

            once = true;
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(mLeftMenuWidth, 0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int[] location = new int[2];
        mContent.getLocationOnScreen(location);
        int x = location[0];
        if (ev.getRawX() > (x + (mLeftMenuWidth == 0 ? 0 : mMenuPadding)) &&
                ev.getRawX() < (x + mContentWidth - (mRightMenuWidth == 0 ? 0 : mMenuPadding))) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            int scrollX = getScrollX();
            if (scrollX >= (mLeftMenuWidth + mRightMenuWidth / 2)) {
                this.smoothScrollTo(mLeftMenuWidth + mRightMenuWidth, 0);
                isLeftOpen = false;
                isRightOpen = true;
            }else if (scrollX >= mLeftMenuWidth / 2){
                this.smoothScrollTo(mLeftMenuWidth, 0);
                isLeftOpen = false;
                isRightOpen = false;
            }else {
                this.smoothScrollTo(0, 0);
                isLeftOpen = true;
                isRightOpen = false;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 打开菜单
     */
    public void openLeftMenu() {
        if (isLeftOpen) return;
        this.smoothScrollTo(0, 0);
        isLeftOpen = true;
        isRightOpen = false;
    }

    public void openRight() {
        if (isRightOpen) return;
        this.smoothScrollTo(mLeftMenuWidth + mRightMenuWidth, 0);
        isRightOpen = true;
        isLeftOpen = false;
    }

    public void closeMenu() {
        if (!isLeftOpen && !isRightOpen) return;
        this.smoothScrollTo(mLeftMenuWidth, 0);
        isLeftOpen = false;
        isRightOpen = false;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * @return true表示ScrollView已经停止滑动，否则正在滑动中
     */
    public boolean isScrollOver() {
        try {
            Field mScroller = getClass().getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Object object = mScroller.get(this);
            if (object instanceof OverScroller) {
                OverScroller overScroller = (OverScroller) object;
                return overScroller.isFinished();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }
}