package com.example.termproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

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
        holder.setData(requestUser.get(position).uid,requestUser.get(position).username,requestUser.get(position).location);
    }

    @Override
    public int getItemCount() {
        return requestUser.size();
    }
}
