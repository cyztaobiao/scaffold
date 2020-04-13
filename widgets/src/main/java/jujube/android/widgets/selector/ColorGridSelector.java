package jujube.android.widgets.selector;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.ImageView;

import jujube.android.widgets.R;

public class ColorGridSelector extends GridLayout {

    public interface OnColorSelectedListener {
        void onColorSelected(Drawable newDrawable, int newColor);
    }

    private OnColorSelectedListener colorSelectedListener;

    public void setColorSelectedListener(OnColorSelectedListener colorSelectedListener) {
        this.colorSelectedListener = colorSelectedListener;
    }

    public ColorGridSelector(Context context) {
        this(context, null);
    }

    public ColorGridSelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorGridSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int padding = getResources().getDimensionPixelOffset(R.dimen.margin_normal);
        setPadding(padding, padding, padding, padding);
        setColumnCount(4);
        repopulateItems();
        setScaleX(0.f);
        setScaleY(0.f);
        setAlpha(0.f);
        setVisibility(GONE);

    }

    private void repopulateItems() {

        String[] choicesString = getResources().getStringArray(R.array.color_choices);
        int[] colors = new int[16];
        for (int i = 0; i < colors.length; ++i) {
            colors[i] = Color.parseColor(choicesString[i]);
        }
        for (final int color : colors) {
            ImageView itemView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.item_color, this, false);
            final GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(color);

            itemView.setImageDrawable(drawable);

            itemView.setOnClickListener(v -> {
                if (colorSelectedListener != null) {
                    colorSelectedListener.onColorSelected(drawable, color);
                }
                hide();
            });

            addView(itemView);
        }
    }

    public void show() {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
            animate().scaleX(1.f).scaleY(1.f).alpha(1.f).setListener(null).start();
        }
    }

    public void hide() {
        if (getVisibility() != GONE) {
            ColorGridSelector.this.animate().scaleX(0.f).scaleY(0.f).alpha(0.f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }
    }

}
