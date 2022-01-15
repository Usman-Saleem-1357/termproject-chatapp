package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.termproject.Models.DBHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class loginActivity extends AppCompatActivity {
    String username,password,uid;
    EditText userfield,passwordField;
    Button login;
    DBHelper dbHelper;
    ProgressDialog progressBar;
    //DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReferenceFromUrl("https://termproject-chatapp-default-rtdb.firebaseio.com/");
    //FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindFields();
        progressBar = new ProgressDialog(this);
        progressBar.setTitle("Logging In Please Wait");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.show();
                loginUser();
            }
        });
    }
    public void bindFields()
    {
        userfield = findViewById(R.id.loginEmail);
        passwordField = findViewById(R.id.loginPassword);
        dbHelper = new DBHelper();
        Intent intent = getIntent();
        if(intent.getData()!=null) {
            username = intent.getStringExtra("user");
            password = intent.getStringExtra("pass");
            uid = intent.getStringExtra("uid");
            userfield.setText(username);
            passwordField.setText(password);
        }
        login = findViewById(R.id.login);
    }
    public void loginUser()
    {
        username = userfield.getText().toString();
        password = passwordField.getText().toString();
        dbHelper.loginAuthentication(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressBar.dismiss();
                    Intent intent1 = new Intent(loginActivity.this,NearbyUserList.class);
                    intent1.putExtra("uid",dbHelper.getUID());
                    startActivity(intent1);
                }
                else {
                    progressBar.dismiss();
                    Toast.makeText(loginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}