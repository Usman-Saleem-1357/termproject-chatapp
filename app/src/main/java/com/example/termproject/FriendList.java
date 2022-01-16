package com.example.termproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.termproject.Models.ChatModel;
import com.example.termproject.Models.DBHelper;
import com.example.termproject.Models.Friends;
import com.example.termproject.Models.RequestModel;
import com.example.termproject.Models.UserModel;
import com.example.termproject.RecyclerViews.myRequestRecyclerAdapter;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendList extends AppCompatActivity {
    DBHelper dbHelper;
    RecyclerView recyclerView1;
    CircleImageView userImage;
    ImageView backImage;
    TextView username;
    myRequestRecyclerAdapter adapter;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        bindFields();
        recyclerView1 = findViewById(R.id.friendlist);

        username = findViewById(R.id.requests_username);
        userImage = findViewById(R.id.request_user_profile);
        backImage = findViewById(R.id.back_to_all_users);


        Intent intent = getIntent();
        if (intent.hasExtra("uid"))
        {
            uid = intent.getStringExtra("uid");
        }

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(FriendList.this,NearbyUserList.class);
                backIntent.putExtra("uid",uid);
                startActivity(backIntent);
                finish();
            }
        });
        ////////// CURRENT USER DATA //////////////////////////

        dbHelper.firestoreref.collection("Users").whereEqualTo("uid",uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : value){
                    UserModel user = doc.toObject(UserModel.class);
                    if(user.getUsername()!=null){
                        username.setText(user.getUsername());
                        Glide.with(userImage).load(user.getImageURL()).into(userImage);
                    }
                }
            }
        });


        recyclerView1.setVisibility(View.VISIBLE);
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
                                               recyclerView1.setVisibility(View.VISIBLE);
                                               adapter.setData(users);
                                               adapter.notifyDataSetChanged();
                                           }
                                           if (users.isEmpty())
                                           {
                                               recyclerView1.setVisibility(View.GONE);
                                           }
                                        }
                                    });
                        }
                    }
                });

        //////// POPULATE ACCEPTED REQUESTS ///////////////////////////
        dbHelper.firestoreref.collection("Friends").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : value){
                    Friends friend = doc.toObject(Friends.class);
                    String reqID = "";
                    if(friend.getId().contains(uid)){
                        reqID = getReqID(uid,friend.getId());
                    }
                    else if(friend.getReverseid().contains(uid)){
                        reqID = getReqID(uid,friend.getReverseid());
                    }
                    if (!reqID.equals("")){
                        dbHelper.firestoreref.collection("Users")
                                .whereEqualTo("uid",reqID)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                        for ( QueryDocumentSnapshot doc : value){
                                            users.add(doc.toObject(UserModel.class));
                                            recyclerView1.setVisibility(View.VISIBLE);
                                            adapter.setData(users);
                                            adapter.notifyDataSetChanged();
                                        }
                                        if(users.isEmpty()){
                                            recyclerView1.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    String getReqID(String uid,String fullid){
        int index = fullid.indexOf(uid);
        if(index > 0){
            return fullid.substring(0,index);
        }
        else{
            return fullid.substring(uid.length());
        }
    }

    public void bindFields()
    {
        dbHelper = new DBHelper();
    }

    @Override
    public void onBackPressed(){
        Intent backIntent = new Intent(FriendList.this,NearbyUserList.class);
        backIntent.putExtra("uid",uid);
        startActivity(backIntent);
        finish();
    }

}