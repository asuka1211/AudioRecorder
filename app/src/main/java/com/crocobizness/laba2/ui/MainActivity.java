package com.crocobizness.laba2.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.crocobizness.laba2.AudioRecordViewModel;
import com.crocobizness.laba2.R;
import com.crocobizness.laba2.database.AudioRecord;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION_AND_SAVE_DATA = 200;
    private static final String LOG_TAG = "AudioRecordError";
    private long startRecordingTime;
    private long endRecordingTime;
    private boolean recording = false;
    private MediaRecorder mediaRecorder;
    private String path;
    private String fileName;
    private boolean permissionToRecordAndSaveDataAccepted = false;
    private boolean permissionToSaveExternalStorage = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private AudioRecordViewModel viewModel;
    private AudioRecordsAdapter adapter;
    private RecyclerView recyclerView;

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
        path = getExternalCacheDir().getAbsolutePath();
        Button btnRecord = (Button) findViewById(R.id.main_btnStartRecord);
        Button btnPlay = (Button) findViewById(R.id.audio_item_btnPlay);
        btnRecord.setOnTouchListener(this);
        viewModel = ViewModelProviders.of(this).get(AudioRecordViewModel.class);
        adapter = new AudioRecordsAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subscribeUiRecord();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION_AND_SAVE_DATA) {
            permissionToRecordAndSaveDataAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
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
        viewModel.getRecords().observe(this, new Observer<List<AudioRecord>>() {
            @Override
            public void onChanged(List<AudioRecord> audioRecords) {
                adapter.setAudioRecords(audioRecords);
            }
        });
    }

    private String getRecordTime(long startRecordingTime){
        long endRecordingTime = (System.currentTimeMillis() - startRecordingTime) / 1000;
        String time;
        int min = (int) (endRecordingTime / 60);
        int sec = (int) (endRecordingTime % 60);
        if (sec < 10){
           return time = min + ":" + "0" + sec;
        } else {
            return time = min + ":" + sec;
        }
    }
}
