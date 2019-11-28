package com.crocobizness.laba2.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.crocobizness.laba2.database.entity.AudioRecord;

import java.util.List;

@Dao
public interface AudioRecordDao {

    @Query("SELECT * FROM audiorecord")
    LiveData<List<AudioRecord>> getRecords();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(AudioRecord audioRecord);

    @Delete
    void delete(AudioRecord audioRecord);

}
