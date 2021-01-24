package com.example.soundday.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.soundday.DB.Diary;
import com.example.soundday.Model.ChatActivityViewModel;
import com.example.soundday.Model.DiaryActivityViewModel;
import com.example.soundday.R;
import com.example.soundday.databinding.ActivityChatBinding;
import com.example.soundday.databinding.ActivityDiaryBinding;

public class DiaryActivity extends AppCompatActivity {

    //intent로 받는 애
    private int diary_id;
    private String diary_Name;

    private ActivityDiaryBinding binding;
    private DiaryActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        //intent로 받아옴
        diary_id = getIntent().getIntExtra("diaryId",0);
        diary_Name = getIntent().getStringExtra("diaryName");

        //툴바
        funcToolbar();

        //db에서 값 끌고와서 뿌려줌
        Diary diary = viewModel.getDiary(diary_id);

    }


    //Method-------------

    //툴바
    private void funcToolbar(){
        binding.tvDiaryName.setText(diary_Name);
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}