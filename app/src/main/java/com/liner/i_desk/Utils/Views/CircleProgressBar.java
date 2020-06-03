package com.liner.i_desk.Utils.Views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.ColorInt;

import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ColorUtils;
import com.liner.i_desk.Utils.ViewUtils;

public class CircleProgressBar extends View {

    private final ValueAnimator animationHide = ValueAnimator.ofPropertyValuesHolder(PropertyValuesHolder.ofFloat("Alpha", 1f, 0f));
    private final ValueAnimator animationShow = ValueAnimator.ofPropertyValuesHolder(PropertyValuesHolder.ofFloat("Alpha", 0f, 1f));
    private final ValueAnimator animationStarting = ValueAnimator.ofPropertyValuesHolder(PropertyValuesHolder.ofInt("Rotation", -90, 270),
            PropertyValuesHolder.ofInt("Progress", 0, 100));
    private Drawable normalIcon;
    private Drawable cancelIcon;
    private Drawable finishedIcon;
    private Drawable errorIcon;
    private ProgressState progressState = ProgressState.NORMAL;
    private onStateChageListener onStateChageListener;
    private float strokeWidth = 4;
    private float progress = 0;
    private int progressMin = 0;
    private int progressMax = 100;
    private int progressStartAngle = -90;
    private float progressPadding = ViewUtils.dpToPx(8);
    private float progressIconPadding = ViewUtils.dpToPx(8);
    private int progressColor = Color.DKGRAY;
    private int backgroundColor = Color.LTGRAY;
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    @ColorInt
    private int primaryColor;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public void setOnStateChageListener(CircleProgressBar.onStateChageListener onStateChageListener) {
        this.onStateChageListener = onStateChageListener;
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, 0, 0);
        try {
            progressPadding = typedArray.getDimension(R.styleable.CircleProgressBar_progressBarPadding, progressPadding);
            progressIconPadding = typedArray.getDimension(R.styleable.CircleProgressBar_progressBarIconPadding, progressIconPadding);
            strokeWidth = typedArray.getDimension(R.styleable.CircleProgressBar_progressBarThickness, strokeWidth);
            progress = typedArray.getFloat(R.styleable.CircleProgressBar_progress, progress);
            progressStartAngle = typedArray.getInt(R.styleable.CircleProgressBar_progressStartAngle, progressStartAngle);
            progressColor = typedArray.getInt(R.styleable.CircleProgressBar_progressBarColor, progressColor);
            backgroundColor = typedArray.getInt(R.styleable.CircleProgressBar_progressBarBackgroundColor, backgroundColor);
            progressMin = typedArray.getInt(R.styleable.CircleProgressBar_progressMin, progressMin);
            progressMax = typedArray.getInt(R.styleable.CircleProgressBar_progressMax, progressMax);
        } finally {
            typedArray.recycle();
        }

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(progressColor);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);
        normalIcon = context.getResources().getDrawable(R.drawable.download_icon);
        cancelIcon = context.getResources().getDrawable(R.drawable.cancel_icon);
        finishedIcon = context.getResources().getDrawable(R.drawable.download_finished_icon);
        errorIcon = context.getResources().getDrawable(R.drawable.failed_icon);
        primaryColor = ColorUtils.getThemeColor(context, R.attr.colorPrimaryDark);
        normalIcon.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
        cancelIcon.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
        finishedIcon.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);


        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (progressState) {
                    case NORMAL:
                        setProgressState(ProgressState.START);
                        break;
                    case CANCEL:
                        setProgressState(ProgressState.NORMAL);
                        break;
                    case PROGRESS:
                    case START:
                        setProgressState(ProgressState.CANCEL);
                        break;
                    case FINISHED:
                        setProgressState(ProgressState.FINISHED);
                        break;
                    case ERROR:
                        setProgressState(ProgressState.NORMAL);
                        break;
                }
            }
        });
    }

    public void setProgressState(ProgressState state, boolean notify) {
        this.progressState = state;
        if (notify)
            switch (progressState) {
                case NORMAL:
                    cancelStartingAnimation();
                    if (onStateChageListener != null)
                        onStateChageListener.onIdle();
                    break;
                case START:
                    animateStarting();
                    if (onStateChageListener != null)
                        onStateChageListener.onStart();
                    break;
                case CANCEL:
                    cancelStartingAnimation();
                    if (onStateChageListener != null)
                        onStateChageListener.onCancel();
                    break;
                case PROGRESS:
                    cancelStartingAnimation();
                    if (onStateChageListener != null)
                        onStateChageListener.onProgressing();
                    break;
                case FINISHED:
                    cancelStartingAnimation();
                    if (onStateChageListener != null)
                        onStateChageListener.onFinish();
                    break;
                case ERROR:
                    cancelStartingAnimation();
                    if (onStateChageListener != null)
                        onStateChageListener.onError();
                    break;
            }
        invalidate();

    }

    public void setProgressState(ProgressState state) {
        setProgressState(state, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (progressState) {
            case CANCEL:
            case NORMAL:
                drawIcon(canvas, normalIcon);
                break;
            case START:
            case PROGRESS:
                canvas.drawOval(rectF, backgroundPaint);
                canvas.drawArc(rectF, progressStartAngle, 360 * progress / progressMax, false, foregroundPaint);
                drawIcon(canvas, cancelIcon);
                break;
            case FINISHED:
                drawIcon(canvas, finishedIcon);
                break;
            case ERROR:
                drawIcon(canvas, errorIcon);
                break;
        }
    }

    private void drawIcon(Canvas canvas, Drawable icon) {
        icon.setBounds((int) progressIconPadding, (int) progressIconPadding, (int) (getWidth() - progressIconPadding), (int) (getHeight() - progressIconPadding));
        icon.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        rectF.set(progressPadding + strokeWidth / 2, progressPadding + strokeWidth / 2, (min - strokeWidth / 2) - progressPadding, (min - strokeWidth / 2) - progressPadding);
    }


    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public void setProgressWithAnimation(float progress) {
        setProgressWithAnimation(progress, 1500);
    }

    public void setProgressWithAnimation(float progress, int duration) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
        objectAnimator.setDuration(1500);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    public void animateStarting() {
        animationStarting.setDuration(1500);
        animationStarting.setRepeatMode(ValueAnimator.RESTART);
        animationStarting.setRepeatCount(-1);
        animationStarting.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setProgressStartAngle((Integer) valueAnimator.getAnimatedValue("Rotation"));
                setProgress((Integer) valueAnimator.getAnimatedValue("Progress"));
            }
        });
        animationStarting.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setProgressStartAngle(-90);
                setProgress(0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                setProgressStartAngle(-90);
                setProgress(0);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        if (animationStarting.isRunning()) {
            animationStarting.cancel();
        }
        animationStarting.start();
    }

    public void cancelStartingAnimation() {
        if (animationStarting.isRunning()) {
            animationStarting.cancel();
        }
    }


    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public int getProgressMin() {
        return progressMin;
    }

    public void setProgressMin(int progressMin) {
        this.progressMin = progressMin;
    }

    public int getProgressMax() {
        return progressMax;
    }

    public void setProgressMax(int progressMax) {
        this.progressMax = progressMax;
    }

    public int getProgressStartAngle() {
        return progressStartAngle;
    }

    public void setProgressStartAngle(int progressStartAngle) {
        this.progressStartAngle = progressStartAngle;
        invalidate();
    }

    public float getProgressPadding() {
        return progressPadding;
    }

    public void setProgressPadding(float progressPadding) {
        this.progressPadding = progressPadding;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
        foregroundPaint.setColor(progressColor);
        invalidate();
    }

    public void setProgressBackgroundColor(int color) {
        this.backgroundColor = color;
        backgroundPaint.setColor(backgroundColor);
        invalidate();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        backgroundPaint.setStrokeWidth(strokeWidth);
        foregroundPaint.setStrokeWidth(strokeWidth);
        invalidate();
        requestLayout();
    }


    public enum ProgressState {
        NORMAL,
        START,
        PROGRESS,
        CANCEL,
        FINISHED,
        ERROR
    }

    public interface onStateChageListener {
        void onIdle();

        void onStart();

        void onProgressing();

        void onCancel();

        void onFinish();

        void onError();
    }

}