package com.example.soundday.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.soundday.DB.Chat;
import com.example.soundday.DB.Diary;
import com.example.soundday.Model.DiaryActivityViewModel;
import com.example.soundday.R;
import com.example.soundday.databinding.ActivityDiaryBinding;

import java.util.List;

public class DiaryActivity extends AppCompatActivity {

    //intent로 받는 애
    private int diary_id;
    private String diary_Name;

    private ActivityDiaryBinding binding;
    private DiaryActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary);

        //intent로 받아옴
        diary_id = getIntent().getIntExtra("diaryId",0);
       // diary_Name = getIntent().getStringExtra("diaryName");

        //툴바
        funcToolbar();

        //뷰모델 초기화
        initViewModel();

        //db에서 값 끌고와서 뿌려줌
        Diary diary = viewModel.getDiaryObject(diary_id);
        setDiaryObject(diary);
    }

    //Method-------------

    //툴바
    private void funcToolbar(){
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViewModel(){
        viewModel = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(
                getApplication())).get(DiaryActivityViewModel.class);

        viewModel.getDiaryObjectObserver().observe(this, new Observer<Diary>() {
            @Override
            public void onChanged(Diary diary) {
                //diary가 비어있는 경우
                if(diary==null){
                    binding.tvContents.setVisibility(View.INVISIBLE);
                    binding.tvDiaryName.setVisibility(View.INVISIBLE);
                    Toast.makeText(DiaryActivity.this, "[오류 발생] 오류 신고 부탁드립니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    binding.tvContents.setVisibility(View.VISIBLE);
                    binding.tvDiaryName.setVisibility(View.VISIBLE);
                    setDiaryObject(diary);
                }
            }
        });
    }

    private void setDiaryObject(Diary diary){
        if(diary==null){
            Toast.makeText(this, "[오류 발생] 오류 신고 부탁드립니다.", Toast.LENGTH_SHORT).show();
        }
        else{
            binding.tvDiaryName.setText(diary.diaryName);
            binding.tvContents.setText(diary.contents);
        }
    }
}