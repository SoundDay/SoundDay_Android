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
    private MutableLiveData<Diary> diaryObject;

    public DiaryActivityViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getDBinstance(getApplication().getApplicationContext());
    }

    public MutableLiveData<Diary> getDiaryObjectObserver() {
        if (diaryObject == null) {
            diaryObject = new MutableLiveData<Diary>();
        }
        return diaryObject;
    }

    public Diary getDiaryObject(int dairy_id) {
        Diary diary = db.DiaryDao().getDiaryObject(dairy_id);
        if (diary == null) {
            diaryObject.postValue(null);
        } else {
            diaryObject.postValue(diary);
        }
        return diary;
    }
}
