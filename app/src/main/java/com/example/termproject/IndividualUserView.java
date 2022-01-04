package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

public class IndividualUserView extends AppCompatActivity {
    DBHelper dbHelper;
    TextView username,location;
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
        });
    }
    public void BindViews()
    {
        username = findViewById(R.id.individualuser);
        location = findViewById(R.id.individuallocation);
        sendreq = findViewById(R.id.sendreq);
        cancelreq = findViewById(R.id.cancelreq);
    }
    public void setData(UserModel user)
    {
        username.setText(user.getUsername());
        location.setText(user.getLocation());
    }
}