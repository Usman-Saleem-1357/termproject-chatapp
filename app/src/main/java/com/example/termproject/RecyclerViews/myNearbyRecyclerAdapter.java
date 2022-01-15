package com.example.termproject.RecyclerViews;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.termproject.Models.DBHelper;
import com.example.termproject.Models.UserModel;
import com.example.termproject.R;

import java.util.List;

public class myNearbyRecyclerAdapter extends RecyclerView.Adapter<myNearbyViewHolder>{

    DBHelper dbHelper = new DBHelper();
    List<UserModel> userList;
    final UserModel[] currUser = {null};

    public myNearbyRecyclerAdapter(List<UserModel> userList,UserModel user)
    {
        final boolean[] comp = {true};
        currUser[0] = user;
        this.userList = userList;
    }

    public void setItems(List<UserModel> userList,UserModel user)
    {
        if(!user.getUsername().equals("")) {
            currUser[0] = user;
            this.userList = userList;
        }
    }
    //UserModel currUser;

    @Override
    public void onBindViewHolder(@NonNull myNearbyViewHolder holder, int position)
    {
        String id =dbHelper.getUID();
        float[] resultant = new float[1];
        resultant[0] = 0.0f;
        Location.distanceBetween(currUser[0].getLat()
                , currUser[0].getLongi()
                ,userList.get(position).getLat(),userList.get(position).getLongi(),resultant);

        if (!id.equals(userList.get(position).getUid()) && resultant[0]/10000f<30) {
            resultant[0]=0f;
            holder.setData(userList.get(position).getUsername(), userList.get(position).getLocation()
                    , userList.get(position).getUid());
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT));
            Glide.with(holder.userImage).load(userList.get(position).getImageURL()).into(holder.userImage);
        }
        else {
            resultant[0]=0.0f;
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @NonNull
    @Override
    public myNearbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individualuserview,parent,false);
        return new myNearbyViewHolder(view);
    }
}
