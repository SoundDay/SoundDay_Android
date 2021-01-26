package com.example.soundday.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.soundday.DB.Chat;
import com.example.soundday.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private Context context;
    private List<Chat> ChatList;
    private HandleChatClick clickListener; //interface

    public ChatAdapter(Context context, HandleChatClick clickListener){
        this.context=context;
        this.clickListener=clickListener;
    }

    //adapter를 호출하면 가장 먼저 동작하여 viewholder를 생성
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.listitem_chatbubble,parent,false);
        return new MyViewHolder(view);
    }


    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_contents.setText(this.ChatList.get(position).contents);

        //길게 누를 경우 삭제
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                clickListener.removeItem(ChatList.get(position));
//                return true;
//            }
//        });


        //user가 1(사용자인 경우)
        if(this.ChatList.get(position).user){
            holder.tv_user.setText("Me");
            holder.image_user.setImageResource(R.drawable.img_chat_user);
        }
        else{
            holder.tv_user.setText("SD");
            holder.image_user.setImageResource(R.drawable.img_chat_sd);
        }
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_user;
        TextView tv_contents;
        ImageView image_user;

        MyViewHolder(View itemView) {
            super(itemView) ;
            tv_user = itemView.findViewById(R.id.tv_user);
            tv_contents = itemView.findViewById(R.id.tv_contents);
            image_user = itemView.findViewById(R.id.image_user);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        if(ChatList==null||ChatList.size()==0) return 0;
        else return ChatList.size();
    }


    //바뀐 ChatList가 리사이클러뷰에 적용되도록 adapter에게 알려줌
    //ChatActivity의 getChatListObserver()에서 호출하는 method
    public void setChatList(List<Chat> ChatList){
        this.ChatList = ChatList;
        notifyDataSetChanged();
    }

    //HandleChatClick interface
    //구현 이유 : 리사이클러뷰 외부에서 아이템 클릭 이벤트 처리하기 위함
    public interface HandleChatClick{
        void removeItem(Chat Chat);
    }
}
