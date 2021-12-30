package com.example.termproject;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

public class myNearbyViewHolder extends RecyclerView.ViewHolder {
    private final TextView emailview,userNameView,locationView;


    public myNearbyViewHolder(@NonNull View itemView) {
        super(itemView);
        emailview = itemView.findViewById(R.id.emailinlist);
        userNameView = itemView.findViewById(R.id.usernameinlist);
        locationView = itemView.findViewById(R.id.locationinlist);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),locationView.getText() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setData(String username,String location,String uid)
    {
        userNameView.setText(username);
        locationView.setText(location);
    }
}
