package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.termproject.Models.DBHelper;
import com.example.termproject.Models.Friends;
import com.example.termproject.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class IndividualRequestView extends AppCompatActivity {
    CircleImageView userImage;
    TextView username,location;
    DBHelper dbHelper;
    String uid;
    AppCompatButton accept,decline,chat;
    UserModel user = new UserModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_request_view);
        bindViews();
        Intent intent = getIntent();
        if (intent.hasExtra("uid")){
            uid = intent.getStringExtra("uid");
            dbHelper.firestoreref.collection("Users")
                    .whereEqualTo("uid",intent.getStringExtra("uid"))
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (QueryDocumentSnapshot doc : value){
                        user = doc.toObject(UserModel.class);
                        if(user.getUsername()!=null){
                            setUserData(user);
                        }
                    }
                }
            });
        }

        setButtonVisibility(View.VISIBLE,View.VISIBLE,View.GONE);

        Friends friend = new Friends(dbHelper.getUID() + uid,uid + dbHelper.getUID());
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.firestoreref.collection("Friends").add(friend).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            setButtonVisibility(View.GONE,View.GONE,View.VISIBLE);
                        }
                    }
                });
            }
        });

        dbHelper.firestoreref.collection("Friends")
                .whereIn("id", Arrays.asList(friend.getId(),friend.getReverseid()))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot doc : value) {
                            Friends temp = doc.toObject(Friends.class);
                            if (temp.getId() != null) {
                                setButtonVisibility(View.GONE, View.GONE, View.VISIBLE);
                            }
                        }
                    }
                });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(IndividualRequestView.this);
                alert.setMessage("Are you sure you want to Decline request?");
                alert.setTitle("Decline Request");
                alert.setCancelable(false);
                alert.setPositiveButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.firestoreref.collection("Requests")
                                .whereEqualTo("requestedby", uid)
                                .whereEqualTo("requestedto", dbHelper.getUID())
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        for (QueryDocumentSnapshot doc : value) {
                                            dbHelper.firestoreref.collection("Requests")
                                                    .document(doc.getId()).delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(IndividualRequestView.this, "User Deleted", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(IndividualRequestView.this, FriendList.class);
                                                            intent.putExtra("uid", uid);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                });
                AlertDialog dialogue = alert.create();
                dialogue.show();
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getUsername()!=null){
                    Intent chatIntent = new Intent(IndividualRequestView.this,Chatview.class);
                    chatIntent.putExtra("username",user.getUsername());
                    chatIntent.putExtra("imageid",user.getImageURL());
                    chatIntent.putExtra("uid",user.getUid());
                    startActivity(chatIntent);
                }
            }
        });

    }

    public void bindViews(){
        dbHelper = new DBHelper();
        userImage = findViewById(R.id.individual_request_profile);
        username = findViewById(R.id.individual_request_username);
        location = findViewById(R.id.individual_request_location);
        accept = findViewById(R.id.accept_request);
        decline = findViewById(R.id.decline_request);
        chat = findViewById(R.id.to_chat);
    }

    public void setUserData(UserModel user){
        Glide.with(userImage).load(user.getImageURL()).into(userImage);
        username.setText(user.getUsername());
        location.setText(user.getLocation());
    }

    public void setButtonVisibility(int accept,int decline,int chat){
        this.accept.setVisibility(accept);
        this.decline.setVisibility(decline);
        this.chat.setVisibility(chat);
    }
}