package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.termproject.Models.ChatModel;
import com.example.termproject.Models.DBHelper;
import com.example.termproject.Models.UserModel;
import com.example.termproject.RecyclerViews.ChatRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.type.DateTime;

import java.lang.reflect.Array;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chatview extends AppCompatActivity {
    CircleImageView profile;
    ImageView back,send;
    EditText message_text;
    RecyclerView chat;
    TextView username;
    String u_name,imageurl,uid;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatview);
        bindData();
        Intent intent = getIntent();
        chat.setLayoutManager(new LinearLayoutManager(this));
        if (intent.hasExtra("uid")) {
            u_name = intent.getStringExtra("username");
            imageurl = intent.getStringExtra("imageid");
            uid = intent.getStringExtra("uid");
            setdata(u_name,imageurl);
        }
        List<ChatModel> chatlist = new ArrayList<>();
        ChatRecyclerAdapter adapter = new ChatRecyclerAdapter(chatlist);
        chat.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String text = message_text.getText().toString();
                if(!text.equals("")){
                    ChatModel chat = new ChatModel(text,dbHelper.getUID(),uid, String.valueOf(System.nanoTime()));
                    message_text.setText("");
                    dbHelper.firestoreref.collection("Chat").add(chat).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                        }
                    });
                }
            }
        });

        dbHelper.firestoreref.collection("Chat")
                .orderBy("dateTime")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<ChatModel> chatlist = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value){
                            ChatModel chat1 = doc.toObject(ChatModel.class);
                            if(chat1.getSenderid().equals(dbHelper.getUID()) || chat1.getSenderid().equals(uid)){
                                chatlist.add(chat1);
                            }
                        }
                        adapter.setData(chatlist,uid);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public void setdata(String u_name,String imageurl){
        username.setText(u_name);
        Glide.with(profile).load(imageurl).into(profile);
    }

    public void bindData()
    {
        dbHelper = new DBHelper();
        profile = findViewById(R.id.selected_user_chat_picture);
        back = findViewById(R.id.back_to_chat);
        send = findViewById(R.id.send_message);
        username = findViewById(R.id.chat_username);
        message_text = findViewById(R.id.message);
        chat = findViewById(R.id.chat_recycler_view);
    }
}