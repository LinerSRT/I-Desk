package com.liner.i_desk.Utils.Views.AudioRecord;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ViewUtils;

import java.io.IOException;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class RecordView extends RelativeLayout {

    public static final int DEFAULT_CANCEL_BOUNDS = 8;
    private Chronometer counterTime;
    private TextView slideToCancel;
    private ShimmerLayout slideToCancelLayout;
    private ImageView arrow;
    private float initialX, difX = 0;
    private float cancelBounds = DEFAULT_CANCEL_BOUNDS;
    private long startTime, elapsedTime = 0;
    private Context context;
    private OnRecordListener recordListener;
    private boolean isSwiped, isLessThanSecondAllowed = false;
    private boolean isSoundEnabled = true;
    private int RECORD_START = R.raw.record_start;
    private int RECORD_FINISHED = R.raw.record_finished;
    private int RECORD_ERROR = R.raw.record_error;
    private MediaPlayer player;


    public RecordView(Context context) {
        super(context);
        this.context = context;
        init(context, null, -1, -1);
    }


    public RecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs, -1, -1);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context, attrs, defStyleAttr, -1);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        View view = View.inflate(context, R.layout.record_view_layout, null);
        addView(view);
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        viewGroup.setClipChildren(false);
        arrow = view.findViewById(R.id.arrow);
        slideToCancel = view.findViewById(R.id.slide_to_cancel);
        counterTime = view.findViewById(R.id.counter_tv);
        slideToCancelLayout = view.findViewById(R.id.shimmer_layout);
        hideViews();
        if (attrs != null && defStyleAttr == -1 && defStyleRes == -1) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecordView, defStyleAttr, defStyleRes);
            int slideArrowResource = typedArray.getResourceId(R.styleable.RecordView_slide_to_cancel_arrow, -1);
            String slideToCancelText = typedArray.getString(R.styleable.RecordView_slide_to_cancel_text);
            int slideMarginRight = (int) typedArray.getDimension(R.styleable.RecordView_slide_to_cancel_margin_right, 30);
            int counterTimeColor = typedArray.getColor(R.styleable.RecordView_counter_time_color, -1);
            int arrowColor = typedArray.getColor(R.styleable.RecordView_slide_to_cancel_arrow_color, -1);
            int cancelBounds = typedArray.getDimensionPixelSize(R.styleable.RecordView_slide_to_cancel_bounds, -1);
            if (cancelBounds != -1)
                setCancelBounds(cancelBounds, false);
            if (slideArrowResource != -1) {
                Drawable slideArrow = AppCompatResources.getDrawable(getContext(), slideArrowResource);
                arrow.setImageDrawable(slideArrow);
            }
            if (slideToCancelText != null)
                slideToCancel.setText(slideToCancelText);
            if (counterTimeColor != -1)
                setCounterTimeColor(counterTimeColor);
            if (arrowColor != -1)
                setSlideToCancelArrowColor(arrowColor);
            setMarginRight(slideMarginRight, true);
            typedArray.recycle();
        }

    }


    private void hideViews() {
        hide(slideToCancelLayout);
        hide(counterTime);
        hide(this);
    }

    private void hide(View view){
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.slide_out_right);
        animation.setDuration(400);
        view.startAnimation(animation);
        view.setVisibility(View.GONE);
    }
    private void show(View view){
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.slide_in_left);
        animation.setDuration(400);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }


    private void showViews() {
        show(slideToCancelLayout);
        show(counterTime);
        show(this);
    }


    private boolean isLessThanOneSecond(long time) {
        return time <= 1000;
    }


    private void playSound(int soundRes) {
        if (isSoundEnabled) {
            if (soundRes == 0)
                return;
            try {
                player = new MediaPlayer();
                AssetFileDescriptor afd = context.getResources().openRawResourceFd(soundRes);
                if (afd == null) return;
                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                player.prepare();
                player.start();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
                player.setLooping(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    protected void onActionDown(RecordButton recordBtn) {
        if (recordListener != null)
            recordListener.onStart();
        recordBtn.startScale();
        slideToCancelLayout.startShimmerAnimation();
        initialX = recordBtn.getX();
        playSound(RECORD_START);
        showViews();
        counterTime.setBase(SystemClock.elapsedRealtime());
        startTime = System.currentTimeMillis();
        counterTime.start();
        isSwiped = false;
    }


    protected void onActionMove(RecordButton recordBtn, MotionEvent motionEvent) {

        if (!isSwiped) {

            if (slideToCancelLayout.getX() != 0 && slideToCancelLayout.getX() <= counterTime.getRight() + cancelBounds) {
                hideViews();
                animateCancelSide(recordBtn, slideToCancelLayout, initialX, difX);
                counterTime.stop();
                slideToCancelLayout.stopShimmerAnimation();
                isSwiped = true;
                if (recordListener != null)
                    recordListener.onCancel();
            } else {
                if (motionEvent.getRawX() < initialX) {
                    recordBtn.animate()
                            .x(motionEvent.getRawX())
                            .setDuration(0)
                            .start();
                    if (difX == 0)
                        difX = (initialX - slideToCancelLayout.getX());
                    slideToCancelLayout.animate()
                            .x(motionEvent.getRawX() - difX)
                            .setDuration(0)
                            .start();
                }
            }

        }
    }

    protected void onActionUp(RecordButton recordBtn) {
        if (recordListener != null && !isSwiped)
            recordListener.onFinish(elapsedTime);
        if (!isSwiped)
            playSound(RECORD_FINISHED);
        hideViews();
        animateCancelSide(recordBtn, slideToCancelLayout, initialX, difX);
        counterTime.stop();
        slideToCancelLayout.stopShimmerAnimation();
    }


    private void setMarginRight(int marginRight, boolean convertToDp) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) slideToCancelLayout.getLayoutParams();
        if (convertToDp) {
            layoutParams.rightMargin = ViewUtils.dpToPx(marginRight);
        } else
            layoutParams.rightMargin = marginRight;

        slideToCancelLayout.setLayoutParams(layoutParams);
    }


    public void setOnRecordListener(OnRecordListener recrodListener) {
        this.recordListener = recrodListener;
    }


    public void setSoundEnabled(boolean isEnabled) {
        isSoundEnabled = isEnabled;
    }

    public void setLessThanSecondAllowed(boolean isAllowed) {
        isLessThanSecondAllowed = isAllowed;
    }

    public void setSlideToCancelText(String text) {
        slideToCancel.setText(text);
    }


    public void setCancelBounds(float cancelBounds) {
        setCancelBounds(cancelBounds, true);
    }

    public void setCounterTimeColor(int color) {
        counterTime.setTextColor(color);
    }

    public void setSlideToCancelArrowColor(int color) {
        arrow.setColorFilter(color);
    }

    private void setCancelBounds(float cancelBounds, boolean convertDpToPixel) {
        this.cancelBounds = convertDpToPixel ? ViewUtils.dpToPx(cancelBounds) : cancelBounds;
    }


    void animateCancelSide(final RecordButton recordBtn, FrameLayout slideToCancelLayout, float initialX, float difX) {
        final ValueAnimator positionAnimator = ValueAnimator.ofFloat(recordBtn.getX(), initialX);
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) animation.getAnimatedValue();
                recordBtn.setX(x);
            }
        });
        recordBtn.stopScale();
        positionAnimator.setDuration(0);
        positionAnimator.start();
        if (difX != 0) {
            float x = initialX - difX;
            slideToCancelLayout.animate()
                    .x(x)
                    .setDuration(0)
                    .start();
        }
    }

}


