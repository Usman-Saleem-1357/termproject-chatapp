package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.termproject.Models.DBHelper;
import com.example.termproject.Models.Friends;
import com.example.termproject.Models.RequestModel;
import com.example.termproject.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class IndividualUserView extends AppCompatActivity {
    DBHelper dbHelper;
    TextView username,location,requestText,already_friends;
    CircleImageView userImage;
    AppCompatButton sendreq,cancelreq;
    UserModel user;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_user_view);
        dbHelper = new DBHelper();
        user = null;
        BindViews();
        Intent intent = getIntent();
        if(intent.hasExtra("uid"))
        {
            uid = intent.getStringExtra("uid");
            dbHelper.firestoreref.collection("Users")
                    .whereEqualTo("uid",intent.getStringExtra("uid"))
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot doc : value)
                        {
                            user = doc.toObject(UserModel.class);
                            setData(user);
                        }
                    }
                });
        }
        //////////// CHECK IF REQUEST SENT ////////////////////////////////////
        // Allows separation of sent and received checks ////
        final RequestModel[] checker = {new RequestModel()};
        ///////////////////////////////////////////////////
        dbHelper.firestoreref.collection("Requests")
                .whereEqualTo("requestedby", dbHelper.getUID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot doc : value) {
                            RequestModel temp = doc.toObject(RequestModel.class);
                            requestText.setVisibility(View.GONE);
                            already_friends.setVisibility(View.GONE);
                            if (!temp.getRequestedto().equals(user.getUid())) {
                                sendreq.setVisibility(View.VISIBLE);
                                cancelreq.setEnabled(false);
                                cancelreq.setVisibility(View.GONE);
                            } else {
                                checker[0] = temp;
                                cancelreq.setEnabled(true);
                                cancelreq.setVisibility(View.VISIBLE);
                                sendreq.setVisibility(View.GONE);
                                break;
                            }
                        }
                    }
                });

        //////////////////////////////////////////////////////////////////////

        ///////// CHECK IF REQUEST RECEIVED //////////////////////////////////

        dbHelper.firestoreref.collection("Requests")
                .whereEqualTo("requestedto", dbHelper.getUID()).limit(2000)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot doc : value) {
                            if(checker[0].getRequestedby() == null) {
                                RequestModel temp = doc.toObject(RequestModel.class);
                                cancelreq.setVisibility(View.GONE);
                                already_friends.setVisibility(View.GONE);
                                if (!temp.getRequestedby().equals(user.getUid())) {
                                    sendreq.setVisibility(View.VISIBLE);
                                    sendreq.setEnabled(true);
                                    requestText.setVisibility(View.GONE);
                                } else {
                                    sendreq.setEnabled(false);
                                    sendreq.setVisibility(View.GONE);
                                    requestText.setVisibility(View.VISIBLE);
                                    checker[0] = new RequestModel();
                                    break;
                                }
                            }
                        }
                    }
                });

        //////////////////////////////////////////////////////////////////////
        sendreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        cancelreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelrequest(true);
            }
        });


        dbHelper.firestoreref.collection("Friends").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : value){
                    Friends friends = doc.toObject(Friends.class);
                    if (friends.getReverseid()!=null){
                        if(friends.getReverseid().contains(uid) && friends.getReverseid().contains(dbHelper.getUID())){
                            requestText.setVisibility(View.GONE);
                            sendreq.setVisibility(View.GONE);
                            cancelreq.setVisibility(View.GONE);
                            already_friends.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

    }


    public void sendRequest()
    {
        RequestModel req = new RequestModel(user.getUid(),dbHelper.getUID(),"PROCESSING");
        dbHelper.firestoreref.collection("Requests").add(req).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful())
                {
                    sendreq.setEnabled(false);
                    cancelreq.setEnabled(true);
                    sendreq.setVisibility(View.GONE);
                    cancelreq.setVisibility(View.VISIBLE);
                    Toast.makeText(IndividualUserView.this, "Request Sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void cancelrequest(boolean isFired)
    {
        if (isFired) {
            dbHelper.firestoreref.collection("Requests")
                    .whereEqualTo("requestedto", user.getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        RequestModel req = doc.toObject(RequestModel.class);
                        if (req.getRequestedby().equals(dbHelper.getUID()))   //// IF REQUEST WAS SENT BY ME TO SELECTED USER
                        {
                            dbHelper.firestoreref.collection("Requests")
                                    .document(doc.getId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@NonNull Void unused) {
                                            Toast.makeText(IndividualUserView.this, "REQUEST CANCELLED", Toast.LENGTH_SHORT).show();
                                            sendreq.setEnabled(true);
                                            cancelreq.setEnabled(false);
                                            sendreq.setVisibility(View.VISIBLE);
                                            cancelreq.setVisibility(View.GONE);
                                        }
                                    });
                        }
                    }
                }
            });
        }
    }

    public void BindViews()
    {
        already_friends = findViewById(R.id.already_friends);
        requestText = findViewById(R.id.receivedReq);
        username = findViewById(R.id.individualuser);
        location = findViewById(R.id.individuallocation);
        sendreq = findViewById(R.id.sendreq);
        cancelreq = findViewById(R.id.cancelreq);
        userImage = findViewById(R.id.individualPicture);
    }
    public void setData(UserModel user)
    {
        username.setText(user.getUsername());
        location.setText(user.getLocation());
        Glide.with(userImage).load(user.getImageURL()).into(userImage);
    }
}