package com.example.soundday.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundday.Adapter.CalendarAdapter;
import com.example.soundday.Adapter.DiaryListAdapter;
import com.example.soundday.Adapter.RecentDiaryListAdapter;
import com.example.soundday.DB.Diary;
import com.example.soundday.Model.MainActivityViewModel;
import com.example.soundday.R;
import com.example.soundday.databinding.ActivityMainBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements CalendarAdapter.HandleCalendarClick, DiaryListAdapter.HandleDiaryClick
        , RecentDiaryListAdapter.HandleRecentDiaryClick{

    private ActivityMainBinding binding; //뷰 바인딩을 통해 findviewbyid를 없애줌
    private MainActivityViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //월-일 표시하기
        initCalendarView(binding.rcvCalendarDay, new ArrayList<String>(Arrays.asList("S", "M", "T", "W", "T", "F", "S")));
        //달력 표시하기
        initCalendarView(binding.rcvCalendar, makeCalendarDate());

        statusbarTransparent();

        //리사이클러뷰
        binding.rcvRecentDiaryList.setHasFixedSize(true);
        binding.rcvRecentDiaryList.setLayoutManager(new LinearLayoutManager(this));
        RecentDiaryListAdapter recentDiaryListAdapter = new RecentDiaryListAdapter(this, this);
        binding.rcvRecentDiaryList.setAdapter(recentDiaryListAdapter);

        //뷰 모델 초기화
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(
                getApplication())).get(MainActivityViewModel.class);

        CallObverser(recentDiaryListAdapter);
        viewModel.getRecentDiaryList();
    }

    //CalendarAdapter.HandleCalendarClick interface 구현 (리사이클러뷰 외부에서 아이템 클릭 이벤트 처리하기 위함)
    @Override
    public void calendarItemClick(String day) {
        showDiaryListDialog(day);
    }

    //DiaryAdapter.HandleDiaryClick interface 구현
    public void DiaryItemClick(Diary diary) {
        if (diary.completed) {
            //채팅or일기 선택지
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialog);
            builder.setTitle("-" + diary.diaryName + "-");
            String[] items = new String[]{"채팅으로 보기", "일기장으로 보기"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int pos) {
                    if(pos==0){
                        //채팅
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        intent.putExtra("diaryName", diary.diaryName);
                        intent.putExtra("completed", diary.completed);
                        intent.putExtra("diaryId", diary.id);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
                        intent.putExtra("completed", diary.completed);
                        intent.putExtra("diaryId", diary.id);
                        startActivity(intent);
                    }
                }
            });
            builder.show();

        } else {
            //채팅으로 이동
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("diaryName", diary.diaryName);
            intent.putExtra("completed", diary.completed);
            intent.putExtra("diaryId", diary.id);
            startActivity(intent);
        }
    }

    //RecentDiaryAdapter.HandleRecentDiaryClick interface 구현
    @Override
    public void RecentDiaryItemClick(Diary diary) {
//        Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
//        intent.putExtra("diaryId", diary.id);
//        startActivity(intent);
    }

    //Method---------------------
    private void initCalendarView(RecyclerView recyclerView, ArrayList<String> list) {
        CalendarAdapter calendarAdapter = new CalendarAdapter(this, list, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        recyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> makeCalendarDate() {
        ArrayList<String> dayList = new ArrayList<String>();
        Calendar mCal;

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        //연,월,일을 따로 저장
        SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        mCal = Calendar.getInstance();
        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);

        //1일 - 요일 매칭 시키기 위해 공백 add
        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }

        mCal.set(Calendar.MONTH, mCal.get(Calendar.MONTH));

        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }
        return dayList;
    }

    private void showDiaryListDialog(String day) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_bs_dairylist, null);
        String date = "2021 01 " + day;

        //image_Create
        ImageView image_create = dialogView.findViewById(R.id.image_create);
        image_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Diary diary = new Diary();
                diary.completed = false;
                diary.diaryName = date;
                diary.contents = "일기 내용";

                //Diary 데이터 넣어줌
                viewModel.insertDiary(diary);
            }
        });

        TextView tv_noresult = dialogView.findViewById(R.id.tv_noresult);

        //리사이클러뷰
        RecyclerView rcv_diarylist = dialogView.findViewById(R.id.rcv_dairylist);
        rcv_diarylist.setHasFixedSize(true);
        rcv_diarylist.setLayoutManager(new LinearLayoutManager(this));
        DiaryListAdapter diaryListAdapter = new DiaryListAdapter(this, this);
        rcv_diarylist.setAdapter(diaryListAdapter);

        //옵저버 불러옴
        CallObverser(tv_noresult,rcv_diarylist, diaryListAdapter);

        //현재 db에 있는 값들 recyclerview에 뿌려줌
        viewModel.getAllDiaryList(date);
        viewModel.getRecentDiaryList();

        //Bottomsheet
        BottomSheetDialog BottomSheet;
        BottomSheet = new BottomSheetDialog(this);
        BottomSheet.setContentView(dialogView);
        BottomSheet.show();
    }

    private void statusbarTransparent() {
        //style AppTheme에 <item name="android:statusBarColor">@android:color/transparent</item>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    //뷰 모델 초기화 [[수정 필요]]
    private void CallObverser(TextView tv_noresult, RecyclerView rcv_diarylist, DiaryListAdapter diaryListAdapter){
        viewModel.getDiaryListObserver().observe(this, new Observer<List<Diary>>() {
            @Override
            public void onChanged(List<Diary> diaries) {
                if (diaries == null) {
                    tv_noresult.setVisibility(View.VISIBLE);
                    rcv_diarylist.setVisibility(View.INVISIBLE);
                } else {
                    rcv_diarylist.setVisibility(View.VISIBLE);
                    tv_noresult.setVisibility(View.INVISIBLE);
                    diaryListAdapter.setDiaryList(diaries);
                    rcv_diarylist.smoothScrollToPosition(diaries.size() - 1);
                }
            }
        });
    }

    private void CallObverser(RecentDiaryListAdapter recentDiaryListAdapter){
        //DiaryList들이 실시간으로 반영되도록 observe함
        //데이터바인딩을 이용하면, observe패턴을 쓸 필요없이 xml에서 처리가 가능하다
        viewModel.getRecentDiaryListObserver().observe(this, new Observer<List<Diary>>() {
            @Override
            public void onChanged(List<Diary> diaries) {
                if (diaries == null) {
                    binding.tvNoResult.setVisibility(View.VISIBLE);
                } else {
                    binding.tvNoResult.setVisibility(View.INVISIBLE);
                    recentDiaryListAdapter.setRecentDiaryList(diaries);
                    viewModel.getRecentDiaryList();
                }
            }
        });
    }
}