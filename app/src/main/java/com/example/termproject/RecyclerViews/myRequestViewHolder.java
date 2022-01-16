package com.example.termproject.RecyclerViews;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termproject.IndividualRequestView;
import com.example.termproject.IndividualUserView;
import com.example.termproject.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class myRequestViewHolder extends RecyclerView.ViewHolder {
    public TextView userNameView,locationView;
    public String uid;
    public CircleImageView userImage;
    public myRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        userNameView = itemView.findViewById(R.id.requsername);
        userImage = itemView.findViewById(R.id.requested_users_profile_pic);
        locationView = itemView.findViewById(R.id.requserlocation);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), IndividualRequestView.class);
                intent.putExtra("uid",uid);
                itemView.getContext().startActivity(intent);

            }
        });
    }
    public void setData(String id,String name,String location)
    {
        userNameView.setText(name);
        uid = id;
        locationView.setText(location);
    }
}
