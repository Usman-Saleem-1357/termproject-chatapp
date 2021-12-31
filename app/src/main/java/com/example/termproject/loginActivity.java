package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class loginActivity extends AppCompatActivity {
    String username,password,uid;
    EditText userfield,passwordField;
    Button login;
    DBHelper dbHelper;
    //DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReferenceFromUrl("https://termproject-chatapp-default-rtdb.firebaseio.com/");
    //FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindFields();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Intent intent1 = new Intent(loginActivity.this,NearbyUserList.class);
                    intent1.putExtra("uid",dbHelper.getUID());
                    startActivity(intent1);
                }
                else {
                    Toast.makeText(loginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}