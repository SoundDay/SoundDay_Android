package com.example.soundday.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.soundday.Adapter.CalendarAdapter;
import com.example.soundday.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements CalendarAdapter.HandleCalendarClick {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //월-일 표시하기
        RecyclerView rcv_calendarDay = findViewById(R.id.rcv_calendarDay);
        initRecyclerView(rcv_calendarDay, new ArrayList<String>(Arrays.asList("S", "M", "T", "W", "T", "F", "S")));

        //달력 표시하기
        RecyclerView rcv_calendar = findViewById(R.id.rcv_calendar);
        initRecyclerView(rcv_calendar, makeCalendarDate());
    }



    //CalendarAdapter.HandleCalendarClick interface 구현
    @Override
    public void itemClick(String day) {
        showDiaryListDialog();
    }



    //Method-------
    private void initRecyclerView(RecyclerView recyclerView, ArrayList<String> list) {
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

    private void showDiaryListDialog(){
        View view = getLayoutInflater().inflate(R.layout.dialog_bs_dairylist, null);

        BottomSheetDialog BottomSheet;
        BottomSheet = new BottomSheetDialog(this);
        BottomSheet.setContentView(view);
        BottomSheet.show();
    }
}