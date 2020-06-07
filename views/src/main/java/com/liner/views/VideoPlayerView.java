package com.liner.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.freedom.lauzy.playpauseviewlib.PlayPauseView;
import com.liner.utils.ViewUtils;


public class VideoPlayerView extends RelativeLayout implements View.OnClickListener,VideoSurfaceView.OnPlayStateChangedListener,VideoSurfaceView.OnPlaybackEventListener{
    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;

    private onReadyCallback onReadyCallback;

    private float SCREEN_RATIO;
    private float cornerRadius = ViewUtils.dpToPx(8);


    private PlayPauseView playPauseView;


    private Path path = new Path();
    private ProgressBar loadingProgress;
    private VideoSurfaceView videoView;
    private View layoutVideo;
    private VideoSurfaceView.OnPlayStateChangedListener onPlayStateChangedListener;
    private VideoSurfaceView.OnPlaybackEventListener onPlaybackEventListener;

    public void setOnReadyCallback(VideoPlayerView.onReadyCallback onReadyCallback) {
        this.onReadyCallback = onReadyCallback;
    }

    public VideoPlayerView(Context context) {
        this(context, null);
    }


    public VideoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        SCREEN_WIDTH = metrics.widthPixels;
        SCREEN_HEIGHT = metrics.heightPixels;
        SCREEN_RATIO = (float) SCREEN_WIDTH / SCREEN_HEIGHT;
        setMinimumHeight(0);
        setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.video_player_layout, this);
        videoView = findViewById(R.id.bvv_video);
        layoutVideo = findViewById(R.id.layout_video);
        playPauseView = findViewById(R.id.playPauseView);
        loadingProgress = findViewById(R.id.loadingProgress);
        videoView.setOnPlaybackEventListener(this);
        videoView.setOnPlayStateChangedListener(this);
        layoutVideo.setOnClickListener(this);
        hideControls();
        showProgress();

        playPauseView.setPlayPauseListener(new PlayPauseView.PlayPauseListener() {
            @Override
            public void play() {
                startVideo();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideControls();
                    }
                }, 500);
            }

            @Override
            public void pause() {
                pauseVideo();
            }
        });

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        RectF rectF = new RectF(0, 0, w, h);
        path.reset();
        path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW);
        path.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(path);
        super.onDraw(canvas);
        canvas.restoreToCount(save);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);
    }

    public void setVideoURI(Uri uri) {
        setVideoURI(uri, false);
    }

    public void setVideoURI(Uri uri, boolean startImmediately) {
        videoView.setVideoURI(uri, startImmediately);
    }

    public void setVideoURL(String url, boolean startImmediately) {
        videoView.setVideoURI(Uri.parse(url), startImmediately);
    }

    public void setVideoURL(String url) {
        setVideoURL(url, false);
    }

    public void startVideo() {
        videoView.start();
    }

    public void pauseVideo() {
        videoView.pause();
    }

    public void stopVideo() {
        videoView.stop();
    }

    public void release() {
        videoView.release();
    }

    public void seekTo(int msec) {
        videoView.seekTo(msec);
    }

    public boolean isPlaying() {
        return videoView.isPlaying();
    }

    public int getDuration() {
        return videoView.getDuration();
    }

    public int getCurrentPosition() {
        return videoView.getCurrentPosition();
    }

    public boolean isReleased() {
        return videoView.isReleased();
    }

    public int getVideoWidth() {
        return videoView.getVideoWidth();
    }

    public int getVideoHeight() {
        return videoView.getVideoHeight();
    }

    public void setOnPlaybackEventListener(VideoSurfaceView.OnPlaybackEventListener onPlaybackEventListener) {
        this.onPlaybackEventListener = onPlaybackEventListener;
    }

    public void setOnPlayStateChangedListener(VideoSurfaceView.OnPlayStateChangedListener onPlayStateChangedListener) {
        this.onPlayStateChangedListener = onPlayStateChangedListener;
    }

    private void hideControls() {
        Animation fadeOut = AnimationUtils.loadAnimation(playPauseView.getContext(), android.R.anim.fade_out);
        playPauseView.startAnimation(fadeOut);
        playPauseView.setVisibility(View.GONE);
    }

    private void showControls() {
        Animation fadeIn = AnimationUtils.loadAnimation(playPauseView.getContext(), android.R.anim.fade_in);
        playPauseView.startAnimation(fadeIn);
        playPauseView.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        Animation fadeOut = AnimationUtils.loadAnimation(loadingProgress.getContext(), android.R.anim.fade_out);
        loadingProgress.startAnimation(fadeOut);
        loadingProgress.setVisibility(View.GONE);
    }

    private void showProgress() {
        Animation fadeIn = AnimationUtils.loadAnimation(loadingProgress.getContext(), android.R.anim.fade_in);
        loadingProgress.startAnimation(fadeIn);
        loadingProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout_video) {
            if (playPauseView.getVisibility() == VISIBLE) {
                hideControls();
            } else {
                showControls();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideControls();
                    }
                }, 2000);
            }
        }
    }

    @Override
    public void onPrepared() {
        if(onReadyCallback != null){
            onReadyCallback.onReady(VideoPlayerView.this);
        }
        int videoWidth = videoView.getVideoWidth();
        int videoHeight = videoView.getVideoHeight();
        float ratioVideo = (float) videoWidth / videoHeight;
        float ratioScreenToVideo = SCREEN_RATIO / ratioVideo;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutVideo.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutVideo.setLayoutParams(params);
        }
        params.height = Math.round(ratioScreenToVideo * SCREEN_HEIGHT);
        layoutVideo.requestLayout();
        hideProgress();
        showControls();
        if (onPlayStateChangedListener != null) {
            onPlayStateChangedListener.onPrepared();
        }
        seekTo(1);
    }

    @Override
    public void onError(VideoSurfaceView.ErrorReason reason) {
        if (onPlayStateChangedListener != null) {
            onPlayStateChangedListener.onError(reason);
        }
    }

    @Override
    public void onCompletion() {
        pauseVideo();
        if (onPlayStateChangedListener != null) {
            onPlayStateChangedListener.onCompletion();
        }
    }

    @Override
    public void onReleased() {
        if(onReadyCallback != null){
            onReadyCallback.onRelease();
        }
        if (onPlayStateChangedListener != null) {
            onPlayStateChangedListener.onReleased();
        }
    }

    @Override
    public void onPlaying() {
        playPauseView.setPlaying(true);
        if (onPlaybackEventListener != null) {
            onPlaybackEventListener.onPlaying();
        }
    }

    @Override
    public void onPaused() {
        if (onPlaybackEventListener != null) {
            onPlaybackEventListener.onPaused();
        }
    }

    @Override
    public void onStopped() {
        playPauseView.setPlaying(false);
        if (onPlaybackEventListener != null) {
            onPlaybackEventListener.onStopped();
        }
    }

    @Override
    public void onPositionChanged(int position) {

    }

    @Override
    public void onBufferingUpdate(int buffering) {
        if (onPlaybackEventListener != null) {
            onPlaybackEventListener.onBufferingUpdate(buffering);
        }
    }

    @Override
    public void onSeekComplete() {
        if (onPlaybackEventListener != null) {
            onPlaybackEventListener.onSeekComplete();
        }
    }

    public interface onReadyCallback{
        void onReady(VideoPlayerView videoPlayerView);
        void onRelease();
    }
}