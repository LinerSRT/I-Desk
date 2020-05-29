package com.liner.i_desk.Utils.Animations;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;

import com.google.firebase.database.core.view.QuerySpec;
import com.google.firebase.database.core.view.ViewCache;

public class ViewAnimator {
    private View view;
    private boolean isAnimating = false;

    public ViewAnimator(View view) {
        this.view = view;
    }
    public ViewAnimator(ViewGroup view) {
        this.view = view;
    }

    public void animateAction(int duration, final AnimatorListener listener){
        if (view == null)
            return;
        if(view instanceof ViewGroup){
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                ((ViewGroup) view).getChildAt(i).animate()
                        .setDuration(duration)
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .setInterpolator(new CycleInterpolator(0.5f))
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                isAnimating = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                isAnimating = false;
                                listener.done();
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                                isAnimating = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                                isAnimating = true;
                            }
                        }).start();
            }
        } else{
            view.animate()
                    .setDuration(duration)
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setInterpolator(new CycleInterpolator(0.5f))
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            isAnimating = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            isAnimating = false;
                            listener.done();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                            isAnimating = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                            isAnimating = true;
                        }
                    }).start();
        }
    }

    public void animateAction(int duration){
        if (view == null)
            return;
        if(view instanceof ViewGroup){
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                ((ViewGroup) view).getChildAt(i).animate()
                        .setDuration(duration)
                        .scaleX(0.8f)
                        .scaleY(0.8f)
                        .setInterpolator(new CycleInterpolator(0.5f))
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                isAnimating = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                isAnimating = false;
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                                isAnimating = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                                isAnimating = true;
                            }
                        }).start();
            }
        } else{
            view.animate()
                    .setDuration(duration)
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setInterpolator(new CycleInterpolator(0.5f))
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            isAnimating = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            isAnimating = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                            isAnimating = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                            isAnimating = true;
                        }
                    }).start();
        }
    }


    public interface AnimatorListener{
        void done();
    }

    public boolean isAnimating() {
        return isAnimating;
    }
}
