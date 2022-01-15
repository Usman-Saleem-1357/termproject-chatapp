package com.example.termproject.RecyclerViews;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.termproject.IndividualUserView;
import com.example.termproject.R;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class myNearbyViewHolder extends RecyclerView.ViewHolder {
    private final TextView userNameView,locationView;
    public CircleImageView userImage;
    public String uid;
    public myNearbyViewHolder(@NonNull View itemView) {
        super(itemView);
        userNameView = itemView.findViewById(R.id.usernameinlist);
        locationView = itemView.findViewById(R.id.locationinlist);
        userImage = itemView.findViewById(R.id.userPic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), IndividualUserView.class);
                intent.putExtra("uid",uid);
                itemView.getContext().startActivity(intent);
            }
        });
    }

    public void setData(String username,String location,String uid)
    {
        userNameView.setText(username);
        locationView.setText(location);
        this.uid = uid;
    }
}
