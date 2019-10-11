package com.crocobizness.laba2.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.crocobizness.laba2.database.AppDatabase;
import com.crocobizness.laba2.database.AudioRecord;
import com.crocobizness.laba2.database.AudioRecordDao;

import java.util.List;

public class DataRepository {

    private AudioRecordDao audioRecordDao;
    private LiveData<List<AudioRecord>> records;

    DataRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        audioRecordDao = db.audioRecordDao();
        records = audioRecordDao.getRecors();
    }

    public LiveData<List<AudioRecord>> getRecords() {
        return records;
    }

    public void deleteRecord(AudioRecord audioRecord){
        new DeleteAsyncTask(audioRecordDao).doInBackground(audioRecord);
    }

    public void insertRecord(AudioRecord audioRecord){
        new InsertAsyncTask(audioRecordDao).doInBackground(audioRecord);
    }

    private static class InsertAsyncTask extends AsyncTask<AudioRecord,Void,Void>{

        private AudioRecordDao mDao;

        InsertAsyncTask(AudioRecordDao audioRecordDao){
            mDao = audioRecordDao;
        }

        @Override
        protected Void doInBackground(AudioRecord... audioRecords) {
            mDao.insert(audioRecords[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<AudioRecord,Void,Void>{

        private AudioRecordDao mDao;

        DeleteAsyncTask(AudioRecordDao audioRecordDao){
            mDao = audioRecordDao;
        }

        @Override
        protected Void doInBackground(AudioRecord... audioRecords) {
            mDao.delete(audioRecords[0]);
            return null;
        }
    }
}
