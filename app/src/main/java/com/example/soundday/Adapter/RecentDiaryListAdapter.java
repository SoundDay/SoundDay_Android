package com.example.soundday.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.soundday.DB.Diary;
import com.example.soundday.R;

import java.util.List;

public class RecentDiaryListAdapter extends RecyclerView.Adapter<RecentDiaryListAdapter.MyViewHolder> {

    private Context context;
    private List<Diary> DiaryList;
    private HandleRecentDiaryClick clickListener; //interface

    public RecentDiaryListAdapter(Context context, HandleRecentDiaryClick clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    //adapter를 호출하면 가장 먼저 동작하여 viewholder를 생성
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_recent_diary, parent, false);
        return new MyViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_diaryName.setText(this.DiaryList.get(position).diaryName);
        holder.tv_diaryContents.setText(this.DiaryList.get(position).contents);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.RecentDiaryItemClick(DiaryList.get(position));
            }
        });
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_diaryName;
        TextView tv_diaryContents;

        MyViewHolder(View itemView) {
            super(itemView);
            tv_diaryName = itemView.findViewById(R.id.tv_diaryName);
            tv_diaryContents = itemView.findViewById(R.id.tv_diaryContents);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        if (DiaryList == null || DiaryList.size() == 0) return 0;
        if(DiaryList.size() > 2){
            return 2;
        }
        else {
            return DiaryList.size();
        }
    }

    //바뀐 DiaryList가 리사이클러뷰에 적용되도록 adapter에게 알려줌
    //UserMainActivity의 getDiaryListObserver()에서 호출하는 method
    public void setRecentDiaryList(List<Diary> DiaryList) {
        this.DiaryList = DiaryList;
        notifyDataSetChanged();
    }

    //HandleDiaryClick interface
    public interface HandleRecentDiaryClick {
        void RecentDiaryItemClick(Diary Diary);
    }
}
