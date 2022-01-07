package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
        dbHelper.firestoreref.collection("Users")
                .whereEqualTo("uid",dbHelper.getUID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot doc : value)
                        {
                            UserModel user = doc.toObject(UserModel.class);
                            dbHelper.firestoreref.collection("Users")
                                    .document(doc.getId())
                                    .collection("Requests")
                                    .whereEqualTo("status","RECEIVED").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    List<RequestModel> requests = new ArrayList<>();
                                    for (QueryDocumentSnapshot doc : value)
                                    {
                                        requests.add(doc.toObject(RequestModel.class));
                                    }
                                    for (RequestModel req : requests) {
                                        dbHelper.firestoreref.collection("Users")
                                                .whereEqualTo("uid", req.getRequestedby()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                for(QueryDocumentSnapshot doc : value)
                                                {
                                                    users.add(doc.toObject(UserModel.class));
                                                    adapter.setData(users);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
                /*.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot doc : task.getResult())
                {
                    UserModel user = doc.toObject(UserModel.class);
                    dbHelper.firestoreref.collection("Users")
                            .document(doc.getId())
                            .collection("Requests").whereEqualTo("status","RECEIVED")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<RequestModel> requests = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult())
                            {
                                requests.add(doc.toObject(RequestModel.class));
                            }
                            for (RequestModel req : requests)
                            {
                                dbHelper.firestoreref.collection("Users")
                                        .whereEqualTo("uid",req.getRequestedby())
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for(QueryDocumentSnapshot doc : task.getResult())
                                                {
                                                    users.add(doc.toObject(UserModel.class));
                                                    adapter.setData(users);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                            }
                            adapter = new myRequestRecyclerAdapter(users);
                            recyclerView1.setAdapter(adapter);
                        }
                    });
                }
            }
        });*/

    }

    public void bindFields()
    {
        dbHelper = new DBHelper();
    }

}