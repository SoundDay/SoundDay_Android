package com.example.soundday.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundday.R;

import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<String> list;
    private HandleCalendarClick clickListener;  //interface

    public CalendarAdapter(Context context, ArrayList<String> list, HandleCalendarClick clickListener) {
        this.list = list;
        this.context = context;
        this.clickListener = clickListener;
    }

    //adapter를 호출하면 가장 먼저 동작하여 viewholder를 생성
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_calendar_text, parent, false);
        return new CustomViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.tv_calendarText.setText(list.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!list.get(position).isEmpty() && list.size() != 7) {
                    String tmp = list.get(position);
                    if(tmp.length() == 1) tmp = "0" + tmp;
                    clickListener.calendarItemClick(tmp);
                }
            }
        });
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tv_calendarText;
        CustomViewHolder(View itemView) {
            super(itemView);
            tv_calendarText = itemView.findViewById(R.id.tv_calendarText);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return list.size();
    }

    //HandleCalendarClick interface
    //리사이클러뷰 외부에서 아이템 클릭 이벤트 처리하기 위함
    public interface HandleCalendarClick {
        void calendarItemClick(String day);
    }
}

