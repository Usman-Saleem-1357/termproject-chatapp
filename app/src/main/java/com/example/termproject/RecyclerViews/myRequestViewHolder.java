package com.example.termproject.RecyclerViews;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termproject.IndividualUserView;
import com.example.termproject.R;

public class myRequestViewHolder extends RecyclerView.ViewHolder {
    public TextView idview,userNameView,locationView;
    public String uid;
    public myRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        idview = itemView.findViewById(R.id.requserid);
        userNameView = itemView.findViewById(R.id.requsername);
        locationView = itemView.findViewById(R.id.requserlocation);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), IndividualUserView.class);
                intent.putExtra("uid",uid);
                itemView.getContext().startActivity(intent);
            }
        });
    }
    public void setData(String id,String name,String location)
    {
        idview.setText(id);
        userNameView.setText(name);
        locationView.setText(location);
    }
}
