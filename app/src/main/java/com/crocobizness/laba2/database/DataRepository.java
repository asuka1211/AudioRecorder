package com.crocobizness.laba2.database;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;

import com.crocobizness.laba2.database.dao.AudioRecordDao;
import com.crocobizness.laba2.database.entity.AudioRecord;

import java.io.File;
import java.util.List;

public class DataRepository {

    private AudioRecordDao audioRecordDao;
    private LiveData<List<AudioRecord>> records;

    public DataRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        audioRecordDao = db.audioRecordDao();
        records = audioRecordDao.getRecords();
    }

    public LiveData<List<AudioRecord>> getRecords() {
        return records;
    }

    public void deleteRecord(AudioRecord audioRecord){
        new DatabaseAsyncTask(audioRecordDao,DatabaseAsyncTask.DELETE).execute(audioRecord);
    }

    public void insertRecord(AudioRecord audioRecord){
        new DatabaseAsyncTask(audioRecordDao,DatabaseAsyncTask.INSERT).execute(audioRecord);
    }

    private static class DatabaseAsyncTask extends AsyncTask<AudioRecord,Void,Void>{

        private AudioRecordDao mDao;
        private static final int INSERT = 0;
        private static final int DELETE = 1;
        private int mode;

        DatabaseAsyncTask(AudioRecordDao audioRecordDao, int mode){
            mDao = audioRecordDao;
            this.mode = mode;
        }

        @Override
        protected Void doInBackground(AudioRecord... audioRecords) {
            switch (mode){
                case INSERT:
                    mDao.insert(audioRecords[0]);
                    break;
                case DELETE:
                    File file = new File(audioRecords[0].getPath());
                    boolean check = file.delete();
                    mDao.delete(audioRecords[0]);
                    break;
            }
            return null;
        }
    }

}
