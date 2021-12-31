package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    Button register;
    EditText email;
    EditText username;
    EditText password;
    EditText confPass;
    double lat,longi;
    DBHelper dbHelper;
    FusedLocationProviderClient provider;
    //DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReferenceFromUrl("https://termproject-chatapp-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //bind all data to respective fields
        bindFields();

        //location workings
        if(ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            provider.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location loc = task.getResult();
                    if(loc!=null)
                    {
                        lat = loc.getLatitude();
                        longi = loc.getLongitude();
                    }
                    else {
                         lat = 31.526542688148975;
                         longi = 74.28628631738394;
                    }
                }
            });
        }
        else {
            ActivityCompat.requestPermissions(RegisterActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            ActivityCompat.recreate(RegisterActivity.this);
        }



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

            dbHelper.registerUser(emailtext,passwordtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
                        String add="";
                        try {
                            List<Address> li = geocoder.getFromLocation(lat,longi,1);
                            add = li.get(0).getLocality() + " , " + li.get(0).getAdminArea() + " , " + li.get(0).getCountryName();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        UserModel user = new UserModel(lat,longi,usernametext,add,dbHelper.getUID());
                        //databaseReference.child("users").child(uid).setValue(user);
                        dbHelper.registerUserData(user);
                        //databaseReference.child("users").child("email").child("username").setValue(usernametext);
                        Intent intent = new Intent(RegisterActivity.this,loginActivity.class);
                        intent.putExtra("user",emailtext);
                        intent.putExtra("pass",passwordtext);
                        intent.putExtra("uid",dbHelper.getUID());
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
        provider = LocationServices.getFusedLocationProviderClient(this);
        lat = 31.526542688148975;
        longi = 74.28628631738394;
        dbHelper = new DBHelper();
    }
}