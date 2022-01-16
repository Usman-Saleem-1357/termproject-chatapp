package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.termproject.Models.DBHelper;
import com.example.termproject.Models.UserModel;
import com.example.termproject.RecyclerViews.myNearbyRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NearbyUserList extends AppCompatActivity {
    RecyclerView recyclerView;
    DBHelper dbHelper;
    CircleImageView userImage;
    ImageView logoutImage;
    TextView username;
    myNearbyRecyclerAdapter adapter;
    FloatingActionButton toReq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_user_list);

        recyclerView = findViewById(R.id.recyclerview);
        userImage = findViewById(R.id.login_user_pic);
        username = findViewById(R.id.login_user_name);
        logoutImage = findViewById(R.id.logout_user);

        logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setAlertDialogue();
            }
        });

        ///////// LOGOUT WHEN BACK PRESSED HERE/////////////////////
        //onBackPressed();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        dbHelper = new DBHelper();
        String uid = intent.getStringExtra("uid");


        ////////// CURRENT USER DATA //////////////////////////

        dbHelper.firestoreref.collection("Users").whereEqualTo("uid",uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : value){
                    UserModel user = doc.toObject(UserModel.class);
                    if(user.getUsername()!=null){
                        username.setText(user.getUsername());
                        Glide.with(userImage).load(user.getImageURL()).into(userImage);
                    }
                }
            }
        });






        final List<UserModel>[] userdata = new List[]{new ArrayList<>()};
        recyclerView.setHasFixedSize(true);
        Task<QuerySnapshot> col = dbHelper.firestoreref.collection("Users").whereNotEqualTo("uid",dbHelper.getUID()).get();
        Task<QuerySnapshot> col2 = dbHelper.firestoreref.collection("Users").whereEqualTo("uid",dbHelper.getUID()).get();
        final UserModel[] currUser = new UserModel[1];
        col.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                userdata[0] = new ArrayList<>();
                for(QueryDocumentSnapshot doc : task.getResult())
                {
                    userdata[0].add(doc.toObject(UserModel.class));
                }
                col2.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        currUser[0] = task.getResult().toObjects(UserModel.class).get(0);
                        adapter = new myNearbyRecyclerAdapter(userdata[0],currUser[0]);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
        dbHelper.firestoreref.collection("Users").whereNotEqualTo("uid",dbHelper.getUID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                userdata[0] = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value)
                {
                    userdata[0].add(doc.toObject(UserModel.class));
                }
                if (currUser[0]!=null) {
                    adapter.setItems(userdata[0],currUser[0]);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        toReq = findViewById(R.id.toRequests);
        toReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NearbyUserList.this,FriendList.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed(){
       setAlertDialogue();
    }

    public void setAlertDialogue(){
        AlertDialog.Builder alert = new AlertDialog.Builder(NearbyUserList.this);
        alert.setMessage("Are you sure you want to logout?");
        alert.setTitle("Logout?");
        alert.setCancelable(false);
        alert.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent logoutIntent = new Intent(NearbyUserList.this,loginActivity.class);
                dbHelper.fAuth.signOut();
                startActivity(logoutIntent);
                finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NearbyUserList.this, "Logout Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialogue = alert.create();
        dialogue.show();

    }

}