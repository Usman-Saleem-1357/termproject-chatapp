package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.termproject.Models.DBHelper;
import com.example.termproject.Models.UserModel;
import com.example.termproject.RecyclerViews.myNearbyRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
        final List<UserModel>[] userdata = new List[]{new ArrayList<>()};
        recyclerView.setHasFixedSize(true);
        //adapter = new myNearbyRecyclerAdapter(userdata[0],new UserModel(0,0,"","",""));
        //recyclerView.setAdapter(adapter);
        Task<QuerySnapshot> col = dbHelper.firestoreref.collection("Users").whereNotEqualTo("uid",dbHelper.getUID()).get();
        Task<QuerySnapshot> col2 = dbHelper.firestoreref.collection("Users").whereEqualTo("uid",dbHelper.getUID()).get();
        final UserModel[] currUser = new UserModel[1];
        col.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                userdata[0] = new ArrayList<>();
                for(QueryDocumentSnapshot doc : task.getResult())
                {
                    userdata[0].add(doc.toObject(UserModel.class));
                }
                col2.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        currUser[0] = task.getResult().toObjects(UserModel.class).get(0);
                        adapter = new myNearbyRecyclerAdapter(userdata[0],currUser[0]);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
        dbHelper.firestoreref.collection("Users").whereNotEqualTo("uid",dbHelper.getUID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                userdata[0] = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value)
                {
                    userdata[0].add(doc.toObject(UserModel.class));
                }
                if (currUser[0]!=null) {
                    adapter.setItems(userdata[0],currUser[0]);
                    adapter.notifyDataSetChanged();
                }
            }
        });
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

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}