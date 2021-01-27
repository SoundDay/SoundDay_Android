package com.example.soundday.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.soundday.DB.Chat;
import com.example.soundday.DB.Diary;
import com.example.soundday.Model.DiaryActivityViewModel;
import com.example.soundday.Model.MainActivityViewModel;
import com.example.soundday.R;
import com.example.soundday.databinding.ActivityDiaryBinding;

import java.util.List;

public class DiaryActivity extends AppCompatActivity {

    //intent로 받는 애
    private int diary_id;
    private boolean completed;

    private ActivityDiaryBinding binding;
    private DiaryActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary);

        //intent로 받아옴
        diary_id = getIntent().getIntExtra("diaryId",0);
        completed = getIntent().getBooleanExtra("completed", true);

        statusbarTransparent();

        //툴바
        funcToolbar();

        //completed 처리
        if(completed) {
            binding.tvComplete.setVisibility(View.INVISIBLE);
        }
        else{
            binding.tvComplete.setVisibility(View.VISIBLE);
        }

        //뷰모델 초기화
        initViewModel();

        //db에서 값 끌고와서 뿌려줌
        Diary diary = viewModel.getDiaryObject(diary_id);
        setDiaryObject(diary);

        //complete 클릭
        binding.tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(DiaryActivity.this, "ㅅㅂ", Toast.LENGTH_SHORT).show();
                diary.completed = true;
                viewModel.updateDiary(diary);
                finish();
            }
        });
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

    private void statusbarTransparent() {
        //style AppTheme에 <item name="android:statusBarColor">@android:color/transparent</item>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private void setDiaryObject(Diary diary){
        if(diary==null){
            Toast.makeText(this, "[오류 발생] 오류 신고 부탁드립니다.", Toast.LENGTH_SHORT).show();
        }
        else{
            //diary.contents = "집에 있는 시간이 너무 길어지니깐 답답하고 기분도 다운되는거\n같아 이런 기분을 어떻게 해소할 수 있을지 모르겠어";
            binding.tvDiaryName.setText(diary.diaryName);
            binding.tvContents.setText(diary.contents);
        }
    }
}