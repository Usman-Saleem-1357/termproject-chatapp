package com.example.termproject.RecyclerViews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termproject.Models.DBHelper;
import com.example.termproject.Models.UserModel;
import com.example.termproject.R;

import java.util.List;

public class myRequestRecyclerAdapter extends RecyclerView.Adapter<myRequestViewHolder> {
    DBHelper dbHelper = new DBHelper();
    List<UserModel> requestUser;

    public myRequestRecyclerAdapter(List<UserModel> user)
    {
        requestUser=user;
    }
    @NonNull
    @Override
    public myRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requestview,parent,false);
        return new myRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myRequestViewHolder holder, int position) {
        holder.setData(requestUser.get(position).getUid(),requestUser.get(position).getUsername(),requestUser.get(position).getLocation());
    }
    public void setData(List<UserModel> user)
    {
        requestUser = user;
    }
    @Override
    public int getItemCount() {
        return requestUser.size();
    }
}
