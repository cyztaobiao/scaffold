package jujube.android.widgets.phoenix.refresh_drawable;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import jujube.android.widgets.phoenix.PullToRefreshLayout;

/**
 * Created by tb on 2017/9/22.
 */

public abstract class BaseRefreshDrawable extends Drawable implements Drawable.Callback, Animatable{

	protected PullToRefreshLayout mParent;

	public BaseRefreshDrawable(PullToRefreshLayout mRefreshLayout) {
		this.mParent = mRefreshLayout;
	}

	public Context getContext() {
		return mParent != null ? mParent.getContext() : null;
	}

	public abstract void setPercent(float percent, boolean invalidate);

	public abstract void offsetTopAndBottom(int offset);

	@Override
	public void invalidateDrawable(@NonNull Drawable who) {
		final Callback callback = getCallback();
		if (callback != null)
			callback.invalidateDrawable(this);
	}

	@Override
	public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
		final Callback callback  = getCallback();
		if (callback != null) {
			callback.scheduleDrawable(this, what, when);
		}
	}

	@Override
	public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
		final Callback callback = getCallback();
		if (callback != null)
			callback.unscheduleDrawable(this, what);
	}

	@Override
	public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

	}

	@Override
	public void setColorFilter(@Nullable ColorFilter colorFilter) {

	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}
}
