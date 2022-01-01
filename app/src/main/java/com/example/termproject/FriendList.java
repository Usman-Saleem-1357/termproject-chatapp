package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class FriendList extends AppCompatActivity {
    DBHelper dbHelper;
    RecyclerView recyclerView1;
    myRequestRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        bindFields();
        recyclerView1 = findViewById(R.id.friendlist);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        List<UserModel> reqList = dbHelper.getAllRequests();
        adapter = new myRequestRecyclerAdapter(reqList);
        recyclerView1.setAdapter(adapter);
        dbHelper.databaseReference.child("Requests").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter = new myRequestRecyclerAdapter(dbHelper.getAllRequests());
                recyclerView1.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter = new myRequestRecyclerAdapter(dbHelper.getAllRequests());
                recyclerView1.setAdapter(adapter);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                adapter = new myRequestRecyclerAdapter(dbHelper.getAllRequests());
                recyclerView1.setAdapter(adapter);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void bindFields()
    {
        dbHelper = new DBHelper();
    }

}