package com.example.termproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NearbyUserList extends AppCompatActivity {
    DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://termproject-chatapp-default-rtdb.firebaseio.com/");
    RecyclerView recyclerView;
    DBHelper dbHelper;
    myNearbyRecyclerAdapter adapter;
    FloatingActionButton toReq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_user_list);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        dbHelper = new DBHelper();
        String uid = intent.getStringExtra("uid");
        FirebaseRecyclerOptions<UserModel> options = dbHelper.getUserOptions();
        adapter = new myNearbyRecyclerAdapter(options);
        recyclerView.setAdapter(adapter);
        toReq = findViewById(R.id.toRequests);
        toReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NearbyUserList.this,FriendList.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}