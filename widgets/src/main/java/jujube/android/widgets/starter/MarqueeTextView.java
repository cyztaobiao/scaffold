package jujube.android.widgets.starter;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * <jujube.android.widgets.starter.MarqueeTextView
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:ellipsize="marquee"
 *         android:marqueeRepeatLimit ="marquee_forever"
 *         android:focusable="true"
 *         android:focusableInTouchMode="true"
 *         android:singleLine="true"/>
 */
public class MarqueeTextView extends AppCompatTextView {

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (focused)
            super.onFocusChanged(true, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if (focused)
            super.onWindowFocusChanged(true);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

}
