package com.example.termproject.RecyclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.termproject.Models.ChatModel;
import com.example.termproject.Models.DBHelper;
import com.example.termproject.R;
import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    public List<ChatModel> chat;
    String uid;
    public DBHelper dbHelper;

    public ChatRecyclerAdapter(List<ChatModel> chat){
        this.chat = chat;
        dbHelper = new DBHelper();
    }

    public void setData(List<ChatModel> chat,String uid){
        this.chat = chat;
        this.uid = uid;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            if(chat.get(position).getSenderid().equals(dbHelper.getUID())){
                if(!uid.equals("") && chat.get(position).getReceiverid().equals(uid)){
                    holder.send.setVisibility(View.VISIBLE);
                    holder.receive.setVisibility(View.GONE);
                    holder.send.setText(chat.get(position).getMessage());
                }
            }
            else if(chat.get(position).getReceiverid().equals(dbHelper.getUID())){
                if(!uid.equals("") && chat.get(position).getSenderid().equals(uid)) {
                    holder.send.setVisibility(View.GONE);
                    holder.receive.setVisibility(View.VISIBLE);
                    holder.receive.setText(chat.get(position).getMessage());
                }
            }
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }
}
