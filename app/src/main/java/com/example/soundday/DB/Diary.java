package com.example.soundday.DB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Diary {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="diaryName")
    public String diaryName;

    @ColumnInfo(name="contents")
    public String contents;

    @ColumnInfo(name="completed")
    public boolean completed;

}
