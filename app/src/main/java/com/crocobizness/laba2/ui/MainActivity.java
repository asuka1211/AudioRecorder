package com.crocobizness.laba2.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crocobizness.laba2.viewmodel.AudioRecordViewModel;
import com.crocobizness.laba2.R;
import com.crocobizness.laba2.database.entity.AudioRecord;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, Player.EventListener {

    private String path;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION_AND_SAVE_DATA = 200;
    private static final String LOG_TAG = "AudioRecordError";
    private boolean recording = false;
    private MediaRecorder mediaRecorder;
    private String fileName;
    private boolean permissionToRecordAndSaveDataAccepted = false;
    private AudioRecordViewModel viewModel;
    private AudioRecordsAdapter adapter;
    private SimpleExoPlayer player;
    private DataSource.Factory dataSourceFactory;
    private boolean isStart = false;
    private boolean isPlaying = false;
    private Click click;
    private RecyclerView recyclerView;
    private ItemSwipeManager itemSwipeManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.main_recycle_view);
        ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_RECORD_AUDIO_PERMISSION_AND_SAVE_DATA);
        path = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath();
        viewModel = ViewModelProviders.of(this).get(AudioRecordViewModel.class);
        adapter = new AudioRecordsAdapter(this, new AudioRecordsAdapter.Listener() {
            @Override
            public void onClick(View view) {
                prepareExoPlayer(view);
            }

            @Override
            public void deleteItem(AudioRecord record) {
                viewModel.delete(record);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subscribeUiRecord();
        click = new Click();
        itemSwipeManager = new ItemSwipeManager(this, new SwipeListenerImpl(adapter));
        Button btnRecord = findViewById(R.id.main_btnStartRecord);
        btnRecord.setOnTouchListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        itemSwipeManager.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStop() {
        itemSwipeManager.detachFromRecyclerView();
        super.onStop();
    }

    private ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }

        @Override
        public void onLoadingChanged(boolean isLoading) { ;
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState){
                case ExoPlayer.STATE_ENDED:
                    isPlaying = false;
                    isStart = false;
                    click.setPlayPause(true);
                    break;
                case ExoPlayer.STATE_READY:
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    break;
                case ExoPlayer.STATE_IDLE:
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION_AND_SAVE_DATA) {
            permissionToRecordAndSaveDataAccepted =
                    grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAndSaveDataAccepted ) {
            finish();
        }
    }

    private void startRecord(){
        Date date = new Date();
        mediaRecorder = new MediaRecorder();
        fileName = String.valueOf(date.getTime());
        path += fileName;
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(path);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recording = true;
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
            recording = false;
        }
    }

    private void stopRecord(){
        mediaRecorder.stop();
        mediaRecorder.release();
        AudioRecord audioRecord = new AudioRecord(fileName,path);
        viewModel.insert(audioRecord);
        mediaRecorder = null;
        recording = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeExoPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseExoPlayer();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                if (!recording) {
                    view.setBackground(getResources().getDrawable(R.drawable.round_button_blue));
                    startRecord();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (recording) {
                    view.setBackground(getResources().getDrawable(R.drawable.round_button_yellow));
                    stopRecord();
                }
                break;
        }
        return true;
    }

    private void subscribeUiRecord(){
        viewModel.getRecords().observe(this,
                audioRecords -> adapter.setAudioRecords(audioRecords));
    }

    private void initializeExoPlayer(){
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
            player.addListener(eventListener);
            dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, "Audio recorder"));
        }
    }

    private void releaseExoPlayer(){
        if (player != null){
            player.release();
            player = null;
        }

    }

    private void prepareExoPlayer(View view){
        ImageView imageView = (ImageView) view.getTag(AudioRecordsAdapter.IMG_VIEW);
        if (isStart && click.getBtnPlay() == imageView) {
            click.setPlayPause(isPlaying);
            return;
        }
        if (click.getBtnPlay() != imageView && click.getBtnPlay() != null) {
            click.setPlayPause(true);
        }

        AudioRecord audioRecord = (AudioRecord) view.getTag(AudioRecordsAdapter.CURRENT_AUDIO_RECORD);
        File audioTrack = new File(audioRecord.getPath());
        Uri trackUri = Uri.fromFile(audioTrack);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(trackUri);
        player.prepare(mediaSource);
        click.setBtnPlay(imageView);
        click.setPlayPause(isPlaying);
        isStart = true;
    }

    private class Click{

        private ImageView btnPlay;

        public ImageView getBtnPlay() {
            return btnPlay;
        }

        public void setBtnPlay(ImageView btnPlay) {
            this.btnPlay = btnPlay;
        }

        private void setPlayPause(boolean play){
            if(!play){
                btnPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                isPlaying = true;
                player.setPlayWhenReady(isPlaying);
            } else {
                btnPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                isPlaying = false;
                player.setPlayWhenReady(isPlaying);
            }
        }
    }
}
