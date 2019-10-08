package com.crocobizness.laba2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.crocobizness.laba2.database.AppDatabase;
import com.crocobizness.laba2.database.AudioRecord;
import com.crocobizness.laba2.database.AudioRecordDao;

import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String LOG_TAG = "AudioRecordError";
    private long startRecordingTime;
    private long endRecordingTime;
    private boolean recording = false;
    private MediaRecorder mediaRecorder;
    private String fileName;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileName = getExternalCacheDir().getAbsolutePath();
        Button btnRecord = (Button) findViewById(R.id.main_btnStartRecord);
        Button btnPlay = (Button) findViewById(R.id.audio_item_btnPlay);
        btnRecord.setOnTouchListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.audio_item_btnPlay){
        }
    }

    private void startRecord(){
        Date date = new Date();
        mediaRecorder = new MediaRecorder();
        startRecordingTime = System.currentTimeMillis();

        fileName += date.getTime();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mediaRecorder.start();
    }

    private void stopRecord(){
        mediaRecorder.stop();
        mediaRecorder.release();
        endRecordingTime = (System.currentTimeMillis() - startRecordingTime) * 1000;
        int min = (int) (endRecordingTime / 60);
        int sec = (int) (endRecordingTime - min);
        String time = String.valueOf(min) + " : " + String.valueOf(sec);
        AppDatabase db = App.getInstance().getDatabase();
        AudioRecordDao audioRecordDao = db.audioRecordDao();
        AudioRecord audioRecord = new AudioRecord(fileName,time);
        mediaRecorder = null;
    }

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
}
