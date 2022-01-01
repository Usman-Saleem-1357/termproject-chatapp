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
import com.google.android.gms.tasks.Task;

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
            while (user == null) {
                user = dbHelper.getUserData(intent.getStringExtra("uid"));
            }
            setData(user);
        }

        UserModel finalUser = user;
        sendreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestModel req = new RequestModel(user.uid);
                dbHelper.sendRequest(req).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(IndividualUserView.this, "Request sent", Toast.LENGTH_SHORT).show();
                        //sendreq.setActivated(false);
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
        username.setText(user.username);
        location.setText(user.location);
    }
}