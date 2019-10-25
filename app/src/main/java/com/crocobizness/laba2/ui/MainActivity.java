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
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crocobizness.laba2.AudioRecordViewModel;
import com.crocobizness.laba2.R;
import com.crocobizness.laba2.database.AudioRecord;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, Player.EventListener, AudioRecordsAdapter.AudioPlayBackListener {

    private String path;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION_AND_SAVE_DATA = 200;
    private static final String LOG_TAG = "AudioRecordError";
    private long startRecordingTime;
    private boolean recording = false;
    private MediaRecorder mediaRecorder;
    private String fileName;
    private boolean permissionToRecordAndSaveDataAccepted = false;
    private AudioRecordViewModel viewModel;
    private AudioRecordsAdapter adapter;
    private SimpleExoPlayer player;
    private DataSource.Factory dataSourceFactory;
    private boolean isPlaying = false;
    private Handler handler;
    private Runnable runnableSeekBar;
    private Runnable runnableTextView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.main_recycle_view);
        ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_RECORD_AUDIO_PERMISSION_AND_SAVE_DATA);
        path = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath();
        viewModel = ViewModelProviders.of(this).get(AudioRecordViewModel.class);
        adapter = new AudioRecordsAdapter(this, this::prepareExoPlayer,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subscribeUiRecord();
        Button btnRecord = findViewById(R.id.main_btnStartRecord);
        btnRecord.setOnTouchListener(this);
    }

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
        startRecordingTime = System.currentTimeMillis();
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
        AudioRecord audioRecord = new AudioRecord(fileName,path,getRecordTime(startRecordingTime));
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
                    startRecord();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (recording) {
                    stopRecord();
                }
                break;
        }
        return true;
    }

    private void subscribeUiRecord(){
        viewModel.getRecords().observe(this, audioRecords -> adapter.setAudioRecords(audioRecords));
    }

    public static String getRecordTime(long startRecordingTime){
        long endRecordingTime = (System.currentTimeMillis() - startRecordingTime) / 1000;
        int min = (int) (endRecordingTime / 60);
        int sec = (int) (endRecordingTime % 60);
        if (sec < 10){
           return min + ":" + "0" + sec;
        } else {
            return min + ":" + sec;
        }
    }

    private void initializeExoPlayer(){
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
            player.addListener(this);
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
        AudioRecord audioRecord = (AudioRecord) view.getTag(AudioRecordsAdapter.CURRENT_AUDIO_RECORD);
        File audioTrack = new File(audioRecord.getPath());
        Uri trackUri = Uri.fromFile(audioTrack);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(trackUri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playWhenReady && playbackState == Player.STATE_READY){
            isPlaying = true;
            handler.post(runnableSeekBar);
            //handler.post(runnableTextView);
        } else {
            isPlaying = false;
        }
    }

    @Override
    public void seekBarStateChange(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar.setProgress(0);
        seekBar.setMax((int) player.getDuration() / 1000 );

        if (handler == null){
            handler = new Handler();
        }

        runnableSeekBar = new Runnable() {
            @Override
            public void run() {
                if (player != null && isPlaying) {
                    seekBar.setProgress((int) (player.getCurrentPosition() / 1000));
                    handler.postDelayed(this, 1000);
                }
            }
        };
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds =  timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%01d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    public void currentTimeChange(TextView textView) {
        runnableTextView = new Runnable() {
            @Override
            public void run() {
                if (player != null && isPlaying){
                    textView.setText(stringForTime((int) player.getCurrentPosition()));
                    handler.postDelayed(this,1000);
                }
            }
        };
    }
}
