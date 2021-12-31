package com.example.termproject;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class myRequestViewHolder extends RecyclerView.ViewHolder {
    private final TextView idview,userNameView,locationView;
    public String uid;
    public myRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        idview = itemView.findViewById(R.id.idinlist);
        userNameView = itemView.findViewById(R.id.usernameinlist);
        locationView = itemView.findViewById(R.id.locationinlist);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(),IndividualUserView.class);
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
