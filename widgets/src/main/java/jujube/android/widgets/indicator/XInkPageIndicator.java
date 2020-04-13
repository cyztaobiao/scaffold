package jujube.android.widgets.indicator;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.viewpager.widget.ViewPager;

import java.util.Arrays;

import jujube.android.widgets.R;

/**
 * Created by tb on 2017/10/16.
 */

public class XInkPageIndicator extends View implements ViewPager.OnPageChangeListener{

	// defaults
	private static final int DEFAULT_DOT_SIZE = 8;                      // dp
	private static final int DEFAULT_GAP = 12;                          // dp
	private static final int DEFAULT_ANIM_DURATION = 320;               // ms
	private static final int DEFAULT_UNSELECTED_COLOUR = 0x80ffffff;    // 50% white
	private static final int DEFAULT_SELECTED_COLOUR = 0xffffffff;      // 100% white

	// constants
	private static final float INVALID_FRACTION = -1f;
	private static final float MINIMAL_REVEAL = 0.00001f;

	// configurable attributes
	private int dotDiameter;
	private int gap;
	private long animDuration;
	private int unselectedColour;
	private int selectedColour;

	// derived from attributes
	private float dotRadius;
	private float halfDotRadius;
	private long animHalfDuration;
	private float dotTopY;
	private float dotCenterY;
	private float dotBottomY;

	// ViewPager
	private ViewPager viewPager;

	// state
	private int pageCount;
	private int currentPage;
	private int previousPage;
	private float selectedDotX;
	private boolean selectedDotInPosition;
	private float[] dotCenterX;
	private float[] joiningFractions;
	private float retreatingJoinX1;
	private float retreatingJoinX2;
	private float[] dotRevealFractions;
	private boolean isAttachedToWindow;
	private boolean pageChanging;

	// drawing
	private final Paint unselectedPaint;
	private final Paint selectedPaint;
	private Path combinedUnselectedPath;
	private final Path unselectedDotPath;
	private final Path unselectedDotLeftPath;
	private final Path unselectedDotRightPath;
	private final RectF rectF;

	// animation
	private ValueAnimator moveAnimation;
	private AnimatorSet joiningAnimationSet;
	private InkPageIndicator.PendingRetreatAnimator retreatAnimation;
	private InkPageIndicator.PendingRevealAnimator[] revealAnimations;
	private final Interpolator interpolator;

	// working values for beziers
	float endX1;
	float endY1;
	float endX2;
	float endY2;
	float controlX1;
	float controlY1;
	float controlX2;
	float controlY2;

	Animation rightwardAnimation = new Animation() {
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			Log.e("TAG", interpolatedTime + "");
			if (interpolatedTime < 0.7f) {
				selectedDotX = dotCenterX[previousPage] + (dotDiameter + gap) * interpolatedTime * 101 / 71;
				dotRevealFractions[currentPage] = 0f;
				dotRevealFractions[previousPage] = dotRadius;
			}
			else {
				selectedDotX = dotCenterX[currentPage];
				dotRevealFractions[previousPage] = (interpolatedTime - 0.7f) * dotRadius / 0.3f;
				dotRevealFractions[currentPage] = (interpolatedTime - 0.7f) * (dotDiameter + gap) / 0.3f;
			}
			invalidate();
		}
	};

	Animation leftwardAnimation = new Animation() {
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 0.7f) {
				selectedDotX = dotCenterX[previousPage] - (dotDiameter + gap) * interpolatedTime * 101 / 71;
				dotRevealFractions[currentPage] = 0f;
				dotRevealFractions[previousPage] = dotRadius;
			}
			else {
				selectedDotX = dotCenterX[currentPage];
				dotRevealFractions[previousPage] = (interpolatedTime - 0.7f) * dotRadius / 0.3f;
				dotRevealFractions[currentPage] = (interpolatedTime - 0.7f) * (dotDiameter + gap) / 0.3f;
			}

			invalidate();
		}
	};

	public XInkPageIndicator(Context context) {
		this(context, null, 0);
	}

	public XInkPageIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public XInkPageIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		final int density = (int) context.getResources().getDisplayMetrics().density;

		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(
				attrs, R.styleable.InkPageIndicator, defStyle, 0);

		dotDiameter = a.getDimensionPixelSize(R.styleable.InkPageIndicator_dotDiameter,
				DEFAULT_DOT_SIZE * density);
		dotRadius = dotDiameter / 2;
		halfDotRadius = dotRadius / 2;
		gap = a.getDimensionPixelSize(R.styleable.InkPageIndicator_dotGap,
				DEFAULT_GAP * density);
		animDuration = (long) a.getInteger(R.styleable.InkPageIndicator_animationDuration,
				DEFAULT_ANIM_DURATION);
		animHalfDuration = animDuration / 2;
		unselectedColour = a.getColor(R.styleable.InkPageIndicator_pageIndicatorColor,
				DEFAULT_UNSELECTED_COLOUR);
		selectedColour = a.getColor(R.styleable.InkPageIndicator_currentPageIndicatorColor,
				DEFAULT_SELECTED_COLOUR);

		a.recycle();

		unselectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		unselectedPaint.setColor(unselectedColour);
		selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		selectedPaint.setColor(selectedColour);
		interpolator = new FastOutSlowInInterpolator();

		// create paths & rect now – reuse & rewind later
		combinedUnselectedPath = new Path();
		unselectedDotPath = new Path();
		unselectedDotLeftPath = new Path();
		unselectedDotRightPath = new Path();
		rectF = new RectF();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int desiredHeight = dotDiameter + getPaddingTop() + getPaddingBottom();
		int height;
		switch (MeasureSpec.getMode(heightMeasureSpec)) {
			case MeasureSpec.EXACTLY:
				height = MeasureSpec.getSize(heightMeasureSpec);
				break;
			case MeasureSpec.AT_MOST:
				height = Math.min(desiredHeight, MeasureSpec.getSize(heightMeasureSpec));
				break;
			default:
				height = desiredHeight;
				break;
		}

		int desiredWidth = getPaddingLeft() + pageCount * dotDiameter + (pageCount - 1) * gap + getPaddingRight();
		int width;
		switch (MeasureSpec.getMode(widthMeasureSpec)) {
			case MeasureSpec.EXACTLY:
				width = MeasureSpec.getSize(widthMeasureSpec);
				break;
			case MeasureSpec.AT_MOST:
				width = Math.min(desiredWidth, MeasureSpec.getSize(widthMeasureSpec));
				break;
			default:
				width = desiredWidth;
				break;
		}
		setMeasuredDimension(width, height);
		calculateDotPositions(width, height);
	}

	private void calculateDotPositions(int width, int height) {
		int left = getPaddingLeft();
		int right = width - getPaddingRight();
		int top = getPaddingTop();
		int bottom = height - getPaddingBottom();

		dotCenterX = new float[pageCount];
		float startCenterX = left + (right - left - (dotDiameter * pageCount + gap * (pageCount - 1))) /2 + dotRadius;
		for (int i = 0; i < pageCount; i++) {
			dotCenterX[i] = startCenterX + i * (dotDiameter + gap);
		}
		dotTopY = top;
		dotCenterY = top + dotRadius;
		dotBottomY = top + dotDiameter;

		selectedDotX = dotCenterX[currentPage];

		selectedDotInPosition = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (viewPager == null || pageCount == 0) return;
		drawUnselected(canvas);
		drawSelected(canvas);

	}

	private void drawUnselected(Canvas canvas) {
		unselectedDotPath.rewind();

		if (!selectedDotInPosition) {
			if (previousPage < currentPage) {
				rectF.set(dotCenterX[previousPage] - dotRadius + dotRevealFractions[currentPage], dotTopY,
						dotCenterX[currentPage] + dotRadius, dotBottomY);

			}else {
				rectF.set(dotCenterX[currentPage] - dotRadius, dotTopY,
						dotCenterX[previousPage] + dotRadius - dotRevealFractions[currentPage], dotBottomY);
			}
			unselectedDotPath.addRoundRect(rectF, dotRadius, dotRadius, Path.Direction.CW);
			for (int i = 0; i < pageCount; i ++) {
				if (i == previousPage) {
					unselectedDotPath.addCircle(dotCenterX[i], dotCenterY, dotRevealFractions[previousPage], Path.Direction.CW);
				}else
					unselectedDotPath.addCircle(dotCenterX[i], dotCenterY, dotRadius, Path.Direction.CW);
			}
		}else {
			for (int i = 0; i < pageCount; i ++) {
				if (joiningFractions[i] != 0f) {
					rectF.set(dotCenterX[i] - dotRadius, dotTopY, dotCenterX[i] + dotRadius, dotBottomY);
					unselectedDotPath.arcTo(rectF, 90, 180, true);
					unselectedDotPath.cubicTo(
							dotCenterX[i] + dotRadius * 1.3f + gap * joiningFractions[i] * 1.3f, dotTopY + halfDotRadius,
							dotCenterX[i] + dotRadius * 1.3f + gap * joiningFractions[i] * 1.3f, dotBottomY - halfDotRadius,
							dotCenterX[i], dotBottomY);
					unselectedDotPath.close();
					if (i + 1 <= pageCount - 1) {
						i++;
						rectF.set(dotCenterX[i] - dotRadius, dotTopY, dotCenterX[i] + dotRadius, dotBottomY);
						unselectedDotPath.arcTo(rectF, 270, 180, true);
						unselectedDotPath.cubicTo(
								dotCenterX[i] - dotRadius * 1.3f - gap * joiningFractions[i] * 1.3f, dotBottomY - halfDotRadius,
								dotCenterX[i] - dotRadius * 1.3f - gap * joiningFractions[i] * 1.3f, dotTopY + halfDotRadius,
								dotCenterX[i], dotTopY);
						unselectedDotPath.close();
					}
				}else
					unselectedDotPath.addCircle(dotCenterX[i], dotCenterY, dotRadius, Path.Direction.CW);
			}
		}


		canvas.drawPath(unselectedDotPath, unselectedPaint);
	}
	private void drawSelected(Canvas canvas) {
		canvas.drawCircle(selectedDotX, dotCenterY, dotRadius, selectedPaint);
	}

	public void setViewPager(ViewPager pager) {
		this.viewPager = pager;
		this.pageCount = pager.getAdapter().getCount();
		viewPager.addOnPageChangeListener(this);
		currentPage = viewPager.getCurrentItem();
		joiningFractions = new float[pageCount == 0 ? 0 : pageCount];
		Arrays.fill(joiningFractions, 0f);
		dotRevealFractions =new float[pageCount == 0 ? 0 : pageCount];
		Arrays.fill(dotRevealFractions, 0f);
		requestLayout();
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		Arrays.fill(joiningFractions, 0f);
		float fraction = positionOffset;
		if (position != currentPage)
			fraction = 1f - fraction;

		joiningFractions[position] = fraction;
		if (position + 1 <= pageCount - 1) {
			joiningFractions[position + 1] = fraction;
		}
		invalidate();
	}

	@Override
	public void onPageSelected(int position) {
		previousPage = currentPage;
		currentPage = position;
		startAnimation(createMoveSelectedAnimator(previousPage, currentPage));
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	private Animation createMoveSelectedAnimator(
			int was, int now) {
		Animation moveSelected = was > now ? leftwardAnimation : rightwardAnimation;
		moveSelected.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				selectedDotInPosition = false;
				Arrays.fill(dotRevealFractions, 0f);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				selectedDotInPosition = true;
				selectedDotX = dotCenterX[currentPage];
				invalidate();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		moveSelected.setInterpolator(interpolator);
		moveSelected.reset();
		moveSelected.setDuration(animDuration);

		return moveSelected;
	}
}
