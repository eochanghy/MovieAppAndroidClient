package fu.prm391.sampl.finalproject_movieappclient.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import fu.prm391.sampl.finalproject_movieappclient.MoviePlayerActivity;
import fu.prm391.sampl.finalproject_movieappclient.R;

public class FloatingWidgetService extends Service {

    private WindowManager mWindowManager;
    private View mFLoatingWedge;
    private Uri videoUri;
    private SimpleExoPlayer exoPlayer;
    private PlayerView playerView;


    public FloatingWidgetService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String uriStr = intent.getStringExtra("videoUri");
            videoUri = Uri.parse(uriStr);
            if(mWindowManager != null && mFLoatingWedge.isShown() && exoPlayer != null) {
                mWindowManager.removeView(mFLoatingWedge);
                mFLoatingWedge = null;
                mWindowManager = null;
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.release();
                exoPlayer = null;
            }
            final  WindowManager.LayoutParams param;
            mFLoatingWedge = LayoutInflater.from(this).inflate(R.layout.custom_pop_up_window, null);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                param = new WindowManager.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT
                        );
            } else {
                param = new WindowManager.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT
                );
            }
            param.gravity = Gravity.TOP | Gravity.LEFT;
            param.x = 0;
            param.y = 0;
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFLoatingWedge, param);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            playerView = mFLoatingWedge.findViewById(R.id.playerView);
            ImageView imageViewClose = mFLoatingWedge.findViewById(R.id.imgView_dismiss);
            ImageView imageViewMaximize = mFLoatingWedge.findViewById(R.id.imgView_maximize);
            imageViewMaximize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mWindowManager != null && mFLoatingWedge.isShown() && exoPlayer != null) {
                        mWindowManager.removeView(mFLoatingWedge);
                        mFLoatingWedge = null;
                        mWindowManager = null;
                        exoPlayer.setPlayWhenReady(false);
                        exoPlayer.release();
                        exoPlayer = null;
                        stopSelf();

                        Intent openActivity = new Intent(FloatingWidgetService.this, MoviePlayerActivity.class);
                        openActivity.putExtra("videoUri", videoUri.toString());
                        openActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(openActivity);
                    }
                }
            });
            imageViewClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mWindowManager != null && mFLoatingWedge.isShown() && exoPlayer != null){
                        mWindowManager.removeView(mFLoatingWedge);
                        mFLoatingWedge = null;
                        mWindowManager = null;
                        exoPlayer.setPlayWhenReady(false);
                        exoPlayer.release();
                        exoPlayer = null;
                        stopSelf();
                    }
                }
            });

            playVideo();
            mFLoatingWedge.findViewById(R.id.relativeLayout_customPopup).setOnTouchListener(new View.OnTouchListener() {
                private int initialX, initialY;
                private float initialTouchX, initialTouchY;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = param.x;
                            initialY = param.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;

                        case MotionEvent.ACTION_UP:

                            return true;
                        case MotionEvent.ACTION_MOVE:
                            param.x = initialX + (int) (event.getRawX() - initialTouchX);
                            param.y = initialX + (int) (event.getRawY() - initialTouchY);
                            mWindowManager.updateViewLayout(mFLoatingWedge, param);
                            return true;
                    }
                    return false;
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }
    public void playVideo() {
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(
                    new AdaptiveTrackSelection.Factory(bandwidthMeter)
            );
            exoPlayer = ExoPlayerFactory.newSimpleInstance(FloatingWidgetService.this, trackSelector);
            String playerInfo = Util.getUserAgent(this, "videoPlayer");
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, playerInfo);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    public void onDestroy() {
        super.onDestroy();
        if(mFLoatingWedge != null) {
            mWindowManager.removeView(mFLoatingWedge);
        }
    }
}
