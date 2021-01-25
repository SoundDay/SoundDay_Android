package com.example.soundday.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DiaryDao {
    //Entity가 여러개여도 Dao 하나로 작동
    
    //----------Chat-------------
    //Chat 테이블 중 diaryId가 매개변수(diary_id)와 같은 것만 불러오겠다
    @Query("Select * from Chat where diaryId = :diary_id")
    List<Chat> getAllChatList(int diary_id);

    @Insert
    void insertChat(Chat...Chat); //... : 일정하지 않은 개수의 파라미터

    @Update
    void updateChat(Chat Chat);

    @Delete
    void deleteChat(Chat Chat);

    
    //---------Diary-------------

    //Diary 테이블 중 diaryName가 매개변수(diary_name)와 같은 것만 불러오겠다
    @Query("Select * from Diary where diaryName = :diary_name")
    List<Diary> getAllDiaryList(String diary_name);

    @Query("Select * from Diary where id = :diary_id")
    Diary getDiaryObject(int diary_id);

    @Query("Select * from Diary Where completed=1 ORDER BY diaryName DESC")
    List<Diary> getRecentDiaryList();

    @Insert
    void insertDiary(Diary Diary);

    @Update
    void updateDiary(Diary Diary);

    @Delete
    void deleteDiary(Diary Diary);
}
