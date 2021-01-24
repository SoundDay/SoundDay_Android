package com.example.soundday.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Chat.class, Diary.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DiaryDao DiaryDao();

    //db 생성을 액티비티에서 코드로 직접 실행하지 않고,
    //instance를 받아서 실행하도록
    public static AppDatabase INSTANCE;
    public static AppDatabase getDBinstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class,"DB")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

}
