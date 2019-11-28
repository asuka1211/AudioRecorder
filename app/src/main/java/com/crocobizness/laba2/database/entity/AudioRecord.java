package com.crocobizness.laba2.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AudioRecord {

    public AudioRecord(String name,String path) {
        this.name = name;
        this.path = path;
    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    private String path;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
