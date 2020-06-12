package com.liner.views;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

public class AppBar extends BaseItem {
    private ImageButton expandAppBar;
    private ExpandLayout appBarLayout;
    private YSTextView requestsItem;
    private YSTextView profileItem;
    private BarCallback barCallback;

    private final ValueAnimator expandAnimation = ValueAnimator.ofPropertyValuesHolder(
            PropertyValuesHolder.ofFloat("buttonRotation", 0, 180),
            PropertyValuesHolder.ofFloat("itemsSize", 0, 1)
    );
    private final ValueAnimator collapseAnimation = ValueAnimator.ofPropertyValuesHolder(
            PropertyValuesHolder.ofFloat("buttonRotation", 180, 0),
            PropertyValuesHolder.ofFloat("itemsSize", 1, 0)
    );

    public AppBar(Context context) {
        super(context);
        setup();
    }

    public AppBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();

    }

    private void setup(){
        expandAnimation.setInterpolator(new OvershootInterpolator());
        expandAnimation.setDuration(400);
        expandAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                expandAppBar.setRotation((Float) valueAnimator.getAnimatedValue("buttonRotation"));
                profileItem.setScaleX((Float) valueAnimator.getAnimatedValue("itemsSize"));
                profileItem.setScaleY((Float) valueAnimator.getAnimatedValue("itemsSize"));
                profileItem.setAlpha((Float) valueAnimator.getAnimatedValue("itemsSize"));
                requestsItem.setScaleX((Float) valueAnimator.getAnimatedValue("itemsSize"));
                requestsItem.setScaleY((Float) valueAnimator.getAnimatedValue("itemsSize"));
                requestsItem.setAlpha((Float) valueAnimator.getAnimatedValue("itemsSize"));
            }
        });
        expandAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                appBarLayout.expand();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        collapseAnimation.setInterpolator(new OvershootInterpolator());
        collapseAnimation.setDuration(400);
        collapseAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                expandAppBar.setRotation((Float) valueAnimator.getAnimatedValue("buttonRotation"));
                profileItem.setScaleX((Float) valueAnimator.getAnimatedValue("itemsSize"));
                profileItem.setScaleY((Float) valueAnimator.getAnimatedValue("itemsSize"));
                profileItem.setAlpha((Float) valueAnimator.getAnimatedValue("itemsSize"));
                requestsItem.setScaleX((Float) valueAnimator.getAnimatedValue("itemsSize"));
                requestsItem.setScaleY((Float) valueAnimator.getAnimatedValue("itemsSize"));
                requestsItem.setAlpha((Float) valueAnimator.getAnimatedValue("itemsSize"));
            }
        });

    }

    @Override
    protected void onFindViewById() {
        expandAppBar = findViewById(R.id.expandAppBar);
        appBarLayout = findViewById(R.id.appBarLayout);
        requestsItem = findViewById(R.id.requestsItem);
        profileItem = findViewById(R.id.profileItem);
        expandAppBar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(expandAnimation.isRunning())
                    expandAnimation.cancel();
                if(collapseAnimation.isRunning())
                    collapseAnimation.cancel();
                collapseAnimation.removeAllListeners();
                expandAnimation.removeAllListeners();
                if(appBarLayout.isExpanded()){
                    collapseAnimation.start();
                    appBarLayout.collapse();
                } else {
                    expandAnimation.start();
                    appBarLayout.expand();
                }
            }
        });

        requestsItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                collapseAnimation.start();
                appBarLayout.collapse();
                collapseAnimation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if(barCallback != null){
                            barCallback.onRequestsOpen();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
        });
        profileItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                collapseAnimation.start();
                appBarLayout.collapse();
                collapseAnimation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if(barCallback != null){
                            barCallback.onProfileOpen();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
        });
    }

    @Override
    protected void onInflate() {
        inflater.inflate(R.layout.app_bar_item, this);
    }


    public void setBarCallback(BarCallback barCallback) {
        this.barCallback = barCallback;
    }

    public interface BarCallback{
        void onRequestsOpen();
        void onProfileOpen();
    }
}
