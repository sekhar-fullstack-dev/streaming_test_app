package com.example.streaming_test_app;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.TrackSelectionOverride;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.Tracks;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.upstream.BandwidthMeter;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;

import com.example.streaming_test_app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ExoPlayer player;
    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private Handler handler;
    private DefaultTrackSelector trackSelector;
    private int currentTrackIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initializePlayer();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer() {
        try {
            if (player == null) {
                // Setup bandwidth meter
                DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this).build();

                // Setup track selector with adaptive track selection
                trackSelector = new DefaultTrackSelector(this, new AdaptiveTrackSelection.Factory());

                // Setup data source factory
                HttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();

                // Setup media source
                @OptIn(markerClass = UnstableApi.class)
                MediaSource mediaSource = new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri("http://192.168.0.110:8000/live/dash/output.mpd"));

                // Setup ExoPlayer
                player = new ExoPlayer.Builder(getApplicationContext())
                        .setTrackSelector(trackSelector)
                        .setBandwidthMeter(bandwidthMeter)
                        .build();

                // Attach player to the PlayerView
                binding.playerView.setPlayer(player);

                // Prepare and start playback
                player.setMediaSource(mediaSource);
                player.prepare();
                player.play();

                // Enable logging
                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlayerError(PlaybackException error) {
                        Log.e(TAG, "Player error: " + error.getMessage());
                    }

                    @Override
                    public void onTracksChanged(Tracks tracks) {
                        Log.d(TAG, "Tracks changed");
                    }
                });

                // Change track every 4 seconds
                handler = new Handler();
                handler.postDelayed(trackChangeRunnable, 4000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Runnable trackChangeRunnable = new Runnable() {
        @Override
        public void run() {
            changeTrack();
            //handler.postDelayed(this, 2000);
        }
    };

    @OptIn(markerClass = UnstableApi.class)
    private void changeTrack() {
        if (player != null) {
            Tracks tracks = player.getCurrentTracks();
            for (int i = 0; i < tracks.getGroups().size(); i++) {
                Tracks.Group trackGroup = tracks.getGroups().get(i);
                if (trackGroup.getType() == C.TRACK_TYPE_VIDEO) {
                    int newTrackIndex = (currentTrackIndex) % trackGroup.length;
                    Log.d(TAG, "Switching to track index: " + newTrackIndex);
                    player.setTrackSelectionParameters(
                            player.getTrackSelectionParameters()
                                    .buildUpon()
                                    .setOverrideForType(
                                            new TrackSelectionOverride(
                                                    trackGroup.getMediaTrackGroup(), /* trackIndex= */ 0))
                                    .build());
                    return;
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            handler.removeCallbacks(trackChangeRunnable);
            player.release();
            player = null;
        }
    }
}
