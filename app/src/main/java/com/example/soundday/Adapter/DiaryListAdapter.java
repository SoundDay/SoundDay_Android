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

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.MyViewHolder> {

    private Context context;
    private List<Diary> DiaryList;
    private HandleDiaryClick clickListener; //interface

    public DiaryListAdapter(Context context, HandleDiaryClick clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    //adapter를 호출하면 가장 먼저 동작하여 viewholder를 생성
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.listitem_diarylist, parent, false);
        return new MyViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (DiaryList.size() == 1) {
            holder.tv_diaryName.setText(this.DiaryList.get(position).diaryName);
        } else if (DiaryList.size() > 1) {
            int now = position + 1;
            holder.tv_diaryName.setText(this.DiaryList.get(position).diaryName + "   (" + now + ")");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.DiaryItemClick(DiaryList.get(position));
            }
        });

//        holder.bnt_edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickListener.editItem(DiaryList.get(position));
//            }
//        });
//        holder.bnt_remove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickListener.removeItem(DiaryList.get(position));
//            }
//        });
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_diaryName;

        MyViewHolder(View itemView) {
            super(itemView);
            tv_diaryName = itemView.findViewById(R.id.tv_diaryName);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        if (DiaryList == null || DiaryList.size() == 0) return 0;
        else return DiaryList.size();
    }

    //바뀐 DiaryList가 리사이클러뷰에 적용되도록 adapter에게 알려줌
    //UserMainActivity의 getDiaryListObserver()에서 호출하는 method
    public void setDiaryList(List<Diary> DiaryList) {
        this.DiaryList = DiaryList;
        notifyDataSetChanged();
    }

    //HandleDiaryClick interface
    //구현 이유 : 리사이클러뷰 외부(액티비티, 프래그먼트, ...)에서 아이템 클릭 이벤트 처리하기 위함
    public interface HandleDiaryClick {
        void DiaryItemClick(Diary Diary);
//        void editItem(Diary Diary);
//        void removeItem(Diary Diary);
    }
}
