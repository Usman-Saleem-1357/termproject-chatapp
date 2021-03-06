package com.example.termproject.Models;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper{
    public DatabaseReference databaseReference;
    public FirebaseStorage firebaseStorage;
    public StorageReference storageReference;
    public FirebaseFirestore firestoreref = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth;
    public DBHelper()
    {
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://termproject-chatapp-default-rtdb.firebaseio.com/");
        fAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public boolean registerUserData(UserModel user)
    {
        CollectionReference doc = firestoreref.collection("Users");
        Map<String,Object> map = new HashMap<>();
        map.put("lat",user.getLat());
        map.put("longi",user.getLongi());
        map.put("username",user.getUsername());
        map.put("location",user.getLocation());
        map.put("uid",user.getUid());
        doc.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(@NonNull DocumentReference documentReference) {

            }
        });
        return true;
    }

    public FirestoreRecyclerOptions<UserModel> UserOptions()
    {
        Query query = firestoreref.collection("users").orderBy("username",Query.Direction.DESCENDING).limit(100);
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(firestoreref.collection("users"), UserModel.class).build();
        return options;
    }

    public List<UserModel> getAllUsers()
    {
        List<UserModel> usdata = new ArrayList<>();
        firestoreref.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot doc : task.getResult())
                    {
                        UserModel user = doc.toObject(UserModel.class);
                        usdata.add(user);
                    }
                }
            }
        });
        return usdata;
    }

    public FirebaseRecyclerOptions<UserModel> getUserOptions()
    {
        return new FirebaseRecyclerOptions.Builder<UserModel>().setQuery(databaseReference.child("users")
                ,UserModel.class).build();
    }

    public FirebaseRecyclerOptions<RequestModel> getRequestOptions(String uid)
    {
        return new FirebaseRecyclerOptions.Builder<RequestModel>().setQuery(databaseReference.child("Requests").child(getUID())
                ,RequestModel.class).build();
    }


    public Task<AuthResult> loginAuthentication(String username, String password)
    {
        return fAuth.signInWithEmailAndPassword(username, password);
    }

    public Task<AuthResult> registerUser(String username, String password)
    {
        return fAuth.createUserWithEmailAndPassword(username, password);
    }

    public String getUID()
    {
        return fAuth.getCurrentUser().getUid();
    }
}
