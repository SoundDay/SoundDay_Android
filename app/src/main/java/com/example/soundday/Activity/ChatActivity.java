package com.example.soundday.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soundday.R;
import com.example.soundday.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {

    private String date;
    private ActivityChatBinding binding;
    //뷰 바인딩을 통해 findviewbyid를 없애줌

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        //UserMainActivity에서 값을 넘겨준 걸 받음
        date = getIntent().getStringExtra("diaryName");
        makeToolbar(binding.toolbar);
        binding.tvDiaryName.setText(date);
    }

    //Method--------
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
}