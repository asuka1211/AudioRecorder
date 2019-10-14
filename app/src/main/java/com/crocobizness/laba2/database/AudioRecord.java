package com.crocobizness.laba2.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AudioRecord {

    public AudioRecord(String name,String path, String time) {
        this.name = name;
        this.path = path;
        this.time = time;
    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    private String path;

    private String time;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
