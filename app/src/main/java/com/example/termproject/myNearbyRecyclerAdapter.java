package com.example.termproject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

public class myNearbyRecyclerAdapter extends FirebaseRecyclerAdapter<UserModel,myNearbyViewHolder> {

    DBHelper dbHelper = new DBHelper();
    //UserModel currUser;
    public myNearbyRecyclerAdapter(@NonNull FirebaseRecyclerOptions<UserModel> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myNearbyViewHolder holder, int position, @NonNull UserModel model)
    {
        String id =dbHelper.getUID();
        UserModel currUser= null;
        while(currUser == null) {
            currUser = dbHelper.getUserData(id);
        }
        float[] res = new float[1];
        res[0] = 0.0f;
        Location.distanceBetween(currUser.lat,currUser.longi,model.lat,model.longi,res);
        if (!id.equals(model.uid)) {
            holder.setData(model.username, model.location, model.uid);
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    @NonNull
    @Override
    public myNearbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individualuserview,parent,false);
        return new myNearbyViewHolder(view);
    }
}
