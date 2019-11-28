package com.crocobizness.laba2.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.crocobizness.laba2.database.DataRepository;
import com.crocobizness.laba2.database.entity.AudioRecord;

import java.util.List;

public class AudioRecordViewModel extends AndroidViewModel {

    private DataRepository dataRepository;
    private LiveData<List<AudioRecord>> records;

    public AudioRecordViewModel(@NonNull Application application) {
        super(application);
        this.dataRepository = new DataRepository(application);
        this.records = dataRepository.getRecords();
    }

    public void insert(AudioRecord audioRecord){
        dataRepository.insertRecord(audioRecord);
    }

    public void delete(AudioRecord audioRecord){
        dataRepository.deleteRecord(audioRecord);
    }

    public LiveData<List<AudioRecord>> getRecords(){
        return records;
    }
}
