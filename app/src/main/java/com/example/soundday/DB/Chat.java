package com.example.soundday.DB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Chat {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="diaryId")
    public int diaryId;

    @ColumnInfo(name="contents")
    public String contents;

    @ColumnInfo(name="user")
    public boolean user;
}
