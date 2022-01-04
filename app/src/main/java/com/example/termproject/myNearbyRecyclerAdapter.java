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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

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
            holder.setData(userList.get(position).getUsername(), userList.get(position).getLocation(), userList.get(position).getUid());
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT));
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
