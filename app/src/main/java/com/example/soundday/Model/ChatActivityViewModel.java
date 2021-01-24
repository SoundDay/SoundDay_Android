package com.example.soundday.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.soundday.DB.AppDatabase;
import com.example.soundday.DB.Chat;

import java.util.List;

public class ChatActivityViewModel extends AndroidViewModel {

    //원래는 LiveData를 Dao에다가 getAll()의 return값을 LiveData<List<>> 로 했는데,
    //이번에는 MutableLiveData<List<Items>> 이렇게 했다.
    //MutableLiveData는 메서드를 이용해서 값 변경이 가능하고
    //LiveData는 값 변경이 불가능하다.

    //Dao에서 LiveData< >를 반환하지 않고, 일단 List<>를 반환한다음
    //ViewModel에서 MutableLiveData<List<Chat>> listOfChat을 이용해 넣어준다.
    private MutableLiveData<List<Chat>> listOfChat;
    private AppDatabase db;

    public ChatActivityViewModel(@NonNull Application application) {
        super(application);

        listOfChat = new MutableLiveData<>();

        db = AppDatabase.getDBinstance(getApplication().getApplicationContext());
    }

    //MutableLiveData<List<Chat>>를 반환하여 실시간 변화에 대해 observer를 하도록 한다.
    public MutableLiveData<List<Chat>> getChatListObserver(){
        return listOfChat;
    }

    public void getAllChatList(int dairy_id){
        List<Chat> ChatList = db.DiaryDao().getAllChatList(dairy_id);
        //db에 있는 Chat들 중 diary_Id인 애들을 ChatList에 담는다.

        if(ChatList.size() > 0){
            //postValue : 값을 set하는데, 백그라운드에서 실행
            listOfChat.postValue(ChatList);
        }
        else {//만약 ItemsLists가 null이면
            listOfChat.postValue(null);
        }
    }

    public void insertChat(Chat chat){
        db.DiaryDao().insertChat(chat);
        getAllChatList(chat.diaryId);
    }

    public void updateChat(Chat chat){
        db.DiaryDao().updateChat(chat);
        getAllChatList(chat.diaryId);
    }

    public void deleteChat(Chat chat){
        db.DiaryDao().deleteChat(chat);
        getAllChatList(chat.diaryId);
    }
}
