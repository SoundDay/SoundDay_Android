package com.example.soundday.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soundday.Adapter.ChatAdapter;
import com.example.soundday.DB.Chat;
import com.example.soundday.Model.ChatActivityViewModel;
import com.example.soundday.R;
import com.example.soundday.databinding.ActivityChatBinding;

import java.util.List;

public class ChatActivity extends AppCompatActivity
implements ChatAdapter.HandleChatClick{

    private ActivityChatBinding binding;
    //뷰 바인딩을 통해 findviewbyid를 없애줌

    private ChatAdapter chatAdapter;
    private ChatActivityViewModel viewModel;

    //intent로 받는 애들
    private int diary_id;
    private String diary_Name;
    private boolean completed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        //UserMainActivity에서 값을 넘겨준 걸 받음
        diary_Name = getIntent().getStringExtra("diaryName");
        diary_id = getIntent().getIntExtra("diaryId",0);
        completed = getIntent().getBooleanExtra("completed",true);

        //툴바
        makeToolbar(binding.toolbar);
        binding.tvDiaryName.setText(diary_Name);

        //전송버튼
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contents = binding.etTyping.getText().toString();

                //사용자가 아무 값도 안넣고, 저장버튼 누르는 걸 막음
                if(TextUtils.isEmpty(contents)){
                    Toast.makeText(ChatActivity.this, "채팅을 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveNewChat(contents);
            }
        });

        initrecyclerview();
        initviewmodel();
        viewModel.getAllChatList(diary_id);
    }

    //Method--------
    private void saveNewChat(String contents) {
        //viewModel에 값을 넣어주기위해 item 객체를 만듦
        Chat item = new Chat();
        item.contents = contents;
        item.diaryId = diary_id;
        item.user = true;

        viewModel.insertChat(item);

        binding.etTyping.setText("");
        //값을 넣어줬으므로, edittext를 비워줌
    }

    //뷰모델
    private void initviewmodel() {
        viewModel = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(
                getApplication())).get(ChatActivityViewModel.class);

        //ChatList들이 실시간으로 반영되도록 observe함
        //데이터바인딩을 이용하면, observe패턴을 쓸 필요없이 xml에서 처리가 가능하다
        viewModel.getChatListObserver().observe(this, new Observer<List<Chat>>() {
            @Override
            public void onChanged(List<Chat> chats) {
                //items List가 비어있는 경우
                if(chats==null){
                    binding.rcvChat.setVisibility(View.INVISIBLE);
                }
                else{
                    binding.rcvChat.setVisibility(View.VISIBLE);
                    chatAdapter.setChatList(chats);
                    //바뀐 chat이 리사이클러뷰에 적용되도록 adapter에게 알려줌
                }
            }
        });
    }

    //리사이클러뷰
    private void initrecyclerview() {
        binding.rcvChat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter= new ChatAdapter(this, this);
        binding.rcvChat.setAdapter(chatAdapter);
    }

    //툴바
    private void makeToolbar(Toolbar tb){
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false); //기본 제목을 없애줌
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher_foreground);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                Toast.makeText(getApplicationContext(), "완료", Toast.LENGTH_LONG).show();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //ChatAdapter의 HandleChatClick interface 구현------
    @Override
    public void removeItem(Chat Chat) {

    }
}