package com.example.termproject.RecyclerViews;

import android.view.View;
import android.widget.TextView;
import com.example.termproject.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    public TextView send,receive;
    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        send = itemView.findViewById(R.id.sender_side);
        receive = itemView.findViewById(R.id.receiver_side);
    }
}
