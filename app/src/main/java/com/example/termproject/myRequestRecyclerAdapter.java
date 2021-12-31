package com.example.termproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class myRequestRecyclerAdapter extends FirebaseRecyclerAdapter<RequestModel,myRequestViewHolder> {
    DBHelper dbHelper = new DBHelper();
    public myRequestRecyclerAdapter(@NonNull FirebaseRecyclerOptions<RequestModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myRequestViewHolder holder, int position, @NonNull RequestModel model) {
        UserModel user = dbHelper.getUserData(model.reqid);
        holder.setData(user.uid,user.username,user.location);
    }

    @NonNull
    @Override
    public myRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requestview,parent,false);
        return new myRequestViewHolder(view);
    }
}
