package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

public class IndividualUserView extends AppCompatActivity {
    DBHelper dbHelper;
    TextView username,location,requestText;
    Button sendreq,cancelreq;
    UserModel user;
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
            Task<QuerySnapshot> data = dbHelper.firestoreref.collection("Users")
                    .whereEqualTo("uid",intent.getStringExtra("uid")).get();
            data.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(QueryDocumentSnapshot doc : task.getResult())
                    {
                        user = doc.toObject(UserModel.class);
                        setData(user);
                    }
                }
            });
        }
        sendreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        cancelreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequest_Curr_usr();
                cancelReq_Other_usr();
            }
        });
    }

    public void reqAlreadySent()
    {
        dbHelper.firestoreref.collection("Users").whereEqualTo("uid",dbHelper.getUID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : value)
                {
                    dbHelper.firestoreref.collection("Users")
                            .document(doc.getId())
                            .collection("Requests")
                            .whereEqualTo("requestedto",user.getUid())
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            for(QueryDocumentSnapshot doc : value)
                            {
                                if (doc.exists())
                                {
                                    sendreq.setVisibility(View.VISIBLE);
                                    cancelreq.setVisibility(View.GONE);
                                }
                                else {
                                    cancelreq.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void cancelRequest_Curr_usr()
    {
        final String[] currDocid = {""};
        dbHelper.firestoreref.collection("Users")
                .whereEqualTo("uid",dbHelper.getUID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    currDocid[0] = doc.getId();
                    dbHelper.firestoreref.collection("Users")
                            .document(doc.getId())
                            .collection("Requests")
                            .whereEqualTo("requestedto", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                dbHelper.firestoreref.collection("Users")
                                        .document(currDocid[0])
                                        .collection("Requests")
                                        .document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        sendreq.setVisibility(View.VISIBLE);
                                        cancelreq.setVisibility(View.GONE);
                                        Toast.makeText(IndividualUserView.this, "Friend Request Canceled", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    public void cancelReq_Other_usr()
    {
        final String[] docID = {""};
        dbHelper.firestoreref.collection("Users")
                .whereEqualTo("uid",user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot doc : task.getResult()) {
                    docID[0] = doc.getId();
                    dbHelper.firestoreref.collection("Users")
                            .document(docID[0])
                            .collection("Requests")
                            .whereEqualTo("requestedby", dbHelper.getUID())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                dbHelper.firestoreref.collection("Users")
                                        .document(docID[0]).collection("Requests")
                                        .document(doc.getId()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                Toast.makeText(IndividualUserView.this
                                                        , "Deleted From Received", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });

    }

    public void sendRequest()
    {
        RequestModel req = new RequestModel(user.getUid(), dbHelper.getUID(),"SENT");
        RequestModel req1 = new RequestModel(user.getUid(),dbHelper.getUID(),"RECEIVED");
        Task<QuerySnapshot> query = dbHelper.firestoreref.collection("Users").whereEqualTo("uid",dbHelper.getUID()).get();
        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot doc : task.getResult())
                {
                    dbHelper.firestoreref.collection("Users").document(doc.getId()).collection("Requests").add(req).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(@NonNull DocumentReference documentReference) {
                            dbHelper.firestoreref.collection("Users")
                                    .whereEqualTo("uid",req.getRequestedto())
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot doc : task.getResult())
                                    {
                                        dbHelper.firestoreref.collection("Users")
                                                .document(doc.getId())
                                                .collection("Requests")
                                                .add(req1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(@NonNull DocumentReference documentReference) {
                                                Toast.makeText(IndividualUserView.this, "REQUEST SENT", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public boolean requestAlreadyReceived()
    {
        final boolean[] choice = {false};
        final String[] docID = {""};

        dbHelper.firestoreref.collection("Users")
                .whereEqualTo("uid",dbHelper.getUID())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot doc : task.getResult()) {
                    docID[0] = doc.getId();
                    dbHelper.firestoreref.collection("Users")
                            .document(docID[0])
                            .collection("Requests")
                            .whereEqualTo("requestedby", user.getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot doc: task.getResult()) {
                                if (doc.exists()) {
                                    cancelreq.setVisibility(View.GONE);
                                    sendreq.setVisibility(View.GONE);
                                    requestText.setVisibility(View.VISIBLE);
                                    choice[0] = true;
                                } else {
                                    cancelreq.setVisibility(View.VISIBLE);
                                    sendreq.setVisibility(View.VISIBLE);
                                    requestText.setVisibility(View.GONE);
                                    choice[0] = false;
                                }

                            }
                            reqAlreadySent();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(IndividualUserView.this, "FAILED", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        /*dbHelper.firestoreref.collection("Users").whereEqualTo("uid",dbHelper.getUID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(QueryDocumentSnapshot doc : value)
                {
                    docID[0] = doc.getId();
                    dbHelper.firestoreref.collection("Users")
                            .document(docID[0])
                            .collection("Requests")
                            .whereEqualTo("requestedby",user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            for (QueryDocumentSnapshot doc: value) {
                                if(doc.exists())
                                {
                                    cancelreq.setVisibility(View.GONE);
                                    sendreq.setVisibility(View.GONE);
                                    requestText.setVisibility(View.VISIBLE);
                                    choice[0] = true;
                                }
                                else{
                                    choice[0]  = false;
                                }
                            }
                        }
                    });
                }
            }
        });*/
        return choice[0];
    }

    public void BindViews()
    {
        requestText = findViewById(R.id.receivedReq);
        username = findViewById(R.id.individualuser);
        location = findViewById(R.id.individuallocation);
        sendreq = findViewById(R.id.sendreq);
        cancelreq = findViewById(R.id.cancelreq);
    }
    public void setData(UserModel user)
    {
        username.setText(user.getUsername());
        location.setText(user.getLocation());
        requestAlreadyReceived();
    }
}