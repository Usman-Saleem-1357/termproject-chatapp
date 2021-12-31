package com.example.termproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class IndividualUserView extends AppCompatActivity {
    DBHelper dbHelper;
    TextView username,location;
    Button sendreq,cancelreq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_user_view);
        dbHelper = new DBHelper();
        UserModel user = null;
        BindViews();
        Intent intent = getIntent();
        if(intent.hasExtra("uid"))
        {
            while (user == null) {
                user = dbHelper.getUserData(intent.getStringExtra("uid"));
            }
            setData(user);
        }
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