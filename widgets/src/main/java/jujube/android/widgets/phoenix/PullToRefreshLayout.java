package jujube.android.widgets.phoenix;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;

import androidx.core.view.ViewCompat;

import jujube.android.widgets.Utils;
import jujube.android.widgets.phoenix.refresh_drawable.BaseRefreshDrawable;
import jujube.android.widgets.phoenix.refresh_drawable.SunRefreshDrawable;

/**
 * Created by tb on 2017/9/21.
 * https://github.com/Yalantis/Phoenix
 */

public class PullToRefreshLayout extends ViewGroup{

	private static final int DRAG_MAX_DISTANCE = 120;
	private static final float DRAG_RATE = .5f;
	private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

	public static final int STYLE_SUN = 0;
	public static final int MAX_OFFSET_ANIMATION_DURATION = 700;

	private static final int INVALID_POINTER = -1;

	private View mTarget;
	private ImageView mRefreshView;
	private BaseRefreshDrawable mRefreshDrawable;
	private Interpolator mDecelerateInterpolator;
	private int mTouchSlop;
	private int mTotalDragDistance;
	private float mCurrentDragPercent;
	private int mCurrentOffsetTop;
	private boolean mRefreshing;
	private int mActivePointerId;
	private boolean mIsBeingDragged;
	private float mInitialMotionY;
	private int mFrom;
	private float mFromDragPercent;
	private boolean mNotify;
	private OnRefreshListener mListener;

	private int mTargetPaddingTop;
	private int mTargetPaddingBottom;
	private int mTargetPaddingRight;
	private int mTargetPaddingLeft;

	public int getTotalDragDistance() {
		return mTotalDragDistance;
	}

	private final Animation mAnimateToStartPosition = new Animation() {
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			moveToStart(interpolatedTime);
		}
	};

	private void moveToStart(float interpolatedTime) {
		int targetTop = mFrom - (int) (mFrom * interpolatedTime);
		float targetPercent = mFromDragPercent * (1.0f - interpolatedTime);
		int offset = targetTop - mTarget.getTop();

		mCurrentDragPercent = targetPercent;
		mRefreshDrawable.setPercent(mCurrentDragPercent, true);
		mTarget.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTargetPaddingBottom + targetTop);
		setTargetOffsetTop(offset, false);
	}

	private Animation.AnimationListener mToStartListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mRefreshDrawable.stop();
			mCurrentOffsetTop = mTarget.getTop();
		}
	};

	private final Animation mAnimateToCorrectPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			int targetTop;
			int endTarget = mTotalDragDistance;
			targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
			int offset = targetTop - mTarget.getTop();

			mCurrentDragPercent = mFromDragPercent - (mFromDragPercent - 1.0f) * interpolatedTime;
			mRefreshDrawable.setPercent(mCurrentDragPercent, true);
			mTarget.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTargetPaddingBottom + targetTop);
			setTargetOffsetTop(offset, false /* requires update */);
		}
	};

	private Animation.AnimationListener mToCorrectListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (mNotify) {
				if (mListener != null)
					mListener.onRefresh();
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	};

	private void animateOffsetToStartPosition() {
		mFrom = mCurrentOffsetTop;
		mFromDragPercent = mCurrentDragPercent;
		long animationDuration = Math.abs((long) (MAX_OFFSET_ANIMATION_DURATION * mFromDragPercent));

		mAnimateToStartPosition.reset();
		mAnimateToStartPosition.setDuration(animationDuration);
		mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
		mAnimateToStartPosition.setAnimationListener(mToStartListener);
		mTarget.startAnimation(mAnimateToStartPosition);
	}

	private void animateOffsetToCorrectPosition() {
		mFrom = mCurrentOffsetTop;
		mFromDragPercent = mCurrentDragPercent;

		mAnimateToCorrectPosition.reset();
		mAnimateToCorrectPosition.setDuration(MAX_OFFSET_ANIMATION_DURATION);
		mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
		mAnimateToCorrectPosition.setAnimationListener(mToCorrectListener);
		mTarget.startAnimation(mAnimateToCorrectPosition);

		mRefreshDrawable.start();
		mCurrentOffsetTop = mTarget.getTop();
		mTarget.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTotalDragDistance);
	}

	public void setRefreshing(boolean refreshing) {
		setRefreshing(refreshing, false);
	}

	private void setRefreshing(boolean refreshing, final boolean notify) {
		if (mRefreshing != refreshing) {
			mNotify = notify;
			ensureTarget();
			mRefreshing = refreshing;
			if (mRefreshing) {
				mRefreshDrawable.setPercent(1f, true);
				animateOffsetToCorrectPosition();
			}else {
				animateOffsetToStartPosition();
			}
		}
	}

	public PullToRefreshLayout(Context context) {
		this(context, null);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mTotalDragDistance = Utils.convertDpToPixel(context, DRAG_MAX_DISTANCE);

		mRefreshView = new ImageView(context);
		mRefreshDrawable = new SunRefreshDrawable(this);
		mRefreshView.setImageDrawable(mRefreshDrawable);

		addView(mRefreshView);
		setWillNotDraw(true);
		setChildrenDrawingOrderEnabled(true);
	}



	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		ensureTarget();
		if (mTarget == null)
			return;

		widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - mTargetPaddingLeft - mTargetPaddingRight, MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - mTargetPaddingTop - mTargetPaddingBottom, MeasureSpec.EXACTLY);
		mTarget.measure(widthMeasureSpec, heightMeasureSpec);
		mRefreshView.measure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		ensureTarget();
		if (mTarget == null)
			return;

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		int left = getPaddingLeft();
		int top = getPaddingTop();
		int right = getPaddingRight();
		int bottom = getPaddingBottom();


		mTarget.layout(left, top + mCurrentOffsetTop, left + width - right, top + height - bottom + mCurrentOffsetTop);
		mRefreshView.layout(left, top, left + width - right, top + height - bottom);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!isEnabled() || canChildScrollUp() || mRefreshing)
			return false;

		final int action = ev.getAction();

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				setTargetOffsetTop(0, true);
				mActivePointerId = ev.getPointerId(0);
				mIsBeingDragged = false;
				final float initialMotionY = getMotionEventYByIndex(ev);
				if (initialMotionY == -1)
					return false;
				mInitialMotionY = initialMotionY;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mActivePointerId == INVALID_POINTER)
					return false;
				final float y = getMotionEventYByIndex(ev);
				if (y == -1)
					return false;
				final float yDiff = y - mInitialMotionY;
				if (yDiff > mTouchSlop && !mIsBeingDragged)
					mIsBeingDragged = true;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mIsBeingDragged = false;
				mActivePointerId = INVALID_POINTER;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				onSecondPointerUp(ev);
				break;
		}

		return mIsBeingDragged;
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!mIsBeingDragged)
			return super.onTouchEvent(ev);

		final int action = ev.getAction();

		switch (action) {
			case MotionEvent.ACTION_MOVE: {
				final int pointerIndex = ev.findPointerIndex(mActivePointerId);
				if (pointerIndex < 0)
					return false;

				final float y = getMotionEventYByIndex(ev);
				final float yDiff = y - mInitialMotionY;
				final float scrollTop = yDiff * DRAG_RATE;
				mCurrentDragPercent = scrollTop / mTotalDragDistance;
				if (mCurrentDragPercent < 0)
					return false;

				float boundedDragPercent = Math.min(1f, Math.abs(mCurrentDragPercent));
				float extraOs = Math.abs(scrollTop) - mTotalDragDistance;
				float slingshotDist = mTotalDragDistance;
				float tensionSlingshotPercent = Math.max(0, Math.min(extraOs, slingshotDist * 2) / slingshotDist);
				float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
				float extraMove = (slingshotDist) * tensionPercent / 2;
				int targetY = (int) ((slingshotDist * boundedDragPercent) + extraMove);

				mRefreshDrawable.setPercent(mCurrentDragPercent, true);
				setTargetOffsetTop(targetY - mCurrentOffsetTop, true);
				break;
			}
			case MotionEvent.ACTION_POINTER_DOWN:
				final int index = ev.getActionIndex();
				mActivePointerId = ev.getPointerId(index);
				break;
			case MotionEvent.ACTION_POINTER_UP:
				onSecondPointerUp(ev);
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL: {
				performClick();
				if (mActivePointerId == INVALID_POINTER)
					return false;

				final float y = getMotionEventYByIndex(ev);
				final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
				mIsBeingDragged = false;
				if (overScrollTop > mTotalDragDistance) {
					setRefreshing(true, true);
				}else {
					mRefreshing = false;
					animateOffsetToStartPosition();
				}
				mActivePointerId = INVALID_POINTER;
				return false;
			}
		}

		return true;
	}

	private boolean canChildScrollUp() {
		if (Build.VERSION.SDK_INT < 14) {
			if (mTarget instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) mTarget;
				return absListView.getChildCount() > 0
						&& (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
			}else {
				return mTarget.getScrollY() > 0;
			}
		}else {
			return ViewCompat.canScrollVertically(mTarget, -1);
		}
	}

	private void ensureTarget() {
		if (mTarget != null)
			return;
		if (getChildCount() > 1) {
			for (int i = 0; i < getChildCount(); ++i) {
				View child = getChildAt(i);
				if (child != mRefreshView) {
					mTarget = child;
					mTargetPaddingLeft = mTarget.getPaddingLeft();
					mTargetPaddingTop = mTarget.getPaddingTop();
					mTargetPaddingRight = mTarget.getPaddingRight();
					mTargetPaddingBottom = mTarget.getPaddingBottom();
				}
			}
		}
	}

	private void setTargetOffsetTop(int offset, boolean requireUpdate) {
		mTarget.offsetTopAndBottom(offset);
		mRefreshDrawable.offsetTopAndBottom(offset);
		mCurrentOffsetTop = mTarget.getTop();
		if (requireUpdate && Build.VERSION.SDK_INT < 11)
			invalidate();
	}

	private float getMotionEventYByIndex(MotionEvent ev) {
		int index = ev.findPointerIndex(mActivePointerId);
		if (index < 0)
			return -1;
		return ev.getY(index);
	}

	private void onSecondPointerUp(MotionEvent ev) {
		final int pointIndex = ev.findPointerIndex(mActivePointerId);
		final int pointerId = ev.getPointerId(pointIndex);
		if (mActivePointerId == pointerId) {
			final int newPointerIndex = pointIndex == 0 ? 1 : 0;
			mActivePointerId = ev.getPointerId(newPointerIndex);
		}
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		mListener = listener;
	}

	public interface OnRefreshListener {
		void onRefresh();
	}
}
