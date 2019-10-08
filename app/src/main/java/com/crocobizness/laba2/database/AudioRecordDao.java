package com.crocobizness.laba2.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AudioRecordDao {

    @Query("SELECT * FROM audiorecord WHERE id = :id")
    AudioRecord getById(long id);

    @Insert
    void insert(AudioRecord audioRecord);

    @Delete
    void delete(AudioRecord audioRecord);

}
