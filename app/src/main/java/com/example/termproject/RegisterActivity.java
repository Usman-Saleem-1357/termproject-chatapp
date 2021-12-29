package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    Button register;
    EditText email;
    EditText username;
    EditText password;
    EditText confPass;
    FirebaseAuth fAuth;
    DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReferenceFromUrl("https://termproject-chatapp-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindFields();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               registerAction();
            }
        });
        TextView txtview = findViewById(R.id.tologin);
        txtview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(RegisterActivity.this,loginActivity.class);
                startActivity(in);
            }
        });
    }

    public void registerAction()
    {
        String emailtext = email.getText().toString().trim();
        String usernametext = username.getText().toString();
        String passwordtext = password.getText().toString().trim();
        String confPassword = confPass.getText().toString().trim();
        if (emailtext.isEmpty() || usernametext.isEmpty() || passwordtext.isEmpty() || confPassword.isEmpty()
                || !passwordtext.equals(confPassword))
        {
            email.setError("Email Required");
            Toast.makeText(this, "No field Should be empty!", Toast.LENGTH_SHORT).show();
        }
        else {

            fAuth.createUserWithEmailAndPassword(emailtext,passwordtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user1.getUid();
                        UserModel user = new UserModel(usernametext);
                        databaseReference.child("users").child(uid).setValue(user);
                        //databaseReference.child("users").child("email").child("username").setValue(usernametext);
                        Intent intent = new Intent(RegisterActivity.this,loginActivity.class);
                        intent.putExtra("user",emailtext);
                        intent.putExtra("pass",passwordtext);
                        intent.putExtra("uid",uid);
                        startActivity(intent);
                        finish();
                    }
                    else
                        {
                            Toast.makeText(RegisterActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                }
            });
        }

    }

    public void bindFields()
    {
        register = findViewById(R.id.register);
        email = findViewById(R.id.email);
        username = findViewById(R.id.regUserName);
        password = findViewById(R.id.regPassword);
        confPass = findViewById(R.id.confPass);
        fAuth = FirebaseAuth.getInstance();
    }
}