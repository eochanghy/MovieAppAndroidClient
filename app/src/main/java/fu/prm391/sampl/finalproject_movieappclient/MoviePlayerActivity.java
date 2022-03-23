package fu.prm391.sampl.finalproject_movieappclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
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

import fu.prm391.sampl.finalproject_movieappclient.Service.FloatingWidgetService;

public class MoviePlayerActivity extends AppCompatActivity {

    private boolean isFullscreen;
    private ImageView btnFullscreen;
    private Uri videoUri;
    private PlayerView playerView;
    private ExoPlayer exoPlayer;
    private ExtractorsFactory extractorsFactory;
    private ImageView exoFloatingWidget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_player);

        isFullscreen = false;
        playerView = findViewById(R.id.playerView);
        btnFullscreen = findViewById(R.id.exo_fullscreen);
        exoFloatingWidget = findViewById(R.id.exo_floating_widget);

        Intent intent = getIntent();
        if(intent != null) {
            String uriStr = intent.getStringExtra("videoUri");
            videoUri = Uri.parse(uriStr);
        }

        btnFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFullscreen) {
                    btnFullscreen.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.exo_controls_fullscreen_exit));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    btnFullscreen.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_fullscreen_24));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                isFullscreen = !isFullscreen;
            }
        });

        exoFloatingWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    exoPlayer.setPlayWhenReady(false);
                    exoPlayer.release();
                    Intent serviceIntent = new Intent(MoviePlayerActivity.this, FloatingWidgetService.class);
                    serviceIntent.putExtra("videoUri", videoUri.toString());
                    startService(serviceIntent);
                    finish();
            }
        });

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(
            new AdaptiveTrackSelection.Factory(bandwidthMeter)
        );
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        extractorsFactory = new DefaultExtractorsFactory();
        playVideo();
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    private void playVideo() {
        try {
            String playerInfo = Util.getUserAgent(this, "PHEPHIM");
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, playerInfo);
            MediaSource mediaSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory ,null, null);
            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.release();
    }
}