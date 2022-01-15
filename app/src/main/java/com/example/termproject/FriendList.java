package com.example.termproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.termproject.Models.DBHelper;
import com.example.termproject.Models.RequestModel;
import com.example.termproject.Models.UserModel;
import com.example.termproject.RecyclerViews.myRequestRecyclerAdapter;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
        List<UserModel> users = new ArrayList<>();
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        adapter = new myRequestRecyclerAdapter(users);
        recyclerView1.setAdapter(adapter);

        //////// POPULATE RECEIVED REQUESTS //////////////////////////////////

        dbHelper.firestoreref.collection("Requests")
                .whereEqualTo("requestedto",dbHelper.getUID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<UserModel> users = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value)
                        {
                            RequestModel req = doc.toObject(RequestModel.class);
                            dbHelper.firestoreref.collection("Users")
                                    .whereEqualTo("uid",req.getRequestedby())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                           for(QueryDocumentSnapshot doc1 : value)
                                           {
                                               users.add(doc1.toObject(UserModel.class));
                                               adapter.setData(users);
                                               adapter.notifyDataSetChanged();
                                           }
                                        }
                                    });
                        }
                    }
                });

    }

    public void bindFields()
    {
        dbHelper = new DBHelper();
    }

}