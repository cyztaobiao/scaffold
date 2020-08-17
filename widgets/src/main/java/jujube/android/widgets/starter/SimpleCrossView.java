package jujube.android.widgets.starter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import jujube.android.widgets.R;

public class SimpleCrossView extends View {

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int crossColor;

    public SimpleCrossView(Context context) {
        super(context);
        getValue(context, null);
    }

    public SimpleCrossView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getValue(context, attrs);
    }

    public SimpleCrossView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getValue(context, attrs);
    }

    private void getValue(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleCrossView);
        crossColor = typedArray.getColor(R.styleable.SimpleCrossView_crossColor, Color.RED);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        paint.setColor(crossColor);
        paint.setStrokeWidth(4.f);
        canvas.drawLine(w / 2.f, 0, w / 2.f, h, paint);
        canvas.drawLine(0, h / 2.f, w, h / 2.f, paint);
    }
}
