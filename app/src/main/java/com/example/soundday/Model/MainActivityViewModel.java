package com.example.soundday.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.soundday.DB.AppDatabase;
import com.example.soundday.DB.Diary;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    //원래는 LiveData를 Dao에다가 getAll()의 return값을 LiveData<List<>> 로 했는데,
    //이번에는 MutableLiveData<List<Items>> 이렇게 했다.
    //MutableLiveData는 메서드를 이용해서 값 변경이 가능하고
    //LiveData는 값 변경이 불가능하다.

    //Dao에서 LiveData< >를 반환하지 않고, 일단 List<>를 반환한다음
    //ViewModel에서 MutableLiveData<List<Diary>> listOfDiary을 이용해 넣어준다.
    private MutableLiveData<List<Diary>> listOfDiary, listOfRecentDiary;
    private AppDatabase db;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        listOfDiary = new MutableLiveData<>();
        listOfRecentDiary = new MutableLiveData<>();
        db = AppDatabase.getDBinstance(getApplication().getApplicationContext());
    }

    //MutableLiveData<List<Diary>>를 반환하여 실시간 변화에 대해 observer를 하도록 한다.
    public MutableLiveData<List<Diary>> getDiaryListObserver(){
        return listOfDiary;
    }

    //[[수정 필요]]
    public MutableLiveData<List<Diary>> getRecentDiaryListObserver(){
        return listOfRecentDiary;
    }

    public void getAllDiaryList(String dairy_name){
        List<Diary> DiaryList = db.DiaryDao().getAllDiaryList(dairy_name);
        //db에 있는 Diary들 중 diary_Id인 애들을 DiaryList에 담는다.

        if(DiaryList.size() > 0){
            //postValue : 값을 set하는데, 백그라운드에서 실행
            listOfDiary.postValue(DiaryList);
        }
        else {//만약 ItemsLists가 null이면
            listOfDiary.postValue(null);
        }
    }

    public void insertDiary(Diary Diary){
        db.DiaryDao().insertDiary(Diary);
        getAllDiaryList(Diary.diaryName);
    }

    public void updateDiary(Diary Diary){
        db.DiaryDao().updateDiary(Diary);
        getAllDiaryList(Diary.diaryName);
    }

    public void deleteDiary(Diary Diary){
        db.DiaryDao().deleteDiary(Diary);
        getAllDiaryList(Diary.diaryName);
    }

    public void getRecentDiaryList(){
        List<Diary> DiaryList = db.DiaryDao().getRecentDiaryList();
        if(DiaryList.size() > 0){
            //postValue : 값을 set하는데, 백그라운드에서 실행
            listOfRecentDiary.postValue(DiaryList);
        }
        else {//만약 ItemsLists가 null이면
            listOfRecentDiary.postValue(null);
        }
    }
}
