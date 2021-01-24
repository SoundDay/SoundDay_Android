package com.example.soundday.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.soundday.DB.AppDatabase;
import com.example.soundday.DB.Chat;
import com.example.soundday.DB.Diary;

import java.util.List;

public class DiaryActivityViewModel extends AndroidViewModel {

    private AppDatabase db;

    public DiaryActivityViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getDBinstance(getApplication().getApplicationContext());
    }

    public Diary getDiary(int dairy_id){
        Diary diary = db.DiaryDao().getDiary(dairy_id);
        return diary;
    }
}
